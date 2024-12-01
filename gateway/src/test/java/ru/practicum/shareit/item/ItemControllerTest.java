package ru.practicum.shareit.item;

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
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.item.ItemController;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemToUpdateDto;
import ru.practicum.shareit.rest.RestQueryBuilder;
import ru.practicum.shareit.rest.RestQueryDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class ItemControllerTest {
    @Mock
    ItemService itemService;

    @InjectMocks
    ItemController itemController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void requestPathWithoutUserIdInHead() throws Exception {
        List<MockHttpServletRequestBuilder> needIdUserFromHeadRequest =
                List.of(post("/items"),
                        post("/items/1/comment"),
                        put("/items"),
                        patch("/items/1"),
                        delete("/items/1"));

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        for (var requestBuilder: needIdUserFromHeadRequest) {
            var result = mockMvc.perform(requestBuilder)
                    .andReturn();

            var status = result
                    .getResponse()
                    .getStatus();

            var path = result.getRequest().getMethod() + " '" + result.getRequest().getRequestURI() + "'";

            Assertions.assertEquals(400, status, path + " was needed userid as RequestHeader \"X-Sharer-User-Id\"");
        }
    }

    @Test
    public void getItemTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        var dto = ItemDto.builder()
                .id(10L)
                .build();

        var response = new ResponseEntity<>(
                (Object) dto,
                HttpStatus.OK);

        Mockito.when(itemService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10L), Long.class));

        Mockito.verify(itemService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.GET)
                        .path("/1")
                        .build());
    }

    @Test
    void getItemsForUserIdTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        var expectedResult = List.of(
                ItemDto.builder().id(11L).build(),
                ItemDto.builder().id(42L).build(),
                ItemDto.builder().id(37L).build());

        var response = new ResponseEntity<>(
                (Object) expectedResult,
                HttpStatus.OK);

        Mockito.when(itemService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id",is(11L), Long.class))
                .andExpect(jsonPath("$.[1].id",is(42L), Long.class))
                .andExpect(jsonPath("$.[2].id",is(37L), Long.class));

        Mockito.verify(itemService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.GET)
                        .requestHeadUserId(10L)
                        .build());
    }

    @Test
    void findItemsByTextTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        var expectedResult = List.of(
                ItemDto.builder().id(1L).build(),
                ItemDto.builder().id(42L).build(),
                ItemDto.builder().id(37L).build());

        var response = new ResponseEntity<>(
                (Object) expectedResult,
                HttpStatus.OK);

        Mockito.when(itemService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        mockMvc.perform(get("/items/search?text=abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id",is(1L), Long.class))
                .andExpect(jsonPath("$.[1].id",is(42L), Long.class))
                .andExpect(jsonPath("$.[2].id",is(37L), Long.class));

        Mockito.verify(itemService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.GET)
                        .path("/search?text=abc")
                        .build());
    }

    @Test
    void postItemTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        var response = new ResponseEntity<>(
                (Object) ItemDto.builder().id(50L).build(),
                HttpStatus.OK);

        Mockito.when(itemService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        var dto = ItemDto.builder()
                .id(13L)
                .name("la")
                .available(true)
                .description("ma")
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 10L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsBytes(dto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(50L), Long.class));

        Mockito.verify(itemService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.POST)
                        .requestHeadUserId(10L)
                        .body(dto)
                        .build());
    }

    @Test
    void postItemWithoutBodyTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        mockMvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().is(400));
    }

    @Test
    void postCommentTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        var commentDto = CommentDto.builder()
                .id(47L)
                .text("la")
                .build();

        var response = new ResponseEntity<>(
                (Object) commentDto,
                HttpStatus.OK);

        Mockito.when(itemService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/items/13/comment")
                        .header("X-Sharer-User-Id", 10L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsBytes(commentDto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(47L), Long.class));

        Mockito.verify(itemService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.POST)
                        .path("/13/comment")
                        .requestHeadUserId(10L)
                        .body(commentDto)
                        .build());
    }

    @Test
    void postCommentWithoutBodyTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        mockMvc.perform(post("/items/13/comment")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().is(400));
    }

    @Test
    void putItemTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        var response = new ResponseEntity<>(
                (Object) ItemDto.builder().id(50L).build(),
                HttpStatus.OK);

        Mockito.when(itemService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        var dto = ItemDto.builder()
                .id(13L)
                .name("la")
                .description("ma")
                .available(true)
                .build();

        mockMvc.perform(put("/items")
                        .header("X-Sharer-User-Id", 10L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsBytes(dto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(50L), Long.class));

        Mockito.verify(itemService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.PUT)
                        .requestHeadUserId(10L)
                        .body(dto)
                        .build());
    }

    @Test
    void putItemWithoutBodyTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        mockMvc.perform(put("/items")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().is(400));
    }

    @Test
    void patchItemTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        var dto = ItemToUpdateDto.builder()
                .build();

        var response = new ResponseEntity<>(
                (Object) ItemDto.builder().id(50L).build(),
                HttpStatus.OK);

        Mockito.when(itemService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/items/17")
                .header("X-Sharer-User-Id", 10L)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsBytes(dto))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(50L), Long.class));

        Mockito.verify(itemService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.PATCH)
                        .path("/17")
                        .requestHeadUserId(10L)
                        .body(dto)
                        .build());
    }

    @Test
    void patchItemWithoutBodyTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        mockMvc.perform(put("/items")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().is(400));
    }

    @Test
    void deleteItemTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        var dto = ItemDto.builder()
                .id(50L)
                .build();

        var response = new ResponseEntity<>(
                (Object) dto,
                HttpStatus.OK);

        Mockito.when(itemService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        mockMvc.perform(delete("/items/17")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(50L), Long.class));

        Mockito.verify(itemService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.DELETE)
                        .path("/17")
                        .requestHeadUserId(10L)
                        .build());
    }
}
