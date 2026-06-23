package com.sai.taskmanager;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    private final TaskService taskService;
    public TaskController(TaskService taskService)
    {
        this.taskService=taskService;
    }

    @GetMapping()
    public List<Task> getTasks()
    {
        return taskService.getAllTasks();
    }

    @PostMapping()
    public Task createTask(@Valid @RequestBody Task task)
    {
       return taskService.createTask(task);
    }
    @DeleteMapping("/{id}")
    public void deleteTask(@PathVariable int id)
    {
        taskService.deleteTask(id);
    }
    @PutMapping("/{id}")
    public Task updateTask(@PathVariable int id,@RequestBody Task task)
    {
        return taskService.updateTask(id,task);
    }
}

