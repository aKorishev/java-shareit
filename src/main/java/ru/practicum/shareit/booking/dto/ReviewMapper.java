package ru.practicum.shareit.booking.dto;

import ru.practicum.shareit.booking.Review;

public class ReviewMapper {
    public ReviewDto toDto(Review review) {
        return ReviewDto
                .builder()
                .id(review.getId())
                .bookingId(review.getBookingId())
                .text(review.getText())
                .isSuccess(review.isSuccess())
                .build();
    }

    public Review toModel(ReviewDto reviewDto) {
        return Review
                .builder()
                .id(reviewDto.getId())
                .bookingId(reviewDto.getBookingId())
                .text(reviewDto.getText())
                .isSuccess(reviewDto.isSuccess())
                .build();
    }
}
