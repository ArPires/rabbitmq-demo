package com.example.thirdparty.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
public class ThirdPartyController {

    @GetMapping(value = "/thirdparty")
    public void getSomething(@RequestParam String m) {
        log.info("Message arrived: " + m);
    }

}
