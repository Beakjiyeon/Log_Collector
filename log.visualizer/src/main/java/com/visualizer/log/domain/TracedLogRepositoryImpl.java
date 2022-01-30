
package com.visualizer.log.domain;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.core.types.dsl.*;
import com.querydsl.jpa.impl.JPAQueryFactory;


import com.visualizer.log.dto.ErrorServiceDto;
import com.visualizer.log.dto.TracedLogDto;
import com.visualizer.log.dto.ServiceCallCountDto;
import com.visualizer.log.dto.ServiceProcessTimeInTraceIdDto;
import com.visualizer.log.handler.CustomErrorCode;
import com.visualizer.log.handler.ParameterException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;


@RequiredArgsConstructor
@Repository
public class TracedLogRepositoryImpl {

    private final JPAQueryFactory queryFactory;
    private QTracedLog qTracedLog = QTracedLog.tracedLog;


    public QueryResults<ErrorServiceDto> findErrorOccurServicesByLogType(LocalDate searchDate, LinkedHashMap<String, String> orderInfo, Pageable pageable) {
        return queryFactory
                .from(qTracedLog)
                .where(qTracedLog.logType.eq("ERROR").and(qTracedLog.loggingDate.eq(searchDate)))
                .groupBy(qTracedLog.tagId)
                .select(Projections.fields(ErrorServiceDto.class,
                        qTracedLog.tagId,
                        qTracedLog.tagId.count().as("errorOccurCount")))
                .orderBy(getOrderValue(orderInfo, getFirst(orderInfo)), getOrderValue(orderInfo, getLast(orderInfo)))
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
    }

    // 각 traceId 안에서 각 서비스들의 처리 시간
    public List<ServiceProcessTimeInTraceIdDto> findServiceProcessTime(LocalDate searchDate) {
        return queryFactory.from(qTracedLog)
                .where(qTracedLog.loggingDate.eq(searchDate))
                .groupBy(qTracedLog.traceId, qTracedLog.tagId)
                .select(Projections.fields(ServiceProcessTimeInTraceIdDto.class,
                        qTracedLog.tagId,
                        Expressions.timeTemplate(Long.class, "timestampdiff(microsecond, {0}, {1})", qTracedLog.loggingTime.max(), qTracedLog.loggingTime.min()).as("processTime")
                )).fetch();
    }


    public List<ServiceCallCountDto> findServicesCallCount(LocalDate loggingDate) {
        OrderSpecifier<Long> orderSpecifier = Expressions.numberPath(Long.class, "serviceCallCount").desc();
        /*
         QueryDsl은 인라인뷰(from Sub query)를 지원하지 않기 때문에 db에서 1차로 인라인뷰에 해당하는 데이터를 가져온 다음에 서버단위에서 그룹핑 처리를 진행한다.
        - 리포지터리 단위 : 서비스별 호출횟수 데이터 리턴
        - 서비스 단위 : max count 를 가진 서비스 계산
         */

        return queryFactory.from(qTracedLog)
                .groupBy(qTracedLog.tagId)
                .where(qTracedLog.loggingDate.eq(loggingDate))
                .select(Projections.fields(ServiceCallCountDto.class, qTracedLog.tagId, qTracedLog.tagId.count().as("serviceCallCount")))
                .orderBy(orderSpecifier)
                // 중복 max count 를 가진 서비스를 모두 가져오기 위해 limit 쿼리를 사용하지 않음
                .fetch();
    }



    public QueryResults<TracedLogDto> findLogsByTraceId(Map<String, Object> searchVariable, LinkedHashMap<String, String> orderInfo, String orderKey, Pageable pageable) {

        OrderSpecifier<?> orderSpecifier = getOrderValue(orderInfo, orderKey);
        QueryResults<TracedLogDto> result = queryFactory.from(qTracedLog)
                .where(qTracedLog.traceId.eq((String) searchVariable.get("traceId"))
                        .and(qTracedLog.tagId.contains((String) searchVariable.get("tagId"))) // tagId 디폴트 값 :  "" = Param 미입력할 경우 모든 row 리턴
                        .and(qTracedLog.logType.contains((String) searchVariable.get("logType")))
                        .and(qTracedLog.logData.contains((String) searchVariable.get("logData")))
                        //.and(qTracedLog.loggingDate.eq((LocalDate) searchVariable.get("loggingDate")))
                )
                .select(Projections.fields(TracedLogDto.class,
                        qTracedLog.traceId,
                        qTracedLog.tagId,
                        qTracedLog.logType,
                        qTracedLog.logData,
                        qTracedLog.loggingTime))
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return result;
    }

    public QueryResults<TracedLogDto> findLogsByTagId(Map<String, Object> searchVariable, LinkedHashMap<String, String> orderInfo, String orderKey, Pageable pageable) {
        OrderSpecifier<?> orderSpecifier = getOrderValue(orderInfo, orderKey);
        QueryResults<TracedLogDto> result = queryFactory.from(qTracedLog)
                .where(qTracedLog.tagId.eq((String) searchVariable.get("tagId"))
                        .and(qTracedLog.logType.contains((String) searchVariable.get("logType"))) // logType 디폴트 값 :  "" = Param 미입력할 경우 모든 row 리턴
                        .and(qTracedLog.logData.contains((String) searchVariable.get("logData")))
                        .and(qTracedLog.loggingDate.eq((LocalDate) searchVariable.get("loggingDate")))
                )
                .select(Projections.fields(TracedLogDto.class,
                        qTracedLog.traceId,
                        qTracedLog.tagId,
                        qTracedLog.logType,
                        qTracedLog.logData,
                        qTracedLog.loggingTime,
                        qTracedLog.createAt))
                .orderBy(orderSpecifier, Expressions.numberPath(Long.class, "id").asc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetchResults();
        return result;
    }

    // order Param 이 여러개가 오는 경우에도 각 기준 orderSpecifier 을 각각 생성하기 때문에 인자로 orderInfo 를 받음
    private OrderSpecifier<?> getOrderValue(LinkedHashMap<String, String> orderInfo, String orderKey) {

        boolean flag = "desc".equals(orderInfo.get(orderKey));

        switch (orderKey) {
            case "loggingTime":
                TimePath<LocalDateTime> timePath = Expressions.timePath(LocalDateTime.class, orderKey);
                return flag ? timePath.desc() : timePath.asc();

            case "traceId":
            case "tagId":
            case "logType":
                StringPath stringPath = Expressions.stringPath(orderKey);
                return flag ? stringPath.desc() : stringPath.asc();

            case "errorOccurCount":
                NumberPath<Long> numberPath = Expressions.numberPath(Long.class, orderKey);
                return flag ? numberPath.desc() : numberPath.asc();

            default:
                throw new ParameterException(CustomErrorCode.INVALID_ORDER_PARAMETER);
        }

    }

    // 출처: https://bit.ly/3q1q1B5
    public static String getLast(LinkedHashMap<String, String> lhm) {
        int count = 1;
        for (Map.Entry<String, String> it : lhm.entrySet()) {
            if (count == lhm.size()) {
                Map<String, String> map = new HashMap<>();
                map.put(it.getKey(), it.getValue());
                return it.getKey();
            }
            count++;
        }
        return null;
    }


    public static String getFirst(LinkedHashMap<String, String> lhm)
    {
        int count = 1;
        for (Map.Entry<String, String> it : lhm.entrySet()) {
            if (count == 1) {
                Map<String, String> map = new HashMap<>();
                map.put(it.getKey(), it.getValue());
                return it.getKey();
            }
            count++;
        }
        return null;
    }
}

