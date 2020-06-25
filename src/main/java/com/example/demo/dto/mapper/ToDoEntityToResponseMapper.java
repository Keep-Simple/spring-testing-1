package com.example.demo.dto.mapper;

import com.example.demo.dto.ToDoResponse;
import com.example.demo.model.ToDoEntity;

public class ToDoEntityToResponseMapper {
	public static ToDoResponse map(ToDoEntity todoEntity) {
		if (todoEntity == null)
			return null;
		var result = new ToDoResponse();
		result.id = todoEntity.getId();
		result.text = todoEntity.getText();
		result.completedAt = todoEntity.getCompletedAt();
		return result;
	}
}