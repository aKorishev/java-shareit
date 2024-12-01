package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.rest.RestQueryBuilder;
import ru.practicum.shareit.rest.RestService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserToUpdateDto;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public ResponseEntity<Object> getUser(@PathVariable long id) {
        var query = RestQueryBuilder.builder()
                .method(HttpMethod.GET)
                .path("/" + id)
                .build();

        return userService.executeQuery(query);
    }

    @PostMapping
    public ResponseEntity<Object> postUser(@Valid @RequestBody UserDto user) {
        var query = RestQueryBuilder.builder()
                .method(HttpMethod.POST)
                .body(user)
                .build();

        return userService.executeQuery(query);
    }

    @PutMapping
    public ResponseEntity<Object> putUser(@Valid @RequestBody UserDto user) {
        var query = RestQueryBuilder.builder()
                .method(HttpMethod.PUT)
                .body(user)
                .build();

        return userService.executeQuery(query);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> patchUser(
            @PathVariable long id,
            @RequestBody @Valid UserToUpdateDto user) {
        var query = RestQueryBuilder.builder()
                .method(HttpMethod.PATCH)
                .path("/" + id)
                .body(user)
                .build();

        return userService.executeQuery(query);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> deleteUser(@PathVariable long id) {
        var query = RestQueryBuilder.builder()
                .method(HttpMethod.DELETE)
                .path("/" + id)
                .build();

        return userService.executeQuery(query);
    }
}
