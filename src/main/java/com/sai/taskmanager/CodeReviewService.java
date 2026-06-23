package com.sai.taskmanager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class CodeReviewService {

    @Value("${github.token}")
    private String github_token;
    @Value(("${groq.api.key}"))
    private String apikey;
    private final RestTemplate restTemplate = new RestTemplate();
    public  String reviewPR(String repoOwner, String repoName, int prNumber){
        HttpHeaders githubHeaders = new HttpHeaders();
        githubHeaders.setBearerAuth(github_token);
        githubHeaders.set("Accept","application/vnd.github.v3.diff");

        HttpEntity<Void> githubRequest = new HttpEntity<>(githubHeaders);

        String prUrl = "https://api.github.com/repos/" + repoOwner +"/" +repoName+"/pulls/"+prNumber;

        ResponseEntity<String> diffResponse = restTemplate.exchange(prUrl, HttpMethod.GET,githubRequest,String.class);

        String diff = diffResponse.getBody();

        HttpHeaders groqHeaders = new HttpHeaders();
        groqHeaders.setContentType(MediaType.APPLICATION_JSON);
        groqHeaders.setBearerAuth(apikey);

        Map<String, Object> body = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a senior code reviewer. Review the following PR diff. Point out bugs, security issues, code smells, and suggest improvements. Be specific with line references."),
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
