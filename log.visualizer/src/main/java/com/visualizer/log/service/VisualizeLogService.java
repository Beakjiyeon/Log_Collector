package com.visualizer.log.service;

import com.querydsl.core.QueryResults;
import com.visualizer.log.domain.TracedLogRepositoryImpl;
import com.visualizer.log.dto.*;
import com.visualizer.log.handler.CustomErrorCode;
import com.visualizer.log.handler.OrderCondition;
import com.visualizer.log.handler.ParameterException;
import com.visualizer.log.handler.TargetException;
import com.visualizer.log.service.ValidateParamService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.*;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import static java.util.stream.Collectors.toList;


@RequiredArgsConstructor
@Service("v2.VisualizeLogService")
public class VisualizeLogService {

    private final TracedLogRepositoryImpl tracedLogRepositoryImpl;
    private final ValidateParamService validation = new ValidateParamService();

    public Page<ErrorServiceDto> getErrorServices(LocalDate loggingDate, String order, Pageable pageable) {
        validation.validateLoggingDateValue(loggingDate);
        validation.validateOrder(order, OrderCondition.ERROR_OCCUR_COUNT_TAG_ID.getMessage());
        // validation.validatePageable(pageable);

        LinkedHashMap<String, String> orderInfo = makeOrderInfo(order);
        QueryResults<ErrorServiceDto> rawResult = tracedLogRepositoryImpl.findErrorOccurServicesByLogType(loggingDate, orderInfo, pageable);
//        List<ErrorServiceDto> optionalResult = Optional.of(rawResult.getResults())
//                .filter(a -> !a.isEmpty())
//                .orElseThrow(() -> new TargetException(CustomErrorCode.TARGET_NOT_FOUND));
        List<ErrorServiceDto> optionalResult = rawResult.getResults();
        return new PageImpl<>(optionalResult, pageable, rawResult.getTotal());
    }


    public Map<String, Object> getAvgProcessTime(LocalDate loggingDate, String order, Pageable pageable) {
        validation.validateLoggingDateValue(loggingDate);
        validation.validateOrder(order, OrderCondition.TAG_ID_PROCESS_TIME.getMessage());
        validation.validatePageable(pageable);

//        List<ServiceProcessTimeInTraceIdDto> minMaxLoggingTimeInTraceId = Optional.of(tracedLogRepositoryImpl.findServiceProcessTime(loggingDate))
//                .filter(a -> !a.isEmpty())
//                .orElseThrow(() -> new TargetException(CustomErrorCode.TARGET_NOT_FOUND));
        List<ServiceProcessTimeInTraceIdDto> minMaxLoggingTimeInTraceId = tracedLogRepositoryImpl.findServiceProcessTime(loggingDate);

        Map<String, Double> groupByTagId = minMaxLoggingTimeInTraceId.stream()
                .collect(Collectors.groupingBy(ServiceProcessTimeInTraceIdDto::getTagId, Collectors.averagingDouble(ServiceProcessTimeInTraceIdDto::getProcessTime)));

        // 서비스별 - 처리시간 목록 생성
        Iterator<String> keys = groupByTagId.keySet().iterator();
        List<ServiceProcessTimeDto> result = new ArrayList<>();
        while (keys.hasNext()) {
            Object key = keys.next();
            result.add(new ServiceProcessTimeDto(key.toString(), Math.abs(groupByTagId.get(key) / 1000000))); // 초 단위
        }

        // 페이징 및 정렬
        List<ServiceAvgProcessTimeDto> res = result.stream()
                .sorted(makeComparator(order))
                .skip(pageable.getPageNumber())
                .limit(pageable.getPageSize())
                .collect(toList())
                .stream()
                .map(p -> new ServiceAvgProcessTimeDto(p.getTagId(), p.getProcessTime()))
                .collect(toList());

        Map<String, Object> map = new HashMap<>();
        map.put("avgProcessTimes", new PageImpl<>(res, pageable, result.size()));
        return map;
    }


    public Map<String, Object> getMaxProcessTimeDiff(LocalDate loggingDate, String order, Pageable pageable) {
        validation.validateLoggingDateValue(loggingDate);
        validation.validateOrder(order, OrderCondition.TAG_ID_PROCESS_TIME.getMessage());
        validation.validatePageable(pageable);

        // 서비스 단위에서 한번 더 group by 하므로 orderInfo 를 리포지터리로 넘겨주지 않음
        // 각 traceId 내에서 서비스별 처리 시간 목록
//        List<ServiceProcessTimeInTraceIdDto> serviceProcessTimes = Optional.of(tracedLogRepositoryImpl.findServiceProcessTime(loggingDate))
//                .filter(a -> !a.isEmpty())
//                .orElseThrow(() -> new TargetException(CustomErrorCode.TARGET_NOT_FOUND));
        List<ServiceProcessTimeInTraceIdDto> serviceProcessTimes = tracedLogRepositoryImpl.findServiceProcessTime(loggingDate);

        // 통계 group by tagId
        Map<Object, DoubleSummaryStatistics> groupByTagId = serviceProcessTimes.stream()
                .collect(Collectors.groupingBy(ServiceProcessTimeInTraceIdDto::getTagId, Collectors.summarizingDouble(ServiceProcessTimeInTraceIdDto::getProcessTime)));

        // 서비스별 max(처리시간) - min(처리시간) 계산
        Iterator<Object> keys = groupByTagId.keySet().iterator();
        List<ServiceProcessTimeDto> result = new ArrayList<>();
        while(keys.hasNext()) {
            Object key = keys.next();
            double diff = groupByTagId.get(key).getMax() / 1000000.0 - groupByTagId.get(key).getMin() / 1000000.0 ;
            result.add(new ServiceProcessTimeDto(key.toString(), Math.abs(diff)));
        }

        List<ServiceMaxProcessTimeDiffDto> res = result
                .stream()
                .sorted(makeComparator(order))
                .skip(pageable.getPageNumber())
                .limit(pageable.getPageSize())
                .collect(toList())
                .stream()
                .map(p -> new ServiceMaxProcessTimeDiffDto(p.getTagId(), p.getProcessTime()))
                .collect(toList());

        PageImpl<ServiceMaxProcessTimeDiffDto> maxProcessTimeDiffs = new PageImpl<>(res, pageable, result.size());
        Map<String, Object> map = new HashMap<>();
        map.put("maxProcessTimeDiffs", maxProcessTimeDiffs);
        return map;
    }


    // 서비스 단위(db 쿼리x)에서 다중 정렬 조건을 동적으로 처리하는 함수
    private Comparator<ServiceProcessTimeDto> makeComparator(String order) {
        Map<String, Comparator<ServiceProcessTimeDto>> map = new HashMap<>();
        map.put("tagId", Comparator.comparing(ServiceProcessTimeDto::getTagId));
        map.put("-tagId", Comparator.comparing(ServiceProcessTimeDto::getTagId).reversed());
        map.put("maxProcessTimeDiff", Comparator.comparing(ServiceProcessTimeDto::getProcessTime));
        map.put("-maxProcessTimeDiff", Comparator.comparing(ServiceProcessTimeDto::getProcessTime).reversed());
        map.put("processTime", Comparator.comparing(ServiceProcessTimeDto::getProcessTime));
        map.put("-processTime", Comparator.comparing(ServiceProcessTimeDto::getProcessTime).reversed());

        Map<String, Function<ServiceProcessTimeDto, ?>> map2 = new HashMap<>();
        map2.put("tagId", ServiceProcessTimeDto::getTagId);
        map2.put("maxProcessTimeDiff", ServiceProcessTimeDto::getProcessTime);
        map2.put("processTime", ServiceProcessTimeDto::getProcessTime);

        String[] orderParse = order.split(",");
        Comparator<ServiceProcessTimeDto> comparator = map.get(orderParse[0]);

        // order은 url parameter
        if(orderParse.length == 1) {
            return comparator;
        }

        for(int i = 1; i < orderParse.length ; i++) {
            if("tagId".equals(orderParse[i]) || "-tagId".equals(orderParse[i])) {
                comparator.thenComparing(ServiceProcessTimeDto::getTagId);
            } else if("maxProcessTimeDiff".equals(orderParse[i])
                    || "-maxProcessTimeDiff".equals(orderParse[i])
                    || "processTime".equals(orderParse[i])
                    || "-processTime".equals(orderParse[i]))  {
                comparator.thenComparing(ServiceProcessTimeDto::getProcessTime);
            } else {
                throw new ParameterException(CustomErrorCode.INVALID_ORDER_PARAMETER);
            }
            if(orderParse[i].startsWith("-")) {
                comparator.reversed();
            }
        }
        return comparator;
    }


    public MostCallServiceDto getMostCallServices(LocalDate loggingDate) {
        validation.validateLoggingDateValue(loggingDate);
//        List<ServiceCallCountDto> list = Optional.of(tracedLogRepositoryImpl.findServicesCallCount(loggingDate))
//                .filter(a -> !a.isEmpty())
//                .orElseThrow(() -> new TargetException(CustomErrorCode.TARGET_NOT_FOUND));
        List<ServiceCallCountDto> list = tracedLogRepositoryImpl.findServicesCallCount(loggingDate);

        List<String> tagIds = new ArrayList<>();
        if(list.size() > 0){
            long maxCount = list.get(0).getServiceCallCount();
            for (ServiceCallCountDto serviceCallCountDto : list) {
                if (serviceCallCountDto.getServiceCallCount() != maxCount) {
                    break; // maxCount 를 가진 tagId 요소 까지만 리턴
                }
                tagIds.add(serviceCallCountDto.getTagId());
            }
            return new MostCallServiceDto(tagIds, maxCount);
        }else{
            return new MostCallServiceDto(tagIds, 0L);
        }
    }


    // 해당 기능에서 order 기준은 1개
    public Map<String, Object> getLogsByTraceId(Map<String, Object> searchVariable, String order, Pageable pageable) {
        validation.validateEmpty((String) searchVariable.get("traceId"));
        validation.validateLoggingDateValue((LocalDate) searchVariable.get("loggingDate"));
        final String ORDER_TYPE = order.startsWith("-") ? order.substring(1) : order;
        validation.validateOrder(order, ORDER_TYPE);
        validation.validatePageable(pageable);

//        QueryResults<TracedLogDto> rawResult = Optional.of(tracedLogRepositoryImpl.findLogsByTraceId(searchVariable, makeOrderInfo(order), ORDER_TYPE, pageable))
//                .filter(a -> !a.isEmpty())
//                .orElseThrow(() -> new TargetException(CustomErrorCode.TARGET_NOT_FOUND));
        QueryResults<TracedLogDto> rawResult = tracedLogRepositoryImpl.findLogsByTraceId(searchVariable, makeOrderInfo(order), ORDER_TYPE, pageable);

        Map<String, Object> map = new HashMap<>();
        map.put("logs", new PageImpl(rawResult.getResults(), pageable, rawResult.getTotal()));
        return map;

    }


    public Map<String, Object> getLogsByTagId(Map<String, Object> searchVariable, String order, Pageable pageable) {
        validation.validateEmpty((String) searchVariable.get("tagId"));
        validation.validateLoggingDateValue((LocalDate) searchVariable.get("loggingDate"));
        validation.validatePageable(pageable);

        final String ORDER_TYPE = order.startsWith("-") ? order.substring(1) : order;
        validation.validateOrder(order, ORDER_TYPE);

//        QueryResults<TracedLogDto> result = Optional.of(tracedLogRepositoryImpl.findLogsByTagId(searchVariable, makeOrderInfo(order), ORDER_TYPE, pageable))
//                .filter(a -> !a.isEmpty())
//                .orElseThrow(() -> new TargetException(CustomErrorCode.TARGET_NOT_FOUND));
        QueryResults<TracedLogDto> result = tracedLogRepositoryImpl.findLogsByTagId(searchVariable, makeOrderInfo(order), ORDER_TYPE, pageable);
        Map<String, Object> map = new HashMap<>();
        map.put("logs", new PageImpl<>(result.getResults(), pageable, result.getTotal()));
        return map;
    }


    // order=-tagId,count 형식의 값을 { "tageId" : "desc", "count" : "asc" } 형태로 전환해주는 함수
    private LinkedHashMap<String, String> makeOrderInfo(String order) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        String[] orderParse = order.split(",");
        for (String s : orderParse) {
            if (s.startsWith("-")) {
                map.put(s.substring(1), "desc");
            } else {
                map.put(s, "asc");
            }
        }
        return map;
    }
}


