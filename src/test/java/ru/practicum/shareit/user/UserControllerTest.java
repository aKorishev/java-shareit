package ru.practicum.shareit.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import ru.practicum.shareit.request.dto.RequestDto;
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

        Mockito.when(userService.getUser(Mockito.anyLong()))
                .thenReturn(dto);

        mockMvc.perform(get("/users/17"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(47L), Long.class));

        Mockito.verify(userService, Mockito.times(1))
                .getUser(17L);
    }

    @Test
    void postUserTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(userController)
                .build();

        Mockito.when(userService.updateUser(Mockito.any(UserDto.class)))
                .thenReturn(UserDto.builder().id(50L).build());

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
                .updateUser(dto);
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

        Mockito.when(userService.updateUser(Mockito.any(UserDto.class)))
                .thenReturn(UserDto.builder().id(52L).build());

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
                .updateUser(dto);
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

        Mockito.when(userService.updateUser(Mockito.anyLong(), Mockito.any(UserToUpdateDto.class)))
                .thenReturn(UserDto.builder().id(542L).build());

        var dto = UserToUpdateDto.builder().name("la").build();

        mockMvc.perform(patch("/users/13")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsBytes(dto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(542L), Long.class));

        Mockito.verify(userService, Mockito.times(1))
                .updateUser(13, dto);
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

        Mockito.when(userService.deleteUser(Mockito.anyLong()))
                .thenReturn(UserDto.builder().id(54L).build());

        mockMvc.perform(delete("/users/13"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(54L), Long.class));

        Mockito.verify(userService, Mockito.times(1))
                .deleteUser(13);
    }
}
