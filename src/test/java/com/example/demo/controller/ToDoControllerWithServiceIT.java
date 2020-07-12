package com.example.demo.controller;

import com.example.demo.dto.ToDoSaveRequest;
import com.example.demo.model.ToDoEntity;
import com.example.demo.repository.ToDoRepository;
import com.example.demo.service.ToDoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ToDoController.class)
@ActiveProfiles(profiles = "test")
@Import(ToDoService.class)
class ToDoControllerWithServiceIT {

    @Autowired
    private ObjectMapper toJson;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ToDoRepository toDoRepository;

    @Test
    void whenGetAll_thenReturnValidResponse() throws Exception {
        String testText = "My to do text";

        when(toDoRepository
                .findAll())
                .thenReturn(
                Arrays.asList(new ToDoEntity(1L, testText))
        );

        this.mockMvc
                .perform(get("/todos"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].text").value(testText))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[0].completedAt").doesNotExist());
    }

    @Test
    void whenGetByText_thenReturnValidResponse() throws Exception {
        String testText = "My to do text";

        when(toDoRepository
                .findFirstByTextEqualsIgnoreCase(testText))
                .thenReturn(Optional.of(new ToDoEntity(1L, testText)));

        this.mockMvc
                .perform(get("/todos/name").param("text", testText))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.text").value(testText))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void whenGetAllCompleted_thenReturnValidResponse() throws Exception {
        String testText = "To do text";
        String testText1 = "To do text1";
        String testText2 = "To do text2";

        when(toDoRepository.findByCompletedAtNotNull()).thenReturn(
                Arrays.asList(
                        new ToDoEntity(1L, testText).completeNow(),
                        new ToDoEntity(2L, testText1).completeNow(),
                        new ToDoEntity(3L, testText2).completeNow()
                )
        );

        this.mockMvc
                .perform(get("/todos/completed"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(3)))
                .andExpect(jsonPath("$[0].text").value(testText))
                .andExpect(jsonPath("$[1].text").value(testText1))
                .andExpect(jsonPath("$[2].text").value(testText2))
                .andExpect(jsonPath("$[0].id").isNumber())
                .andExpect(jsonPath("$[1].id").isNumber())
                .andExpect(jsonPath("$[2].id").isNumber())
                .andExpect(jsonPath("$[0].completedAt").isNotEmpty())
                .andExpect(jsonPath("$[1].completedAt").isNotEmpty())
                .andExpect(jsonPath("$[2].completedAt").isNotEmpty());
    }

    @Test
    void whenUpdatedOrSaved_thenReturnValidResponse() throws Exception {
        String previousText = "mike";

        var req = ToDoSaveRequest
                .builder()
                .text("Champ")
                .id(1L)
                .build();

        when(toDoRepository
                .findById(anyLong()))
                .thenReturn(Optional.of(new ToDoEntity(req.id, previousText)));

        when(toDoRepository
                .save(ArgumentMatchers.any(ToDoEntity.class)))
                .thenAnswer(i -> {
                    ToDoEntity s = i.getArgument(0, ToDoEntity.class);

                    if (s.getId() == null) {
                        return new ToDoEntity(224L, s.getText());
                    }

                    if (!s.getId().equals(req.id)) {
                        return Optional.empty();
                    }

                    s.setText(s.getText());
                    return s;
                });

        //should return updated
        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/todos")
                        .content(toJson.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.text").value(req.text))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(req.id));

        // should return new ToDoEntity
        req.id = null;

        this.mockMvc
                .perform(MockMvcRequestBuilders
                        .post("/todos")
                        .content(toJson.writeValueAsString(req))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$").isNotEmpty())
                .andExpect(jsonPath("$.text").value(req.text))
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.id").value(224L));
    }

}
