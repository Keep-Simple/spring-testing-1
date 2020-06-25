package com.example.demo.repository;

import com.example.demo.model.ToDoEntity;

import org.springframework.data.jpa.repository.JpaRepository;

public interface ToDoRepository extends JpaRepository<ToDoEntity, Long> {

}