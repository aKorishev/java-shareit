package ru.practicum.shareit.rest;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Value;
import org.springframework.http.HttpMethod;

import java.util.Optional;

@Builder(access = AccessLevel.PACKAGE)
@Value
public class RestQueryDto {
        HttpMethod httpMethod;
        @Builder.Default
        String path = "";
        @Builder.Default
        Optional<RestQueryHead> requestHead = Optional.empty();
        @Builder.Default
        Optional<Object> body = Optional.empty();
}
