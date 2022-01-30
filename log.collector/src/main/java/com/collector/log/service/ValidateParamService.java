package com.collector.log.service;


import com.collector.log.handler.CustomErrorCode;
import com.collector.log.handler.ParameterException;
import org.springframework.data.domain.Pageable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Arrays;

// 사용자 직접 입력 값 유효성 검사
public class ValidateParamService {

    public boolean validateOrder(String order, String type) {
        // 정렬 조건은 type 마다 다르다.
        String[] COUNT_TAGID_SET;
        switch (type) {
            case "createAt":
                COUNT_TAGID_SET = new String[]{
                        "createAt",
                        "-createAt"
                };
                break;
            case "regDate":
                COUNT_TAGID_SET = new String[]{
                        "regDate",
                        "-regDate"
                };
                break;

            case "tagId":
                COUNT_TAGID_SET = new String[]{
                        "tagId",
                        "-tagId"
                };
                break;

            case "loggingTime":
                COUNT_TAGID_SET = new String[]{
                        "loggingTime",
                        "-loggingTime"
                };
                break;

            case "logType":
                COUNT_TAGID_SET = new String[]{
                        "logType",
                        "-logType"
                };
                break;

            case "errorOccurCount-tagId":

                COUNT_TAGID_SET = new String[]{
                        "errorOccurCount",
                        "tagId",
                        "-errorOccurCount",
                        "-tagId",
                        "errorOccurCount,tagId",
                        "errorOccurCount,-tagId",
                        "-errorOccurCount,tagId",
                        "-errorOccurCount,-tagId",
                        "tagId,errorOccurCount",
                        "tagId,-errorOccurCount",
                        "-tagId,errorOccurCount",
                        "-tagId,-errorOccurCount"
                };
                break;

            case "tagId-processTime":
                COUNT_TAGID_SET = new String[]{
                        "tagId",
                        "processTime",
                        "-tagId",
                        "-processTime",
                        "tagId,processTime",
                        "tagId,-processTime",
                        "-tagId,processTime",
                        "-tagId,-processTime",
                        "processTime,tagId",
                        "processTime,-tagId",
                        "-processTime,tagId",
                        "-processTime,-tagId"
                };
                break;

            default:
                throw new RuntimeException("ValidateParamService - validateOrder() - switch type wasn't matched with any case.");
        }
        if(!Arrays.asList(COUNT_TAGID_SET).contains(order)) {
            throw new ParameterException(CustomErrorCode.INVALID_ORDER_PARAMETER );
        }else {
            return true;
        }
    }


    public boolean validateLoggingDateValue(LocalDate localDate) {
        if(localDate.isAfter(LocalDate.now())) {
            // 조회 날짜는 now() 날짜와 같거나 과거여야 한다.
            throw new ParameterException(CustomErrorCode.INVALID_SEARCH_DATE_PARAMETER);
        }
        return true;
    }


    public boolean validatePageable(Pageable pageable) {
        // 페이지 번호, 사이즈는 양수 값이다. 만약 서비스단위에서 체크하지 못하더라도 pageNumber 0 인 경우, 리포지터리 단위에서 관련 예외를 발생시킨다.
        if(pageable.getPageSize() <= -1 || pageable.getPageNumber() <= -1) {
            throw new ParameterException(CustomErrorCode.INVALID_PAGE_PARAMETER);
        }
        return true;
    }


    public boolean validateEmpty(String value) {
        // 빈문자열 인지, null 값인지
        if(value == null || value.length() == 0) {
            throw new ParameterException(CustomErrorCode.NULL_PARAMETER);
        }
        return true;
    }


    public boolean validateRegDate(LocalDate regDate) {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            dateFormat.setLenient(false);
            dateFormat.parse(String.valueOf(regDate));
            return true;
        } catch (ParseException ex) {
            throw new ParameterException(CustomErrorCode.INVALID_SEARCH_DATE_PARAMETER);
        }
    }


    public boolean validateRegHour(int regHour) {
        if(regHour < 0 || regHour > 24) {
            throw new ParameterException(CustomErrorCode.INVALID_SEARCH_HOUR_PARAMETER);
        }
        return true;
    }

}
