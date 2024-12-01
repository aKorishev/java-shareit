package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.rest.RestQueryBuilder;
import ru.practicum.shareit.rest.RestQueryDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
public class BookingControllerTest {
    @Mock
    BookingService bookingService;

    @InjectMocks
    BookingController bookingController;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void requestPathWithoutUserIdInHead() throws Exception {
        List<MockHttpServletRequestBuilder> needIdUserFromHeadRequest =
                List.of(get("/bookings/1"),
                        get("/bookings"),
                        get("/bookings/owner"),
                        post("/bookings"),
                        patch("/bookings/1?approved=true"));

        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
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
    public void getBookingTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        var dto = BookingDto.builder()
                .id(10L)
                .build();

        var response = new ResponseEntity<>(
                        (Object) dto,
                        HttpStatus.OK);

        Mockito.when(bookingService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/1")
                        .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10L), Long.class));

        Mockito.verify(bookingService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.GET)
                        .path("/1")
                        .requestHeadUserId(1L)
                        .build());
    }

    @Test
    void getItemsForUserIdTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        var expectedResult = List.of(
                BookingDto.builder().id(1L).build(),
                BookingDto.builder().id(2L).build(),
                BookingDto.builder().id(3L).build());

        var response = new ResponseEntity<>(
                (Object) expectedResult,
                HttpStatus.OK);

        Mockito.when(bookingService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id",is(1L), Long.class))
                .andExpect(jsonPath("$.[1].id",is(2L), Long.class))
                .andExpect(jsonPath("$.[2].id",is(3L), Long.class));

        Mockito.verify(bookingService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.GET)
                        .requestHeadUserId(10L)
                        .build());
    }

    @Test
    void getItemsForItemOwnerIdTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        var expectedResult = List.of(
                BookingDto.builder().id(1L).build(),
                BookingDto.builder().id(2L).build(),
                BookingDto.builder().id(3L).build());

        var response = new ResponseEntity<>(
                (Object) expectedResult,
                HttpStatus.OK);

        Mockito.when(bookingService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id",is(1L), Long.class))
                .andExpect(jsonPath("$.[1].id",is(2L), Long.class))
                .andExpect(jsonPath("$.[2].id",is(3L), Long.class));

        Mockito.verify(bookingService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.GET)
                        .path("/owner")
                        .requestHeadUserId(10L)
                        .build());
    }

    @Test
    void postItemTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        var dto = BookingDto.builder()
                .id(50L)
                .build();

        var response = new ResponseEntity<>(
                (Object) BookingDto.builder().id(17L).build(),
                HttpStatus.OK);

        Mockito.when(bookingService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 10L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsBytes(dto))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(17L), Long.class));

        Mockito.verify(bookingService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.POST)
                        .requestHeadUserId(10L)
                        .body(dto)
                        .build());
    }

    @Test
    void postItemWithoutBodyTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().is(400));
    }

    @Test
    void patchItemApprovedTrueTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        var dto = BookingDto.builder()
                .id(15L)
                .status(BookingStatus.APPROVED)
                .build();

        var response = new ResponseEntity<>(
                (Object) dto,
                HttpStatus.OK);

        Mockito.when(bookingService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/bookings/10?approved=true")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",is(BookingStatus.APPROVED.name()), String.class));

        Mockito.verify(bookingService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.PATCH)
                        .path("/10?approved=true")
                        .requestHeadUserId(10L)
                        .build());
    }

    @Test
    void patchItemApprovedFalseTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        var dto = BookingDto.builder()
                .id(10L)
                .status(BookingStatus.REJECTED)
                .build();

        var response = new ResponseEntity<>(
                (Object) dto,
                HttpStatus.OK);

        Mockito.when(bookingService.executeQuery(Mockito.any(RestQueryDto.class)))
                .thenReturn(response);

        mockMvc.perform(patch("/bookings/10?approved=false")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",is(BookingStatus.REJECTED.name()), String.class));

        Mockito.verify(bookingService, Mockito.times(1))
                .executeQuery(RestQueryBuilder.builder()
                        .method(HttpMethod.PATCH)
                        .path("/10?approved=false")
                        .requestHeadUserId(10L)
                        .build());
    }

    @Test
    void patchItemWithoutParamsTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        mockMvc.perform(patch("/bookings/100")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().is(400));
    }
}
