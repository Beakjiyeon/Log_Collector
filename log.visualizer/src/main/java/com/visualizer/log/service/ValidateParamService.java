package com.visualizer.log.service;


import com.visualizer.log.handler.CustomErrorCode;
import com.visualizer.log.handler.ParameterException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Arrays;



public class ValidateParamService {
    // 디폴트 값이 아니라 사용자가 직접 입력했을 때 값 유효성 검사

    public boolean validateLoggingDateValue(LocalDate localDate) {
        // 조회 날짜는 now() 날짜와 같거나 과거여야 한다.
        if(localDate.isAfter(LocalDate.now())) {
            //throw new RuntimeException("유효하지 않은 날짜입니다.");
            throw new ParameterException(CustomErrorCode.INVALID_SEARCH_DATE_PARAMETER);
        }
        return true;
    }

    public boolean validateOrder(String order, String type) {


        // 정렬 조건은 type 마다 다르다.
        String[] COUNT_TAGID_SET;
        switch (type) {

            case "traceId":
                COUNT_TAGID_SET = new String[]{
                        "traceId",
                        "-traceId"
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
                throw new ParameterException(CustomErrorCode.INVALID_ORDER_PARAMETER);
        }
        if(!Arrays.asList(COUNT_TAGID_SET).contains(order)) {
            throw new ParameterException(CustomErrorCode.INVALID_ORDER_PARAMETER);
        }
        return true;
    }


    public boolean validatePageable(Pageable pageable) {
        // 페이지 번호, 사이즈는 양수 값이다.
        if(pageable.getPageSize() <= -1 || pageable.getPageNumber() <= -1) {
            throw new ParameterException(CustomErrorCode.INVALID_PAGE_PARAMETER);
        }
        return true;
    }

    public boolean validatePageablePageNumber(int pageNumber, int dbTotalPages) {
        // 페이지 번호, 사이즈는 양수 값이다.
        if (pageNumber > dbTotalPages) {
            throw new ParameterException(CustomErrorCode.INVALID_PAGE_PARAMETER, "페이지는 총 " + dbTotalPages + "개 입니다.");
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

}
