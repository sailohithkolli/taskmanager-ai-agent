package com.sai.taskmanager;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Service
public class AgentService {

    private final TaskService taskService;

    @Value("${groq.api.key}")
    private String apiKey;

    private final RestTemplate restTemplate = new RestTemplate();
    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    public AgentService(TaskService taskService) {
        this.taskService = taskService;
    }

    public String chat(String userMessage) throws Exception {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(apiKey);

        Map<String, Object> body = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(Map.of("role", "system", "content",
                                "You are a task management assistant. You have access to these tools:\n" +
                                        "- get_all_tasks: Returns all tasks from the database\n" +
                                        "- create_task: Creates a new task\n\n" +
                                        "When you need a tool, respond ONLY with JSON: {\"tool\": \"get_all_tasks\"} or {\"tool\": \"create_task\", \"title\": \"task title\"}\n" +
                                        "Otherwise respond normally."),
                                        Map.of("role", "user", "content", userMessage))
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);

        ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_URL, request, Map.class);

        List<Map> choices = (List<Map>) response.getBody().get("choices");
        Map message = (Map) choices.get(0).get("message");
        String aiResponse = (String) message.get("content");

        // Tool detection
        if (aiResponse.trim().startsWith("{")) {
            ObjectMapper mapper = new ObjectMapper();
            Map<String, String> toolCall = mapper.readValue(aiResponse, Map.class);
            String toolName = toolCall.get("tool");

            if ("get_all_tasks".equals(toolName)) {
                List<Task> tasks = taskService.getAllTasks();
                String taskData = mapper.writeValueAsString(tasks);
                return sendFollowUp(headers, userMessage, aiResponse, taskData);
            }

            if("create_task".equals(toolName))
            {
                String title = toolCall.get("title");
                Task newTask = new Task();
                newTask.setTitle(title);
                newTask.setCompleted(false);
                Task created = taskService.createTask(newTask);
                return sendFollowUp(headers, userMessage,aiResponse,"Task created:" + created.getTitle());
            }
        }

        return aiResponse;
    }
    private String sendFollowUp(HttpHeaders headers,String userMessage,String toolCallResponse,String toolData) {
        Map<String, Object> body = Map.of(
                "model", "llama-3.3-70b-versatile",
                "messages", List.of(
                        Map.of("role", "system", "content", "You are a task management assistant."),
                        Map.of("role", "user", "content", userMessage),
                        Map.of("role", "assistant", "content", toolCallResponse),
                        Map.of("role", "user", "content", "Tool result: " + toolData + "\nNow answer the user's question based on this data.")
                )
        );
        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_URL, request, Map.class);
        List<Map> choices = (List<Map>) response.getBody().get("choices");
        Map message = (Map) choices.get(0).get("message");
        return (String) message.get("content");
    }
}