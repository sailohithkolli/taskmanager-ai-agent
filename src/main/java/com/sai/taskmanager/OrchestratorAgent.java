package com.sai.taskmanager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class OrchestratorAgent {
    @Value("${github.token}")
    private String github_token;
    @Value(("${groq.api.key}"))
    private String apikey;
    private final CodeQualityAgent codeQualityAgent;
    private final SecurityAgent securityAgent;
    private final DocumentationAgent documentationAgent;
    public OrchestratorAgent(CodeQualityAgent codeQualityAgent,
                             SecurityAgent securityAgent,
                             DocumentationAgent documentationAgent) {
        this.codeQualityAgent = codeQualityAgent;
        this.securityAgent = securityAgent;
        this.documentationAgent = documentationAgent;
    }
    private final RestTemplate restTemplate = new RestTemplate();
    public String Orchestrate(String repoOwner, String repoName, int prNumber)
    {
        HttpHeaders githubHeaders = new HttpHeaders();
        githubHeaders.setBearerAuth(github_token);
        githubHeaders.set("Accept","application/vnd.github.v3.diff");

        HttpEntity<Void> githubRequest = new HttpEntity<>(githubHeaders);

        String prUrl = "https://api.github.com/repos/" + repoOwner +"/" +repoName+"/pulls/"+prNumber;

        ResponseEntity<String> diffResponse = restTemplate.exchange(prUrl, HttpMethod.GET,githubRequest,String.class);

        String diff = diffResponse.getBody();
        String qualityReview = codeQualityAgent.CheckQuality(diff);
        String securityReview = securityAgent.SecurityCheck(diff);
        String docReview = documentationAgent.GenerateDocument(diff);
        return "## Code Review Report\n\n" +
                "### Code Quality\n" + qualityReview + "\n\n" +
                "### Security\n" + securityReview + "\n\n" +
                "### Documentation\n" + docReview;
    }

}
