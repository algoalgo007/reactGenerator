package com.bgspro.componentGenerator.controller;

import com.bgspro.componentGenerator.domain.User;
import com.bgspro.componentGenerator.service.ChatGPTService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@RequiredArgsConstructor
@RequestMapping("/views")
@Controller
public class GeneratorController {

    private final ChatGPTService chatGPTService;

    @GetMapping("/generator")
    public String getInitPage(HttpServletRequest request, Model model) {
        User user = (User) request.getSession().getAttribute("user");

        if (user != null) {
            model.addAttribute("username", getUsername(user));
        }
        return "home";
    }

    @PostMapping("/prompt")
    public ResponseEntity<Void> saveContent(@RequestBody ContentRequest request, HttpSession session) {
        // todo: content 예외처리
        String content = request.getContent();
        content = deleteComment(content);

        session.setAttribute("content", content);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/prompt")
    public String getPromptPage(Model model, HttpSession session, HttpServletRequest request) {
        String content = (String) session.getAttribute("content");
        String userPrompt = (String) session.getAttribute("userPrompt");

        User user = (User) request.getSession().getAttribute("user");

        if (user != null) {
            model.addAttribute("username", getUsername(user));
        }

        model.addAttribute("userPrompt", userPrompt);
        model.addAttribute("content", content);

        session.removeAttribute("userPrompt");
        session.removeAttribute("content");

        return "prompt";
    }

    @Setter
    @Getter
    private static class ContentRequest {

        private String content;

    }
    private static String deleteComment(String content) {
        if (content.startsWith("```javascript") && content.endsWith("```")) {
            content = content.substring(13, content.length() - 3).trim();
        }
        if (content.startsWith("```jsx") && content.endsWith("```")) {
            content = content.substring(6, content.length() - 3).trim();
        }
        return content;
    }

    private static String getUsername(User user) {
        String userEmail = user.getUsername();
        return userEmail.split("@")[0];
    }
}