package com.collector.log.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync // spring 비동기 기능 활성화
public class AsyncConfig extends AsyncConfigurerSupport {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor(); // 비동기로 호출하는 Thread 설정
        executor.setCorePoolSize(2); // 기본적으로 실행 대기 중인 Thread 개수
        executor.setMaxPoolSize(10); // 동시에 동작하는 최대 Thread 개수
        executor.setQueueCapacity(500); // MaxPoolSize 를 초과하는 요청이 들어오면 Queue 에 저장했다가 자리가 생기면 꺼내서 실행된다. (500개까지 저장함)
        executor.setThreadNamePrefix("MailExecutor-"); // Spring 에서 생성하는 Thread 이름의 접두사
        executor.initialize();
        return executor;
        // 참고: https://blog.huiya.me/15 [by HuiYa]
    }

}

