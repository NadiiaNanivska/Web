package com.example.server.repository;

import com.example.server.model.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TaskRepository  extends JpaRepository<Task, Integer> {
    List<Task> findAllByUserId(Integer user_id);
}
