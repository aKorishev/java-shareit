package ru.practicum.shareit.booking;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping("/{id}")
    public BookingDto getItem(
            @PathVariable long id,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getBooking(id, userId);
    }

    @GetMapping
    public List<BookingDto> getItemsForUserId(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.getItemsForUserId(userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getItemsForItemOwnerId(
            @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return bookingService.getItemsForItemOwnerId(ownerId);
    }

    @PostMapping
    public BookingDto postItem(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{id}")
    public BookingDto patchItem(
            @PathVariable long id,
            @RequestParam(name = "approved") boolean approved,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.setBookingStatus(id, approved, userId);
    }
}
