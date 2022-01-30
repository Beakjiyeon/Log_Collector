package com.collector.log.domain;

import com.collector.log.dto.UndeliveredMailDto;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

@RequiredArgsConstructor
@Repository
public class UndeliveredMailRepositoryImpl {

    private final JPAQueryFactory queryFactory;
    private final QUndeliveredMail qUndeliveredMail = QUndeliveredMail.undeliveredMail;
    private final QTracedLog qTracedLog = QTracedLog.tracedLog;

    public QueryResults<UndeliveredMailDto> findMails(Map<String, Object> searchVariable, String orderInfo, Pageable pageable) {
        OrderSpecifier<LocalDateTime> orderSpecifier = qUndeliveredMail.createAt.asc();

        if("-createAt".equals(orderInfo)) {
            orderSpecifier = qUndeliveredMail.createAt.desc();

        }
        LocalDateTime startValue = ((LocalDate)searchVariable.get("regDate")).atStartOfDay();
        LocalDateTime endValue = ((LocalDate) searchVariable.get("regDate")).atTime(LocalTime.MAX);

        QueryResults<UndeliveredMailDto> rawResult = queryFactory
                .from(qUndeliveredMail)
                .where(qUndeliveredMail.createAt.between(startValue, endValue))
                .innerJoin(qUndeliveredMail.tracedLog, qTracedLog)
                .select(Projections.fields(UndeliveredMailDto.class,
                        qUndeliveredMail.id,
                        qUndeliveredMail.createAt,
                        qUndeliveredMail.causeKeyword,
                        qUndeliveredMail.causeContent,
                        qTracedLog.id.as("logId"),
                        qTracedLog.tagId,
                        qTracedLog.traceId,
                        qTracedLog.logData,
                        qTracedLog.loggingTime
                        // qUndeliveredMail.tracedLog: Json 포맷만 다름
                ))
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset()) // 시작 게시물 번호
                .limit(pageable.getPageSize()) // 보여줄 게시물 개수
                .fetchResults();
        return rawResult;

    }
}


/*
        QueryResults<UndeliveredMailDto> result = queryFactory
                .from(qUndeliveredMail)
                .where(qUndeliveredMail.createAt.between(startValue, endValue))
                .select(Projections.fields(UndeliveredMailDto.class,
                        qUndeliveredMail.id,
                        qUndeliveredMail.createAt,
                        qUndeliveredMail.causeKeyword,
                        qUndeliveredMail.causeContent,
                        qTracedLog.id.as("logId"),
                        qTracedLog.tagId,
                        qTracedLog.traceId,
                        qTracedLog.logData
                        // qTracedLog.loggingTime
                ))
                .orderBy(orderSpecifier)
                .offset(pageable.getOffset()) // 시작 게시물 번호
                .limit(pageable.getPageSize()) // 보여줄 게시물 개수
                .fetchResults();
*/