package com.example.consumer.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ThirdPartyService {

    @Autowired
    private RestTemplate restTemplate;

    public void sendToThirdParty(String message) {

        final String uri = "http://localhost:9997/thirdparty?m=" + message;

        try {
            this.restTemplate.getForEntity(uri, String.class);
        } catch (ResourceAccessException ex) {
            throw new ResourceAccessException("Could not access third party");
        }

        log.info("Message sent to third party service!");

    }
}