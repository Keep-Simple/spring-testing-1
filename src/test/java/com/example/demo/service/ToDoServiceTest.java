package com.example.demo.service;

import com.example.demo.dto.ToDoSaveRequest;
import com.example.demo.dto.mapper.ToDoEntityToResponseMapper;
import com.example.demo.exception.ToDoNotFoundException;
import com.example.demo.model.ToDoEntity;
import com.example.demo.repository.ToDoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;


class ToDoServiceTest {

    private ToDoRepository toDoRepository;

    private ToDoService toDoService;

    @BeforeEach
    void setUp() {
        toDoRepository = mock(ToDoRepository.class);
        toDoService = new ToDoService(toDoRepository);
    }

    // GET_ALL METHOD //

    @Test
    void whenGetAll_thenReturnAll() {
        var expectedList = Arrays.asList(
                new ToDoEntity(0L, "Test 1"),
                new ToDoEntity(1L, "Test 2").completeNow()
        );

        when(toDoRepository
                .findAll())
                .thenReturn(expectedList);

        var actualList = toDoService.getAll();

        assertEquals(actualList.size(), expectedList.size());

        for (int i = 0; i < actualList.size(); i++) {
            assertThat(actualList.get(i),
                    samePropertyValuesAs(ToDoEntityToResponseMapper.map(expectedList.get(i))
            ));
        }
    }

    // GET_ALL_COMPLETED METHOD //

    @Test
    void whenGetAllCompleted_thenReturnAll() {
        var expectedList = Arrays.asList(
                new ToDoEntity(1L, "Test 2").completeNow(),
                new ToDoEntity(1L, "Test 3").completeNow(),
                new ToDoEntity(1L, "Test 4").completeNow()
        );

        when(toDoRepository
                .findByCompletedAtNotNull())
                .thenReturn(expectedList);

        var actualList = toDoService.getAllCompleted();

        assertEquals(actualList.size(), expectedList.size());

        for (int i = 0; i < actualList.size(); i++) {
            assertThat(actualList.get(i),
                    samePropertyValuesAs(ToDoEntityToResponseMapper.map(expectedList.get(i))
            ));
        }
    }

    // UPSERT METHOD //

    @Test
    void whenUpsertWithId_thenReturnUpdated() throws ToDoNotFoundException {
        var expectedToDo = new ToDoEntity(0L, "New Item");

        when(toDoRepository
                .findById(anyLong()))
                .thenReturn(Optional.of(expectedToDo));

        when(toDoRepository
                .save(ArgumentMatchers.any(ToDoEntity.class)))
                .thenAnswer(i -> {
            ToDoEntity s = i.getArgument(0, ToDoEntity.class);

            if (s.getId() == null) {
                return new ToDoEntity(224L, s.getText());
            }

            if (!s.getId().equals(expectedToDo.getId())) {
                return Optional.empty();
            }

            s.setText(s.getText());
            return s;
            });

        var request = ToDoSaveRequest
                .builder()
                .id(expectedToDo.getId())
                .text("Updated Item")
                .build();

        var todo = toDoService.upsert(request);

        assertSame(todo.id, request.id);
        assertEquals(todo.text, request.text);
    }

    @Test
    void whenUpsertWithWrongId_thenThrows() {

        var toDoDto = ToDoSaveRequest
                .builder()
                .text("Non Existent ToDo")
                .id(99L)
                .build();

        //repository mock will behave as needed here - return Optional.empty()
        assertThrows(ToDoNotFoundException.class, ()-> toDoService.upsert(toDoDto));
    }

    @Test
    void whenUpsertNoId_thenReturnNew() throws ToDoNotFoundException {
        var newId = 0L;

        when(toDoRepository
                .save(ArgumentMatchers.any(ToDoEntity.class)))
                .thenAnswer(i -> {
                    ToDoEntity s = i.getArgument(0, ToDoEntity.class);

                    if (s.getId() != null) {
                        return new ToDoEntity();
                    }

                    return new ToDoEntity(newId, s.getText());
                });

        var toDoDto = ToDoSaveRequest
                .builder()
                .text("Created Item")
                .build();

        var result = toDoService.upsert(toDoDto);

        verify(toDoRepository, times(0)).findById(anyLong());
        assertEquals(result.id, newId);
        assertEquals(result.text, toDoDto.text);
    }

    // COMPLETE_TO_DO METHOD //

    @Test
    void whenComplete_thenReturnWithCompletedAt() throws ToDoNotFoundException {
        var startTime = ZonedDateTime.now(ZoneOffset.UTC);
        var todo = new ToDoEntity(0L, "Test 1");

        when(toDoRepository
                .findById(anyLong()))
                .thenReturn(Optional.of(todo));

        when(toDoRepository
                .save(ArgumentMatchers
                        .any(ToDoEntity.class)))
                .thenReturn(todo);

        var result = toDoService.completeToDo(todo.getId());

        assertSame(result.id, todo.getId());
        assertEquals(result.text, todo.getText());
        assertTrue(result.completedAt.isAfter(startTime));
    }

    // GET_ONE METHOD //

    @Test
    void whenGetOne_thenReturnCorrectOne() throws ToDoNotFoundException {
        var todo = new ToDoEntity(0L, "Test 1");

        when(toDoRepository
                .findById(anyLong()))
                .thenReturn(Optional.of(todo));

        var result = toDoService.getOne(0L);

        assertThat(result, samePropertyValuesAs(
                ToDoEntityToResponseMapper.map(todo)
        ));
    }

    @Test
    void whenIdNotFound_thenThrowNotFoundException() {
        assertThrows(ToDoNotFoundException.class, () -> toDoService.getOne(null));
    }

    // GET_BY_TEXT METHOD //

    @Test
    void whenGetByText_thenReturnCorrectOne() throws ToDoNotFoundException {
        var todo = new ToDoEntity(0L, "Cool topic");

        when(toDoRepository
                .findFirstByTextEqualsIgnoreCase(anyString()))
                .thenReturn(Optional.of(todo));

        var result = toDoService.getByText("Cool topic");

        assertThat(result,
                samePropertyValuesAs(ToDoEntityToResponseMapper.map(todo)
        ));
    }

    @Test
    void whenTextNotFound_thenThrowNotFoundException() {
        assertThrows(ToDoNotFoundException.class, () -> toDoService.getByText(null));
    }

    // DELETE_ONE METHOD //

    @Test
    void whenDeleteOne_thenRepositoryDeleteCalled() {
        var id = 0L;
        toDoService.deleteOne(id);

        verify(toDoRepository, times(1)).deleteById(id);
    }

}
