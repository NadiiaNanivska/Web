package com.example.server.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "tasks")
public class Task {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @NotNull(message = "Input value is required")
    @Min(value = 1, message = "Input value must be greater than or equal to 1")
    @Max(value = Long.MAX_VALUE, message = "Input value must be less than or equal to " + Long.MAX_VALUE)
    private Long inputValue;

    @NotNull(message = "First factor is required")
    @Min(value = 1, message = "First factor must be greater than or equal to 1")
    @Max(value = Long.MAX_VALUE, message = "First factor must be less than or equal to " + Long.MAX_VALUE)
    private Long firstFactor;

    @NotNull(message = "Second factor is required")
    @Min(value = 1, message = "Second factor must be greater than or equal to 1")
    @Max(value = Long.MAX_VALUE, message = "Second factor must be less than or equal to " + Long.MAX_VALUE)
    private Long secondFactor;

    @ManyToOne()
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
