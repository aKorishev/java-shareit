package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.rest.RestQueryBuilder;
import ru.practicum.shareit.booking.dto.BookingDto;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getBooking(
            @PathVariable long id,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        var request = RestQueryBuilder.builder()
                .method(HttpMethod.GET)
                .path("/" + id)
                .requestHead("X-Sharer-User-Id", String.valueOf(userId))
                .build();

        return bookingService.executeQuery(request);
    }

    @GetMapping
    public ResponseEntity<Object> getBookingForUserId(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        var request = RestQueryBuilder.builder()
                .method(HttpMethod.GET)
                .requestHead("X-Sharer-User-Id", String.valueOf(userId))
                .build();

        return bookingService.executeQuery(request);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingForItemOwnerId(
            @RequestHeader("X-Sharer-User-Id") long ownerId) {
        var request = RestQueryBuilder.builder()
                .method(HttpMethod.GET)
                .path("/owner")
                .requestHead("X-Sharer-User-Id", String.valueOf(ownerId))
                .build();

        return bookingService.executeQuery(request);
    }

    @PostMapping
    public ResponseEntity<Object> postBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        var request = RestQueryBuilder.builder()
                .method(HttpMethod.POST)
                .requestHead("X-Sharer-User-Id", String.valueOf(userId))
                .body(bookingDto)
                .build();

        return bookingService.executeQuery(request);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchBooking(
            @PathVariable long id,
            @RequestParam(name = "approved") boolean approved,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        var request = RestQueryBuilder.builder()
                .method(HttpMethod.PATCH)
                .path("/" + id + "?approved=" + (approved ? "true" : "false"))
                .requestHead("X-Sharer-User-Id", String.valueOf(userId))
                .build();

        return bookingService.executeQuery(request);
    }
}
