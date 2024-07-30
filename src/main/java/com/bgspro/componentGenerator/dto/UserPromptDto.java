package com.bgspro.componentGenerator.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class UserPromptDto {

    @NotBlank(message = "userPrompt는 null일 수 없습니다.")
    private String userPrompt;

    @Builder
    public UserPromptDto(String userPrompt) {
        this.userPrompt = userPrompt;
    }
}