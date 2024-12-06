package ru.practicum.shareit.request;

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
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.rest.RestQueryBuilder;
import ru.practicum.shareit.rest.RestQueryDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class RequestControllerTest {
    @Mock
    RequestService requestService;

    @InjectMocks
    RequestController requestController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void requestPathWithoutUserIdInHead() throws Exception {
        List<MockHttpServletRequestBuilder> needIdUserFromHeadRequest =
                List.of(get("/requests"),
                        get("/requests/all"),
                        post("/requests"));

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(requestController)
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
    public void getSelfRequestsTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .build();

        List<RequestDto> expectedRequests = List.of(
                RequestDto.builder().id(13L).build(),
                RequestDto.builder().id(24L).build(),
                RequestDto.builder().id(37L).build()
        );

        var response = new ResponseEntity<>(
                (Object)expectedRequests,
                HttpStatus.OK);

        Mockito.when(requestService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        mockMvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(13L), Long.class))
                .andExpect(jsonPath("$[1].id", is(24L), Long.class))
                .andExpect(jsonPath("$[2].id", is(37L), Long.class));

        Mockito.verify(requestService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.GET)
                        .requestHeadUserId(7L)
                        .build());
    }

    @Test
    void getRequestByIdTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .build();

        var expectedResult = RequestDto.builder().id(13L).build();

        var response = new ResponseEntity<>(
                (Object) expectedResult,
                HttpStatus.OK);

        Mockito.when(requestService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        mockMvc.perform(get("/requests/23"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(13L), Long.class));

        Mockito.verify(requestService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.GET)
                        .path("/23")
                        .build());
    }

    @Test
    void getOtherUserRequestsTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .build();

        List<RequestDto> expectedRequests = List.of(
                RequestDto.builder().id(13L).build(),
                RequestDto.builder().id(24L).build(),
                RequestDto.builder().id(37L).build()
        );

        var response = new ResponseEntity<>(
                (Object) expectedRequests,
                HttpStatus.OK);

        Mockito.when(requestService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        mockMvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 7L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id", is(13L), Long.class))
                .andExpect(jsonPath("$[1].id", is(24L), Long.class))
                .andExpect(jsonPath("$[2].id", is(37L), Long.class));

        Mockito.verify(requestService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.GET)
                        .path("/all")
                        .requestHeadUserId(7L)
                        .build());
    }

    @Test
    void postRequestDtoTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .build();

        var response = new ResponseEntity<>(
                (Object) RequestDto.builder().id(50L).build(),
                HttpStatus.OK);

        Mockito.when(requestService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        var dto = RequestDto.builder()
                .id(13L)
                .description("la")
                .build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 10L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsBytes(dto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(50L), Long.class));

        Mockito.verify(requestService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.POST)
                        .requestHeadUserId(10L)
                        .body(dto)
                        .build());
    }

    @Test
    void postRequestWithoutBodyTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(requestController)
                .build();

        mockMvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().is(400));
    }
}
