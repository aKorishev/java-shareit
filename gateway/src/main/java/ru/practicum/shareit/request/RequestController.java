package ru.practicum.shareit.request;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;
import ru.practicum.shareit.rest.RestQueryBuilder;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @GetMapping
    public ResponseEntity<Object> getSelfRequests(
            @RequestHeader("X-Sharer-User-Id") long userId) {
        var query = RestQueryBuilder.builder()
                .method(HttpMethod.GET)
                .requestHead("X-Sharer-User-Id", String.valueOf(userId))
                .build();

        return requestService.executeQuery(query);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getRequestById(@PathVariable long id) {
        var query = RestQueryBuilder.builder()
                .method(HttpMethod.GET)
                .path("/" + id)
                .build();

        return requestService.executeQuery(query);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getOtherUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        var query = RestQueryBuilder.builder()
                .method(HttpMethod.GET)
                .path("/all")
                .requestHead("X-Sharer-User-Id", String.valueOf(userId))
                .build();

        return requestService.executeQuery(query);
    }

    @PostMapping
    public ResponseEntity<Object> postRequestDto(
            @Valid @RequestBody RequestDto requestDto,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        var query = RestQueryBuilder.builder()
                .method(HttpMethod.POST)
                .requestHead("X-Sharer-User-Id", String.valueOf(userId))
                .body(requestDto)
                .build();

        return requestService.executeQuery(query);
    }
}
