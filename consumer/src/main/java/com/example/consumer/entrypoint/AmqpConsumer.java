package com.example.consumer.entrypoint;

import com.example.consumer.dto.MessageDto;
import com.example.consumer.service.ThirdPartyService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rabbitmq.client.Channel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.AmqpRejectAndDontRequeueException;
import org.springframework.amqp.core.AcknowledgeMode;
import org.springframework.amqp.core.CustomExchange;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.amqp.RabbitProperties;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;

import java.io.IOException;

@Component
@Slf4j
public class AmqpConsumer {

    private ObjectMapper mapper = new ObjectMapper();

    @Autowired private RabbitTemplate rabbitTemplate;
    @Autowired private CustomExchange delayedExchange;
    @Autowired private ThirdPartyService thirdPartyService;
    @Autowired private Queue mainQueue;
    @Autowired private Queue deadLetterQueue;
    @Autowired private Queue trashQueue;

    @RabbitListener(queues = "testQueue")
    public void consumer(@Payload Message message,
                         @Header("x-retry-count") int retryCount) {

        MessageDto messageDto;
        try {
            messageDto = mapper.readValue(message.getBody(), MessageDto.class);
        } catch (IOException e) {
            log.error("Message is not valid. Sending nack...");
            this.rabbitTemplate.convertAndSend(trashQueue.getName(), message);
            return;
        }

        try {
            thirdPartyService.sendToThirdParty(messageDto.getMessage());
        } catch (ResourceAccessException ex) {
            if (retryCount < 3) {

                retryCount++;

                log.warn("Retry count: " + retryCount);
                log.warn("=======================");

                message.getMessageProperties().setDelay(retryCount * 5000);
                message.getMessageProperties().setHeader("x-retry-count", retryCount);
                message.getMessageProperties().setContentType("application/json");

                this.rabbitTemplate.convertAndSend(delayedExchange.getName(), "requeueRoutingKey", message);

            } else {
                message.getMessageProperties().setHeader("x-retry-count", 0);
                this.rabbitTemplate.convertAndSend(deadLetterQueue.getName(), message);
                log.warn("Sent to Dead Letter Queue");
            }
        }
    }
}
