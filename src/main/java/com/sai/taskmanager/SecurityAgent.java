package com.sai.taskmanager;

import org.springframework.stereotype.Service;


@Service
public class SecurityAgent {

    private final GroqClient groqClient;
    private static final String SYSTEMPROMPT = "You are an expert security reviewer. Analyze the following PR diff and focus ONLY on:\n" +
            "- Security vulnerabilities (SQL injection, XSS, CSRF)\n" +
            "- Exposed secrets or API keys in code\n" +
            "- Authentication and authorization issues\n" +
            "- Insecure data validation\n" +
            "- Dependency vulnerabilities\n" +
            "Do NOT comment on code quality or style.";

    public SecurityAgent(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    public String SecurityCheck(String diff)
    {

        return groqClient.chat(SYSTEMPROMPT,"Review this PR Diff:\n\n" + diff);

    }

}
