package com.bgspro.componentGenerator.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor
public class ChatCompletionDto {

    private String model;
    private List<MessagesDto> messages;
    private Float temperature;

    @Builder
    public ChatCompletionDto(String model, List<MessagesDto> messages, Float temperature) {
        this.model = model;
        this.messages = messages;
        this.temperature = temperature;
    }
}