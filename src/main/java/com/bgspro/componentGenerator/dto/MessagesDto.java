package com.bgspro.componentGenerator.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class MessagesDto {

    private String role;
    private String content;

    @Builder
    public MessagesDto(String role, String content) {
        this.role = role;
        this.content = content;
    }
}