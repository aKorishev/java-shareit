package ru.practicum.shareit.item;

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
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemToUpdateDto;

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

        Mockito.when(itemService.findItem(Mockito.anyLong()))
                .thenReturn(dto);

        mockMvc.perform(get("/items/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10L), Long.class));
    }

    @Test
    void getItemParsingRequestId() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        mockMvc.perform(get("/items/100"))
                .andExpect(status().isOk());

        Mockito.verify(itemService, Mockito.times(1))
                .findItem(100);
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

        Mockito.when(itemService.getItems(Mockito.anyLong()))
                        .thenReturn(expectedResult);

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id",is(11L), Long.class))
                .andExpect(jsonPath("$.[1].id",is(42L), Long.class))
                .andExpect(jsonPath("$.[2].id",is(37L), Long.class));
    }

    @Test
    void getItemsForUserIdParsingRequestTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        mockMvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk());

        Mockito.verify(itemService, Mockito.times(1))
                .getItems(10);
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

        Mockito.when(itemService.findFreeItemsByText(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(expectedResult);

        mockMvc.perform(get("/items/search?text=abc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id",is(1L), Long.class))
                .andExpect(jsonPath("$.[1].id",is(42L), Long.class))
                .andExpect(jsonPath("$.[2].id",is(37L), Long.class));

        Mockito.verify(itemService, Mockito.times(1))
                .findFreeItemsByText("abc", true);

        Mockito.verify(itemService, Mockito.never())
                .findFreeItemsByText("abc", false);
    }

    @Test
    void findItemsByTextFailParsingRequestTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        mockMvc.perform(get("/items/search"))
                .andExpect(status().is(400));
    }

    @Test
    void postItemTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(itemController)
                .build();

        Mockito.when(itemService.createItem(Mockito.any(ItemDto.class), Mockito.anyLong()))
                .thenReturn(ItemDto.builder().id(50L).build());

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
                .createItem(dto, 10L);
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

        var dto = CommentDto.builder()
                .id(47L)
                .text("la")
                .build();

        Mockito.when(itemService.addComment(Mockito.any(CommentDto.class), Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(CommentDto.builder().id(50L).build());

        mockMvc.perform(post("/items/13/comment")
                        .header("X-Sharer-User-Id", 10L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsBytes(dto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(50L), Long.class));

        Mockito.verify(itemService, Mockito.times(1))
                .addComment(dto, 13L, 10L);
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

        Mockito.when(itemService.updateItem(Mockito.any(ItemDto.class), Mockito.anyLong()))
                .thenReturn(ItemDto.builder().id(50L).build());

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
                .updateItem(dto, 10L);
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

        Mockito.when(itemService.updateItem(Mockito.anyLong(), Mockito.any(ItemToUpdateDto.class), Mockito.anyLong()))
                .thenReturn(ItemDto.builder().id(50L).build());

        mockMvc.perform(patch("/items/17")
                .header("X-Sharer-User-Id", 10L)
                .characterEncoding(StandardCharsets.UTF_8)
                .content(objectMapper.writeValueAsBytes(dto))
                .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(50L), Long.class));

        Mockito.verify(itemService, Mockito.times(1))
                .updateItem(17, dto, 10L);
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

        Mockito.when(itemService.deleteItem(17L, 10L))
                .thenReturn(ItemDto.builder().id(50L).build());

        mockMvc.perform(delete("/items/17")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(50L), Long.class));

        Mockito.verify(itemService, Mockito.times(1))
                .deleteItem(17L, 10L);
    }
}
