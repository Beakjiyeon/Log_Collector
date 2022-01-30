package com.collector.log.service;


import com.collector.log.config.ConstantYml;
import com.collector.log.domain.*;
import com.collector.log.dto.UndeliveredMailDto;
import com.collector.log.handler.CustomErrorCode;
import com.collector.log.handler.CustomResultMessage;
import com.collector.log.handler.TargetException;
import com.collector.log.service.ValidateParamService;
import com.querydsl.core.QueryResults;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;


@Slf4j
@AllArgsConstructor
@Service("v2.EmailService")
public class EmailService {

    private final JavaMailSender emailSender;
    private final UndeliveredMailRepository undeliveredMailRepository;
    private final UndeliveredMailRepositoryImpl undeliveredMailRepositoryImpl;
    private final ValidateParamService validation = new ValidateParamService();
    private final ConstantYml constantYml;

    @Async
    public Object sendMail(TracedLog entity) {
        SimpleMailMessage message = makeMailMessage(entity);
        try {
            emailSender.send(message);
        } catch (MailException e) {
            // 전송 실패 정보 DB 저장
            UndeliveredMail undeliveredMail = UndeliveredMail.builder()
                    .causeKeyword(getExceptionKeyword(e))
                    .causeContent(e.getCause().getLocalizedMessage())
                    .tracedLog(entity)
                    .build();
            return undeliveredMailRepository.save(undeliveredMail);
        }
        return new AsyncResult<>(CustomResultMessage.MAIL_OK.getMessage());
    }


    public Page<UndeliveredMailDto> getFailedMails(Map<String, Object> searchVariable, String order, Pageable pageable) {
        validation.validatePageable(pageable);
        validation.validateOrder(order,"createAt");
        validation.validateLoggingDateValue((LocalDate) searchVariable.get("regDate"));

        QueryResults<UndeliveredMailDto> rawResult = undeliveredMailRepositoryImpl.findMails(searchVariable, order, pageable);

//        List<UndeliveredMailDto> result = Optional.of(rawResult.getResults())
//                .filter(a -> !a.isEmpty())
//                .orElseThrow(() -> new TargetException(CustomErrorCode.TARGET_NOT_FOUND));
        List<UndeliveredMailDto> result = rawResult.getResults();

        return new PageImpl<>(result, pageable, rawResult.getTotal());
    }


    private String getExceptionKeyword(MailException e) {
        String[] exNames = e.getClass().toString().split("\\.");
        return exNames[exNames.length - 1].substring(0, exNames[exNames.length - 1].length() - 9);
    }


    private SimpleMailMessage makeMailMessage(TracedLog entity) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(constantYml.getReceiver());
        message.setSubject("[LogCollector] Error occurred.");
        String mailContent = "아래 내용과 같은 ERROR 타입 로그가 수집되었습니다.\n\n"
                + "- 로깅 일자 : " + entity.getLoggingTime().toLocalDate() + "\n"
                + "- 로깅 일시 : " + entity.getLoggingTime().toLocalTime() + "\n"
                + "- 추적 ID : " + entity.getTraceId() + "\n"
                + "- 서비스 이름 : " + entity.getTagId() + "\n"
                + "- 로그 내용 : " + entity.getLogData() + "\n";
        message.setText(mailContent);
        return message;
    }

}
