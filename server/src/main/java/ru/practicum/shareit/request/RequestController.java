package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.RequestDto;

import java.util.List;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class RequestController {
    private final RequestService requestService;

    @GetMapping
    public List<RequestDto> getSelfRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.findRequestsByUserId(userId, true);
    }

    @GetMapping("/{id}")
    public RequestDto getRequestById(@PathVariable long id) {
        return requestService.findRequestById(id);
    }

    @GetMapping("/all")
    public List<RequestDto> getOtherUserRequests(@RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.findRequestsByUserId(userId, false);
    }

    @PostMapping
    public RequestDto postRequestDto(
            @RequestBody RequestDto requestDto,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return requestService.createRequest(requestDto, userId);
    }
}
