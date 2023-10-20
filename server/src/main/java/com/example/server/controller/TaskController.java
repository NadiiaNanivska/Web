package com.example.server.controller;

import com.example.server.config.JwtService;
import com.example.server.dto.task.TaskRequest;
import com.example.server.service.TaskService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:3000")
public class TaskController {

    private final TaskService taskService;

    private final JwtService jwtService;

    @PostMapping("/tasks")
    public ResponseEntity<?> createTask(@Valid @RequestBody TaskRequest taskRequest, @RequestHeader("Authorization") String token, @RequestParam(name = "taskId") String uniqueId) throws InterruptedException {
        String username = jwtService.extractUsername(token.substring(7));
        return taskService.createTask(taskRequest, username, uniqueId);
    }

    @GetMapping("/tasks/history")
    public ResponseEntity<?> getTasksHistory(@RequestHeader("Authorization") String token) {
        String username = jwtService.extractUsername(token.substring(7));
        return taskService.getTasksByUserId(username);
    }

    @DeleteMapping("/tasks/cancel")
    public ResponseEntity<?> cancelTask(@RequestParam(name = "taskId") String uniqueId) {
        return taskService.cancelTask(uniqueId);
    }
}
