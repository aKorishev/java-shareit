package ru.practicum.shareit.rest;

import lombok.Value;
import org.springframework.http.HttpMethod;

import java.util.Optional;

public class RestQueryBuilder {
    private final RestQueryDto.RestQueryDtoBuilder builder;

    private RestQueryBuilder(RestQueryDto.RestQueryDtoBuilder builder) {
        this.builder = builder;
    }

    public static RestQueryBuilder builder() {
        return new RestQueryBuilder(RestQueryDto.builder());
    }

    public RestQueryDto build() {
        return builder.build();
    }

    public RestQueryBuilder method(HttpMethod method) {
        builder.httpMethod(method);

        return this;
    }

    public RestQueryBuilder path(String path) {
        builder.path(path);

        return this;
    }

    public RestQueryBuilder requestHead(RestQueryHead restQueryHead) {
        builder.requestHead(Optional.of(restQueryHead));

        return this;
    }

    public RestQueryBuilder requestHead(String name, String value) {
        builder.requestHead(Optional.of(new RestQueryHead(name, value)));

        return this;
    }

    public RestQueryBuilder requestHeadUserId(long userId) {
        builder.requestHead(Optional.of(new RestQueryHead("X-Sharer-User-Id", String.valueOf(userId))));

        return this;
    }

    public RestQueryBuilder body(Object body) {
        builder.body(Optional.of(body));

        return this;
    }
}
