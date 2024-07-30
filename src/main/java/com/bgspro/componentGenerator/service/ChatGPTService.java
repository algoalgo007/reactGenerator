package com.bgspro.componentGenerator.service;

import com.bgspro.componentGenerator.dto.UserPromptDto;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public interface ChatGPTService {

    List<Map<String, Object>> modelList();
    Map<String, Object> prompt(UserPromptDto userPromptDto);
}
