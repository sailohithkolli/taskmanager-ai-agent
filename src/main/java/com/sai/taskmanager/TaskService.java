package com.sai.taskmanager;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private final TaskRepository taskRepository;

    public TaskService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public List<Task> getAllTasks()
    {
        return taskRepository.findAll();

    }

    public Task createTask(Task task) {
        return taskRepository.save(task);
    }

    public void deleteTask(int id) {
        taskRepository.deleteById(id);
    }

    public Task updateTask(int id, Task task) {
        Task existing = taskRepository.findById(id).orElseThrow(()->new RuntimeException("Task not found"));
        existing.setTitle(task.getTitle());
        existing.setCompleted(task.isCompleted());
        return taskRepository.save(existing);
    }

    public Task completeTask(int id)
    {
        Task exists = taskRepository.findById(id).orElseThrow(()->new RuntimeException("Task not found"));
        exists.setCompleted(true);
        return taskRepository.save(exists);
    }
}
