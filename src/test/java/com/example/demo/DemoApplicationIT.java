package com.example.demo;

import com.example.demo.controller.ToDoController;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class DemoApplicationIT {

	@Autowired
	private ToDoController toDoController;

	@Test
	void contextLoads() throws Exception {
		if (toDoController == null) {
			throw new Exception("ToDoController is null");
		}
	}

}
