package com.example.demo.controller;

import com.example.demo.dto.ToDoResponse;
import com.example.demo.dto.ToDoSaveRequest;
import com.example.demo.exception.ToDoNotFoundException;
import com.example.demo.service.ToDoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
public class ToDoController {

    @Autowired
    ToDoService toDoService;

    @ExceptionHandler({ToDoNotFoundException.class})
    public String handleException(Exception ex) {
        return ex.getMessage();
    }

    @GetMapping("/todos")
    @Valid List<ToDoResponse> getAll() {
        return toDoService.getAll();
    }

    @GetMapping("/todos/completed")
    @Valid List<ToDoResponse> getAllCompleted() {
        return toDoService.getAllCompleted();
    }

    @PostMapping("/todos")
    @Valid ToDoResponse saveOrUpdate(@RequestBody ToDoSaveRequest todoSaveRequest) throws ToDoNotFoundException {
        return toDoService.upsert(todoSaveRequest);
    }

    @PutMapping("/todos/{id}/complete")
    @Valid ToDoResponse complete(@PathVariable Long id) throws ToDoNotFoundException {
        return toDoService.completeToDo(id);
    }

    @GetMapping("/todos/{id}")
    @Valid ToDoResponse getOne(@PathVariable Long id) throws ToDoNotFoundException {
        return toDoService.getOne(id);
    }

    @GetMapping("/todos/name")
    @Valid ToDoResponse getOneByText(@RequestParam String text) throws ToDoNotFoundException {
        return toDoService.getByText(text);
    }

    @DeleteMapping("/todos/{id}")
    void delete(@PathVariable Long id) {
        toDoService.deleteOne(id);
    }

}
