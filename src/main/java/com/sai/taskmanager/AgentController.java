package com.sai.taskmanager;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/agent")
public class AgentController {
    private final AgentService agentService;

    public AgentController(AgentService agentService) {
        this.agentService = agentService;
    }

    @PostMapping("/chat")
        public String chat(@RequestBody Map<String, String> request) throws Exception {
        return agentService.chat((request.get("message")));
    }
}
