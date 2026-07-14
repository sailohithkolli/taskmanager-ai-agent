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
public class DocumentationAgent {
    @Value("${groq.api.key}")
    private String apikey;
    private final RestTemplate restTemplate = new RestTemplate();
    public String GenerateDocument(String diff)
    {
        HttpHeaders groqHeaders = new HttpHeaders();
        groqHeaders.setContentType(MediaType.APPLICATION_JSON);
        groqHeaders.setBearerAuth(apikey);

        Map<String, Object> body = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are an expert technical writer. Analyze the following PR diff and focus ONLY on:\n" +
                                "- Missing JavaDoc or method comments\n" +
                                "- Unclear method or variable names\n" +
                                "- Missing README updates for new features\n" +
                                "- Undocumented public APIs or endpoints\n" +
                                "- Complex logic that needs inline comments\n" +
                                "Do NOT comment on code quality or security."),
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
