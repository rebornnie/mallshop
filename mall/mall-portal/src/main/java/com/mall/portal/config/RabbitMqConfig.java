package com.mall.portal.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.QueueBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMqConfig {

    @Bean
    public DirectExchange orderDelayExchange() {
        return new DirectExchange("order.delay.exchange");
    }

    @Bean
    public Queue orderDelayQueue() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-message-ttl", 1800000);
        args.put("x-dead-letter-exchange", "order.close.exchange");
        args.put("x-dead-letter-routing-key", "order.close");
        return QueueBuilder.durable("order.delay.queue").withArguments(args).build();
    }

    @Bean
    public DirectExchange orderCloseExchange() {
        return new DirectExchange("order.close.exchange");
    }

    @Bean
    public Queue orderCloseQueue() {
        return QueueBuilder.durable("order.close.queue").build();
    }

    @Bean
    public Binding orderDelayBinding() {
        return BindingBuilder.bind(orderDelayQueue()).to(orderDelayExchange()).with("order.delay");
    }

    @Bean
    public Binding orderCloseBinding() {
        return BindingBuilder.bind(orderCloseQueue()).to(orderCloseExchange()).with("order.close");
    }
}
