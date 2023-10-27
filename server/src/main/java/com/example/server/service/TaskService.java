package com.example.server.service;

import com.example.server.dto.task.TaskRequest;
import com.example.server.dto.task.TaskResponse;
import com.example.server.exceptions.CustomResponse;
import com.example.server.exceptions.UserNotFoundException;
import com.example.server.model.Task;
import com.example.server.model.User;
import com.example.server.repository.TaskRepository;
import com.example.server.repository.UserRepository;
import com.example.server.webSocket.YourWebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import static java.lang.Math.sqrt;

@Service
@RequiredArgsConstructor
public class TaskService {
    private final Logger logger = LoggerFactory.getLogger(TaskService.class);
    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private Map<String, Thread> calculationThreads = new ConcurrentHashMap<>();
    private final YourWebSocketHandler webSocketHandler;
    private final String USER_NOT_FOUND = "User is not found";
    private final String TASK_NOT_FOUND = "Task is not found or cancelled";

    public ResponseEntity<?> getTasksByUserId(String username) {
        logger.info("Отримано запит на отримання списку завдань для користувача {}", username);
        User user = getUserByEmail(username);
        return ResponseEntity.ok(taskRepository.findAllByUserId(user.getId()).stream()
                .map(task -> TaskResponse.builder()
                        .inputValue(task.getInputValue().toString())
                        .firstFactor(task.getFirstFactor().toString())
                        .secondFactor(task.getSecondFactor().toString())
                        .build())
                .collect(Collectors.toList()));
    }

    public ResponseEntity<?> createTask(TaskRequest taskRequest, String username, String uniqueId) throws InterruptedException {
        User user = getUserByEmail(username);
        logger.info("Створено завдання для користувача {}", username);
        Task task = Task.builder()
                .user(user)
                .inputValue(taskRequest.getInputValue())
                .build();
        performCalculationAndSaveResult(task, task.getInputValue(), uniqueId);
        if (task.getFirstFactor() != null) {
            calculationThreads.remove(uniqueId);
            return new ResponseEntity<>(TaskResponse.builder()
                    .inputValue(task.getInputValue().toString())
                    .firstFactor(task.getFirstFactor().toString())
                    .secondFactor(task.getSecondFactor().toString())
                    .build(), HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    public ResponseEntity<?> cancelTask(String uniqueId) {
        logger.info("Скасовується завдання для користувача на потоці " + uniqueId);
        Thread calculationThread = calculationThreads.get(uniqueId);
        if (calculationThread != null && !calculationThread.isInterrupted()) {
            calculationThread.interrupt();
            calculationThreads.remove(uniqueId);
            return new ResponseEntity<>(new CustomResponse("Task is cancelled"), HttpStatus.OK);
        }
        return new ResponseEntity<>(new CustomResponse(TASK_NOT_FOUND), HttpStatus.NOT_FOUND);
    }

    private User getUserByEmail(String username) {
        return userRepository.findByEmail(username)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND));
    }

    private void performCalculationAndSaveResult(Task task, long inputValue, String uniqueId) throws InterruptedException {
        Thread calculationThread = new Thread(() -> {
            try {
                List<Long> result = factorize(inputValue, uniqueId);
                task.setFirstFactor(result.get(result.size() - 1));
                task.setSecondFactor(result.get(result.size() - 2));
                taskRepository.save(task);
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
        });
        calculationThread.setName(uniqueId);
        calculationThreads.put(uniqueId, calculationThread);
        calculationThread.start();
        calculationThread.join();
    }

    private List<Long> factorize(long number, String uniqueId) throws InterruptedException, IOException {
        double previousI = -0.98;
        List<Long> factors = new ArrayList<>();
        long sqrtN = (long) sqrt(number);
        while (number % 2 == 0) {
            factors.add(2L);
            number = number / 2;
            checkThreadState();
        }
        for (long i = 3; i <= sqrtN; i += 2) {
            while (number % i == 0) {
                factors.add(i);
                number = number / i;
            }
            double progress = ((double) i * 100) / sqrtN;
            if (progress >= previousI + 1) {
                webSocketHandler.sendProgressUpdate(uniqueId, String.valueOf(progress));
                previousI = progress;
            }
            checkThreadState();
        }
        if (number > 1) {
            factors.add(number);
        }
        return factors;
    }

    private void checkThreadState() throws InterruptedException {
        if (Thread.currentThread().isInterrupted()) {
            throw new InterruptedException();
        }
    }
}