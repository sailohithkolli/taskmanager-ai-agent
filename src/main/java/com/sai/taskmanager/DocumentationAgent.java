package com.sai.taskmanager;

import org.springframework.stereotype.Service;


@Service
public class DocumentationAgent {
private final GroqClient groqClient;

    public DocumentationAgent(GroqClient groqClient) {
        this.groqClient = groqClient;
    }

    private final static String SYSTEMPROMPT ="You are an expert technical writer. Analyze the following PR diff and focus ONLY on:\n" +
            "- Missing JavaDoc or method comments\n" +
            "- Unclear method or variable names\n" +
            "- Missing README updates for new features\n" +
            "- Undocumented public APIs or endpoints\n" +
            "- Complex logic that needs inline comments\n" +
            "Do NOT comment on code quality or security.";

    public String GenerateDocument(String diff)
    {


return  groqClient.chat(SYSTEMPROMPT,"Review this PR diff:" + diff);
    }
}
