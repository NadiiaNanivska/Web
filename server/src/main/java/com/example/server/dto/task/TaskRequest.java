package com.example.server.dto.task;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class TaskRequest {
    @NotNull(message = "First factor is required")
    @Min(value = 1, message = "First factor must be greater than or equal to 1")
    @Max(value = Long.MAX_VALUE, message = "First factor must be less than or equal to " + Long.MAX_VALUE)
    private Long inputValue;
}
