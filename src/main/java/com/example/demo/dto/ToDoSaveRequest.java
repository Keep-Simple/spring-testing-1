package com.example.demo.dto;

import javax.validation.constraints.NotNull;

public class ToDoSaveRequest {
	public Long id;

	@NotNull
	public String text;
}