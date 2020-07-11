package com.example.demo.dto.mapper;

import com.example.demo.dto.ToDoResponse;
import com.example.demo.model.ToDoEntity;

public class ToDoEntityToResponseMapper {
    public static ToDoResponse map(ToDoEntity todoEntity) {
        if (todoEntity == null) {
            return null;
        }

        return ToDoResponse
                .builder()
                .id(todoEntity.getId())
                .text(todoEntity.getText())
                .completedAt(todoEntity.getCompletedAt())
                .build();
    }
}
