package ru.practicum.shareit.booking;

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
    public BookingDto getBooking(
            @PathVariable long id,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.findBooking(id, userId);
    }

    @GetMapping
    public List<BookingDto> getBookingForUserId(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.findBookingsForUserId(userId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingForItemOwnerId(
            @RequestHeader("X-Sharer-User-Id") long ownerId) {
        return bookingService.findBookingsForItemOwnerId(ownerId);
    }

    @PostMapping
    public BookingDto postBooking(
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody BookingDto bookingDto) {
        return bookingService.createBooking(bookingDto, userId);
    }

    @PatchMapping("/{id}")
    public BookingDto patchBooking(
            @PathVariable long id,
            @RequestParam(name = "approved") boolean approved,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return bookingService.setBookingStatus(id, approved, userId);
    }
}
