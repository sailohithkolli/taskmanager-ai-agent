package com.sai.taskmanager;

import org.springframework.stereotype.Service;


@Service
public class CodeQualityAgent {
private final GroqClient groqClient;

    public CodeQualityAgent(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    private static final String SYSTEM_PROMPT= "You are an expert code quality reviewer. Analyze the following PR diff and focus ONLY on:\n" +
            "- Code quality and readability\n" +
            "- Design patterns and best practices\n" +
            "- Duplicate code and refactoring opportunities\n" +
            "- Performance issues\n" +
            "- Missing error handling\n" +
            "Do NOT comment on security issues.";

    public String CheckQuality(String diff) {

        return groqClient.chat(SYSTEM_PROMPT, "Review this PR diff:\n\n" + diff);

    }
}
