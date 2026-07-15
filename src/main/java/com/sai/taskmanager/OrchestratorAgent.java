package com.sai.taskmanager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

@Service
public class OrchestratorAgent {
    @Value("${github.token}")
    private String github_token;

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
    public String Orchestrate(String repoOwner, String repoName, int prNumber) throws ExecutionException, InterruptedException {
        HttpHeaders githubHeaders = new HttpHeaders();
        githubHeaders.setBearerAuth(github_token);
        githubHeaders.set("Accept","application/vnd.github.v3.diff");

        HttpEntity<Void> githubRequest = new HttpEntity<>(githubHeaders);

        String prUrl = "https://api.github.com/repos/" + repoOwner +"/" +repoName+"/pulls/"+prNumber;

        ResponseEntity<String> diffResponse = restTemplate.exchange(prUrl, HttpMethod.GET,githubRequest,String.class);

        String diff = diffResponse.getBody();
        CompletableFuture<String> qualityFuture = CompletableFuture.supplyAsync(
                () -> codeQualityAgent.CheckQuality(diff));

        CompletableFuture<String> securityFuture = CompletableFuture.supplyAsync(
                () -> securityAgent.SecurityCheck(diff));

        CompletableFuture<String> docFuture = CompletableFuture.supplyAsync(
                () -> documentationAgent.GenerateDocument(diff));

        CompletableFuture.allOf(qualityFuture, securityFuture, docFuture).join();
        String qualityReview = qualityFuture.get();
        String securityReview = securityFuture.get();
        String docReview = docFuture.get();


        return "## Code Review Report\n\n" +
                "### Code Quality\n" + qualityReview + "\n\n" +
                "### Security\n" + securityReview + "\n\n" +
                "### Documentation\n" + docReview;
    }

}
