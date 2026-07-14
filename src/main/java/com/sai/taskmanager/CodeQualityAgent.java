package com.sai.taskmanager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class CodeQualityAgent {
    @Value("${groq.api.key}")
    private String apikey;
    private final RestTemplate restTemplate = new RestTemplate();
    public String CheckQuality(String diff) {
        HttpHeaders groqHeaders = new HttpHeaders();
        groqHeaders.setContentType(MediaType.APPLICATION_JSON);
        groqHeaders.setBearerAuth(apikey);

        Map<String, Object> body = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are an expert code quality reviewer. Analyze the following PR diff and focus ONLY on:\n" +
                                "- Code quality and readability\n" +
                                "- Design patterns and best practices\n" +
                                "- Duplicate code and refactoring opportunities\n" +
                                "- Performance issues\n" +
                                "- Missing error handling\n" +
                                "Do NOT comment on security issues."),
                        Map.of("role", "user", "content", "Review this PR diff:\n\n" + diff)
                )
        );

        HttpEntity<Map<String, Object>> groqRequest = new HttpEntity<>(body, groqHeaders);
        ResponseEntity<Map> response = restTemplate.postForEntity("https://api.groq.com/openai/v1/chat/completions", groqRequest, Map.class);
        List<Map> choices = (List<Map>) response.getBody().get("choices");
        Map message = (Map) choices.get(0).get("message");
        return (String) message.get("content");
    }
}
