package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.rest.RestQueryBuilder;
import ru.practicum.shareit.rest.RestQueryDto;
import ru.practicum.shareit.user.UserController;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserToUpdateDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class UserControllerTest {
    @Mock
    UserService userService;

    @InjectMocks
    UserController userController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void getUserTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        var dto = UserDto.builder()
                .id(47L)
                .name("la")
                .email("email@mail.ru")
                .build();

        var response = new ResponseEntity<>(
                (Object) dto,
                HttpStatus.OK);

        Mockito.when(userService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        mockMvc.perform(get("/users/17"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(47L), Long.class));

        Mockito.verify(userService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.GET)
                        .path("/17")
                        .build());
    }

    @Test
    void postUserTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        var response = new ResponseEntity<>(
                (Object) ItemDto.builder().id(50L).build(),
                HttpStatus.OK);

        Mockito.when(userService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        var dto = UserDto.builder()
                .name("la")
                .email("email@mail.ru")
                .build();

        mockMvc.perform(post("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsBytes(dto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(50L), Long.class));

        Mockito.verify(userService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.POST)
                        .body(dto)
                        .build());
    }

    @Test
    void postUserWithoutBodyTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        mockMvc.perform(post("/users"))
                .andExpect(status().is(400));
    }

    @Test
    void putUserTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        var response = new ResponseEntity<>(
                (Object) ItemDto.builder().id(52L).build(),
                HttpStatus.OK);

        Mockito.when(userService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        var dto = UserDto.builder()
                .id(10L)
                .name("la")
                .email("email@mail.ru")
                .build();

        mockMvc.perform(put("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsBytes(dto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(52L), Long.class));

        Mockito.verify(userService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.PUT)
                        .body(dto)
                        .build());
    }

    @Test
    void putUserWithoutBodyTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        mockMvc.perform(put("/users"))
                .andExpect(status().is(400));
    }

    @Test
    void patchUserTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        var response = new ResponseEntity<>(
                (Object) ItemDto.builder().id(542L).build(),
                HttpStatus.OK);

        Mockito.when(userService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        var dto = UserToUpdateDto.builder().name("la").build();

        mockMvc.perform(patch("/users/13")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsBytes(dto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(542L), Long.class));

        Mockito.verify(userService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.PATCH)
                        .path("/13")
                        .body(dto)
                        .build());
    }

    @Test
    void patchUserWithoutBodyTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        mockMvc.perform(patch("/users/13"))
                .andExpect(status().is(400));
    }

    @Test
    void deleteUserTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        var response = new ResponseEntity<>(
                (Object) ItemDto.builder().id(54L).build(),
                HttpStatus.OK);

        Mockito.when(userService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        mockMvc.perform(delete("/users/13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(54L), Long.class));

        Mockito.verify(userService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.DELETE)
                        .path("/13")
                        .build());
    }
}
