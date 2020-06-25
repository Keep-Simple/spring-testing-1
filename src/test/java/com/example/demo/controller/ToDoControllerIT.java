package com.example.demo.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.util.Arrays;

import com.example.demo.dto.mapper.ToDoEntityToResponseMapper;
import com.example.demo.model.ToDoEntity;
import com.example.demo.service.ToDoService;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@WebMvcTest(ToDoController.class)
@ActiveProfiles(profiles = "test")
class ToDoControllerIT {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private ToDoService toDoService;

	@Test
	void whenGetAll_thenReturnValidResponse() throws Exception {
		String testText = "My to do text";
		Long testId = 1l;
		when(toDoService.getAll()).thenReturn(
			Arrays.asList(
				ToDoEntityToResponseMapper.map(new ToDoEntity(testId, testText))
			)
		);
		
		this.mockMvc
			.perform(get("/todos"))
			.andExpect(status().isOk())
			.andExpect(content().contentType(MediaType.APPLICATION_JSON))
			.andExpect(jsonPath("$").isArray())
			.andExpect(jsonPath("$", hasSize(1)))
			.andExpect(jsonPath("$[0].text").value(testText))
			.andExpect(jsonPath("$[0].id").isNumber())
			.andExpect(jsonPath("$[0].id").value(testId))
			.andExpect(jsonPath("$[0].completedAt").doesNotExist());
	}

}
