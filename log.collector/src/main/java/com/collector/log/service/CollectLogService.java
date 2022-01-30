package com.collector.log.service;


import com.collector.log.domain.TracedLog;
import com.collector.log.domain.TracedLogRepository;
import com.collector.log.dto.LogSaveRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;


@Slf4j
@RequiredArgsConstructor
@Service("v2.CollectLogService")
public class CollectLogService {

    private final TracedLogRepository tracedLogRepository;
    private final EmailService emailService;

    @Transactional
    public Map<String, Long> save(final LogSaveRequestDto requestDto){
        TracedLog entity = tracedLogRepository.save(requestDto.toEntity());
        if("ERROR".equals(requestDto.getLogType())) {
            emailService.sendMail(entity);
        }
        Map<String, Long> result = new HashMap<>();
        result.put("id", entity.getId());
        return result;
    }

}
