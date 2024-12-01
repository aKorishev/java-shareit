package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemToUpdateDto;
import ru.practicum.shareit.rest.RestQueryBuilder;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public ResponseEntity<Object> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        var restQuery = RestQueryBuilder.builder()
                .method(HttpMethod.GET)
                .requestHeadUserId(userId)
                .build();

        return itemService.executeQuery(restQuery);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getItem(@PathVariable long id) {
        var restQuery = RestQueryBuilder.builder()
                .method(HttpMethod.GET)
                .path("/" + id)
                .build();

        return itemService.executeQuery(restQuery);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> findItemsByText(@RequestParam String text) {
        var restQuery = RestQueryBuilder.builder()
                .method(HttpMethod.GET)
                .path("/search?text=" + text)
                .build();

        return itemService.executeQuery(restQuery);
    }

    @PostMapping
    public ResponseEntity<Object> postItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        var restQuery = RestQueryBuilder.builder()
                .method(HttpMethod.POST)
                .requestHeadUserId(userId)
                .body(itemDto)
                .build();

        return itemService.executeQuery(restQuery);
    }

    @PostMapping("/{id}/comment")
    public ResponseEntity<Object> postComment(
            @PathVariable long id,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody CommentDto commentDto) {
        var restQuery = RestQueryBuilder.builder()
                .method(HttpMethod.POST)
                .path("/" + id + "/comment")
                .requestHeadUserId(userId)
                .body(commentDto)
                .build();

        return itemService.executeQuery(restQuery);
    }

    @PutMapping()
    public ResponseEntity<Object> putItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        var restQuery = RestQueryBuilder.builder()
                .method(HttpMethod.PUT)
                .requestHeadUserId(userId)
                .body(itemDto)
                .build();

        return itemService.executeQuery(restQuery);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchItem(
            @PathVariable long id,
            @Valid @RequestBody ItemToUpdateDto item,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        var restQuery = RestQueryBuilder.builder()
                .method(HttpMethod.PATCH)
                .path("/" + id)
                .requestHeadUserId(userId)
                .body(item)
                .build();

        return itemService.executeQuery(restQuery);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteItem(
            @PathVariable long id,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        var restQuery = RestQueryBuilder.builder()
                .method(HttpMethod.DELETE)
                .path("/" + id)
                .requestHeadUserId(userId)
                .build();

        return itemService.executeQuery(restQuery);
    }
}
