package ru.practicum.shareit.user;

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
        return userService.findUserId(id);
    }

    @PostMapping
    public UserDto postUser(@RequestBody UserDto user) {
        return userService.updateUser(user);
    }

    @PutMapping
    public UserDto putUser(@RequestBody UserDto user) {
        return userService.updateUser(user);
    }

    @PatchMapping("/{id}")
    public UserDto patchUser(@PathVariable long id, @RequestBody UserToUpdateDto user) {
        return userService.updateUser(id, user);
    }

    @DeleteMapping("/{id}")
    public UserDto deleteUser(@PathVariable long id) {
        return userService.deleteUser(id);
    }
}
