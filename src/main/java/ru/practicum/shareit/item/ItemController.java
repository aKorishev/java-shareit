package ru.practicum.shareit.item;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemToUpdateDto;

import java.util.List;


@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {
    private final ItemService itemService;

    @GetMapping
    public List<ItemDto> getItems(@RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.getItems(userId);
    }

    @GetMapping("/{id}")
    public ItemDto getItem(@PathVariable long id) {
        return itemService.getItem(id);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemsByText(@RequestParam String text) {
        return itemService.findFreeItemsByText(text, true);
    }

    @PostMapping
    public ItemDto postItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PostMapping("/{id}/comment")
    public CommentDto postComment(
            @PathVariable long id,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @Valid @RequestBody CommentDto commentDto) {
        return itemService.addComment(commentDto, id, userId);
    }

    @PutMapping()
    public ItemDto putItem(
            @Valid @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") long userId) {

        return itemService.updateItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(
            @PathVariable long id,
            @Valid @RequestBody ItemToUpdateDto item,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        itemService.updateItem(id, item, userId);

        return itemService.getItem(id);
    }

    @DeleteMapping("/{id}")
    public ItemDto deleteItem(@RequestParam long id,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.deleteItem(id, userId);
    }
}
