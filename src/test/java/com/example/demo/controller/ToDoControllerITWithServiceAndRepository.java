package com.example.demo.controller;

import com.example.demo.dto.ToDoSaveRequest;
import com.example.demo.model.ToDoEntity;
import com.example.demo.repository.ToDoRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;


import javax.transaction.Transactional;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@SpringBootTest
@ActiveProfiles(profiles = "test")
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ToDoControllerITWithServiceAndRepository {

    @Autowired
    private ObjectMapper toJson;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ToDoRepository repository;

    @BeforeEach
    public void bootstrapData() {
        repository.save(new ToDoEntity("Wash the dishes"));
        repository.save(new ToDoEntity("Learn to test").completeNow());
        repository.save(new ToDoEntity("Cleaning"));
        repository.save(new ToDoEntity("JS").completeNow());
        repository.save(new ToDoEntity("Sports"));
    }

    @AfterEach
    public void clearData() {
        repository.deleteAll();
    }

    @Test
    @Order(1)
    void whenUpdateWithValidId_thenReturnUpdated() throws Exception {

        // Run first because, id will increment over iterations

        var req = ToDoSaveRequest
                .builder()
                .text("Champ")
                .id(2L)
                .build();

        this.mockMvc
                .perform(post("/todos")
                        .content(toJson.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("Champ"))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value("2"));
    }

    @Test
    void whenGetAll_thenReturnValidResponse() throws Exception {

        this.mockMvc
                .perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(5)))
                .andExpect(jsonPath("$[0].text").value("Wash the dishes"))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[4].text").value("Sports"))
                .andExpect(jsonPath("$[4].completedAt").doesNotExist());
    }

    @Test
    void whenGetAllCompleted_thenReturnValidResponse() throws Exception {

        this.mockMvc
                .perform(get("/todos/completed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].text").value("Learn to test"))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[1].text").value("JS"))
                .andExpect(jsonPath("$[1].completedAt").exists());
    }


    @Test
    void whenUpdateWithInValidId_thenThrows() throws Exception {

        var req = ToDoSaveRequest
                .builder()
                .text("Champ")
                .id(999L)
                .build();

        this.mockMvc
                .perform(post("/todos")
                        .content(toJson.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(content().string("Can not find todo with id 999"));
    }

    @Test
    void whenFindByValidText_thenReturnToDo() throws Exception {

        String req = "JS";

        this.mockMvc
                .perform(get("/todos/name")
                        .param("text", req)
                        .contentType(MediaType.TEXT_PLAIN))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value("JS"))
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.completedAt").exists());
    }

}
