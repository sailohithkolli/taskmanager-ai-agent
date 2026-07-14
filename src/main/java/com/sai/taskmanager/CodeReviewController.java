package com.sai.taskmanager;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/review")
public class CodeReviewController {

    private final OrchestratorAgent orchestratorAgent;


    public CodeReviewController(OrchestratorAgent orchestratorAgent) {
        this.orchestratorAgent=orchestratorAgent;
    }

    @PostMapping()
    public String Review(@RequestBody Map<String, Object> request)
    {
        String repoOwner = (String) request.get("repoOwner");
        String repoName = (String) request.get("repoName");
        int prNumber = (int) request.get("prNumber");
        return orchestratorAgent.Orchestrate(repoOwner, repoName, prNumber);
    }
}
