package com.example.demo.dto;

import java.time.ZonedDateTime;

import javax.validation.constraints.NotNull;

public class ToDoResponse {
	@NotNull
	public Long id;

	@NotNull
	public String text;

	public ZonedDateTime completedAt;
}