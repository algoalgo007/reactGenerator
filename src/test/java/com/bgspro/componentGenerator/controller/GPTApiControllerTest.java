package com.bgspro.componentGenerator.controller;

import com.bgspro.componentGenerator.config.jwt.JwtProperties;
import com.bgspro.componentGenerator.config.jwt.TokenProvider;
import com.bgspro.componentGenerator.domain.User;
import com.bgspro.componentGenerator.dto.UserPromptDto;
import com.bgspro.componentGenerator.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@SpringBootTest
@AutoConfigureMockMvc
public class GPTApiControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private JwtProperties jwtProperties;

    private String token;

    @BeforeEach
    void setUp() {
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());

        token = "Bearer " + tokenProvider.generateToken(testUser, Duration.ofDays(14));
    }

    @DisplayName("modelList(): openAI API에서 지원하는 모델들의 리스트를 응답으로 받는다.")
    @Test
    void modelList() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/chatGpt/modelList")
                .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists())
                .andExpect(jsonPath("$[0].object").exists())
                .andExpect(jsonPath("$[0].created").exists())
                .andExpect(jsonPath("$[0].owned_by").exists());
    }

    @DisplayName("sendPrompt(): 유저 프롬프트를 전송하면 올바른 응답을 받는다.")
    @Test
    void sendPrompt() throws Exception {
        UserPromptDto userPromptDto = new UserPromptDto();
        userPromptDto.setUserPrompt("button");

        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/chatGpt/prompt")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPromptDto))
                        .session(new MockHttpSession()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.object").exists())
                .andExpect(jsonPath("$.model").exists());
    }

    @DisplayName("sendPrompt(): 유저 프롬프트를 전송하지않으면 오류가 발생한다.")
    @Test
    void sendPromptFail() throws Exception {
        // given
        UserPromptDto userPromptDto = new UserPromptDto();
        userPromptDto.setUserPrompt(""); // 빈 프롬프트 설정

        // when
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/chatGpt/prompt")
                        .header("Authorization", token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userPromptDto))
                        .session(new MockHttpSession()))
                // then
                .andExpect(status().isBadRequest()) // 400 상태 코드 기대
                .andExpect(jsonPath("$.errorCode").value(400))
                .andExpect(jsonPath("$.message").value("userPrompt는 null일 수 없습니다."));
    }
}