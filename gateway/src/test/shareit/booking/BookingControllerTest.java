package ru.practicum.shareit.booking;

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
import ru.practicum.shareit.booking.BookingController;
import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.hamcrest.Matchers.is;

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

        Mockito.when(bookingService.getBooking(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(dto);

        mockMvc.perform(get("/bookings/1")
                .header("X-Sharer-User-Id", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(10L), Long.class));
    }

    @Test
    void getItemParsingRequestId() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        mockMvc.perform(get("/bookings/100")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk());

        Mockito.verify(bookingService, Mockito.times(1))
                .getBooking(100, 10);
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

        Mockito.when(bookingService.getBookingForUserId(Mockito.anyLong()))
                        .thenReturn(expectedResult);

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id",is(1L), Long.class))
                .andExpect(jsonPath("$.[1].id",is(2L), Long.class))
                .andExpect(jsonPath("$.[2].id",is(3L), Long.class));
    }

    @Test
    void getItemsForUserIdParsingRequestTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk());

        Mockito.verify(bookingService, Mockito.times(1))
                .getBookingForUserId(10);
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

        Mockito.when(bookingService.getBookingsForItemOwnerId(Mockito.anyLong()))
                .thenReturn(expectedResult);

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].id",is(1L), Long.class))
                .andExpect(jsonPath("$.[1].id",is(2L), Long.class))
                .andExpect(jsonPath("$.[2].id",is(3L), Long.class));
    }

    @Test
    void getItemsForItemOwnerIdParsingRequestTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk());

        Mockito.verify(bookingService, Mockito.times(1))
                .getBookingsForItemOwnerId(10);
    }

    @Test
    void postItemTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        Mockito.when(bookingService.createBooking(Mockito.any(BookingDto.class), Mockito.anyLong()))
                .thenReturn(BookingDto.builder().id(50L).build());

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 10L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsBytes(BookingDto.builder().build()))
                        .contentType("application/json"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(50L), Long.class));
    }

    @Test
    void postItemParsingRequestTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        var dto = BookingDto.builder().build();

        mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 10L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .content(objectMapper.writeValueAsBytes(dto))
                        .contentType("application/json"))
                .andExpect(status().isOk());

        Mockito.verify(bookingService, Mockito.times(1))
                .createBooking(dto, 10L);
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

        Mockito.when(bookingService.setBookingStatus(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong()))
                .thenReturn(BookingDto.builder().status(BookingStatus.APPROVED).build());

        mockMvc.perform(patch("/bookings/10?approved=true")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",is(BookingStatus.APPROVED.name()), String.class));
    }

    @Test
    void patchItemApprovedFalseTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        Mockito.when(bookingService.setBookingStatus(Mockito.anyLong(), Mockito.anyBoolean(), Mockito.anyLong()))
                .thenReturn(BookingDto.builder().status(BookingStatus.REJECTED).build());

        mockMvc.perform(patch("/bookings/10?approved=false")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status",is(BookingStatus.REJECTED.name()), String.class));
    }

    @Test
    void patchItemParsingRequestTest() throws Exception {
        MockMvc mockMvc = MockMvcBuilders
                .standaloneSetup(bookingController)
                .build();

        mockMvc.perform(patch("/bookings/100?approved=true")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk());

        mockMvc.perform(patch("/bookings/100?approved=false")
                        .header("X-Sharer-User-Id", 10L))
                .andExpect(status().isOk());

        Mockito.verify(bookingService, Mockito.times(1))
                .setBookingStatus(100, true, 10L);

        Mockito.verify(bookingService, Mockito.times(1))
                .setBookingStatus(100, false, 10L);
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
