package ru.practicum.shareit.user;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.dto.UserToUpdateDto;


@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping("/{id}")
    public UserDto getUser(@PathVariable long id) {
        return userService.getUser(id);
    }

    @PostMapping
    public UserDto postUser(@Valid @RequestBody UserDto user) {
        var newUserId = userService.createUser(user);

        return userService.getUser(newUserId);
    }

    @PutMapping
    public UserDto putUser(@Valid @RequestBody UserDto user) {
        userService.updateUser(user);

        return userService.getUser(user.getId());
    }

    @PatchMapping("/{id}")
    public UserDto patchUser(@PathVariable long id, @RequestBody @Valid UserToUpdateDto user) {
        userService.updateUser(id, user);

        return userService.getUser(id);
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable long id) {
        return userService.deleteUser(id);
    }
}
