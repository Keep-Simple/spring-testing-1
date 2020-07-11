package com.example.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotNull;
import java.time.ZonedDateTime;

@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ToDoResponse {
    @NotNull
    public Long id;

    @NotNull
    public String text;

    public ZonedDateTime completedAt;
}
