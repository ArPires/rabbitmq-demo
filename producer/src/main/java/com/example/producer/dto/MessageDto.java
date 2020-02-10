package com.example.producer.dto;

import lombok.Getter;

import java.io.Serializable;

@Getter
public class MessageDto implements Serializable {

    private String message;
}
