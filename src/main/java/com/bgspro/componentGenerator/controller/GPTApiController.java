package com.bgspro.componentGenerator.controller;

import com.bgspro.componentGenerator.dto.UserPromptDto;
import com.bgspro.componentGenerator.service.ChatGPTService;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RequestMapping(value = "/api/v1/chatGpt")
@RequiredArgsConstructor
@RestController
public class GPTApiController {

    private final ChatGPTService chatGPTService;

    @GetMapping("/modelList")
    public ResponseEntity<List<Map<String, Object>>> selectModelList() {
        List<Map<String, Object>> result = chatGPTService.modelList();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/prompt")
    public ResponseEntity<Map<String, Object>> sendPrompt(@RequestBody @Validated UserPromptDto userPromptDto, HttpSession session) {
        Map<String, Object> result = chatGPTService.prompt(userPromptDto);
        session.setAttribute("userPrompt", userPromptDto.getUserPrompt());
        return ResponseEntity.ok(result);
    }
}
