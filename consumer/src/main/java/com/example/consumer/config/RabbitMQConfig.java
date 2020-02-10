package com.example.consumer.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class RabbitMQConfig {

    @Bean
    public Queue mainQueue() {
        return new Queue("testQueue");
    }

    @Bean
    public Queue deadLetterQueue() {
        return new Queue("testQueue-dlq");
    }

    @Bean
    public Queue trashQueue() {
        return new Queue("trashQueue");
    }

    @Bean
    public CustomExchange delayedExchange() {
        Map<String, Object> args = new HashMap<>();
        args.put("x-delayed-type","direct");
        return new CustomExchange("requeueExchange","x-delayed-message", true,false ,args);
    }

    @Bean("processJobBinding")
    public Binding binding(Queue mainQueue, CustomExchange delayedExchange) {
        return BindingBuilder.bind(mainQueue).to(delayedExchange).with("requeueRoutingKey").noargs();
    }

    @Bean
    public Jackson2JsonMessageConverter converter() {
        return new Jackson2JsonMessageConverter();
    }

}
