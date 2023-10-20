package com.example.server.dto.task;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class TaskResponse {
    private String inputValue;
    private String firstFactor;
    private String secondFactor;
}
