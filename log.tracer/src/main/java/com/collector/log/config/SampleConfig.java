package com.collector.log.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;



public class SampleConfig {

    private static final String EXCHANGE_NAME = "sample.exchange";

    private static final String QUEUE_NAME = "sample.queue";

    private static final String ROUTING_KEY = "sample.logcollector.#";

    /*
     * RabbitMQ server가 종료(어떤 이유에서든지)후 재가동하면,
     * 기본적으로 Queue는 모두 제거된다. 따라서 durable true로 설정해 메시지가 보존되게 함.
     */
    private static final boolean DURABLE = true;

    /*
     * 마지막 Consumer 가 구독을 끝내는 경우 자동으로 삭제된다.
     */
    private static final boolean AUTO_DELETE = true;

    @Bean
    TopicExchange exchange() {
        TopicExchange x = new TopicExchange(EXCHANGE_NAME);
        return x;
    }

    @Bean
    Queue queue() {
        return new Queue(QUEUE_NAME);
    }

    @Bean
    Binding binding (Queue queue, TopicExchange exchange) {
        return BindingBuilder.bind(queue).to(exchange).with(ROUTING_KEY);
    }

    @Bean
    RabbitTemplate rabbitTemplate(ConnectionFactory connectionFactory, MessageConverter messageConverter) {
        RabbitTemplate rabbitTemplate = new RabbitTemplate(connectionFactory);
        rabbitTemplate.setMessageConverter(new Jackson2JsonMessageConverter());
        return rabbitTemplate;
    }

}
