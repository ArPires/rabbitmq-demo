package com.example.producer.entrypoint;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.connection.CorrelationData.Confirm;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ExecutionException;

@RestController
@Slf4j
public class TestController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Autowired
    private Queue mainQueue;

    @PostMapping(value = "/test")
    public void testApi(@RequestBody String messageDto) throws ExecutionException, InterruptedException {

        MessageProperties messageProperties = new MessageProperties();
        messageProperties.setHeader("x-retry-count", 0);
        messageProperties.setContentType("application/json");
        Message message = new Message(messageDto.getBytes(), messageProperties);

        log.info("Message received: \n" + messageDto);

        sendMessage(message);
    }

    private void sendMessage(Message message) throws ExecutionException, InterruptedException {
        setupCallbacks();

        CorrelationData correlationData = new CorrelationData("test");
        this.rabbitTemplate.send("", mainQueue.getName(), message, correlationData);
        Confirm confirm = correlationData.getFuture().get();

        log.info("Confirm received, ack = " + confirm.isAck());
    }

    private void setupCallbacks() {
        this.rabbitTemplate.setConfirmCallback((correlationData, ack, reason) -> {
            if (correlationData != null) {
                log.info("Received: " + (ack ? "ack" : "nack") + " for correlation: " + correlationData);
            }
        });
        this.rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            log.info("Returned: " + message +
                    "\nreplyCode: " + replyCode +
                    "\nreplyText: " + replyText +
                    "\nexchange: " + exchange +
                    "\nroutingKey: " + routingKey);
        });
    }

}
