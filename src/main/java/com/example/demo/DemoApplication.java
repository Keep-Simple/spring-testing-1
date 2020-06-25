package com.example.demo;

import com.example.demo.model.ToDoEntity;
import com.example.demo.repository.ToDoRepository;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class DemoApplication {

	public static void main(String[] args) {
		SpringApplication.run(DemoApplication.class, args);
	}

	@Bean
	CommandLineRunner initDatabase(ToDoRepository repository) {
		return args -> {
			repository.save(new ToDoEntity("Wash the dishes"));
			repository.save(
				new ToDoEntity("Learn to test Java app").completeNow()
			);
		};
	}
}
