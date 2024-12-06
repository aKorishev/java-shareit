package ru.practicum.shareit.item;

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
        return itemService.findItem(id);
    }

    @GetMapping("/search")
    public List<ItemDto> findItemsByText(@RequestParam String text) {
        return itemService.findFreeItemsByText(text, true);
    }

    @PostMapping
    public ItemDto postItem(
            @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.createItem(itemDto, userId);
    }

    @PostMapping("/{id}/comment")
    public CommentDto postComment(
            @PathVariable long id,
            @RequestHeader("X-Sharer-User-Id") long userId,
            @RequestBody CommentDto commentDto) {
        return itemService.addComment(commentDto, id, userId);
    }

    @PutMapping()
    public ItemDto putItem(
            @RequestBody ItemDto itemDto,
            @RequestHeader("X-Sharer-User-Id") long userId) {

        return itemService.updateItem(itemDto, userId);
    }

    @PatchMapping("/{id}")
    public ItemDto patchItem(
            @PathVariable long id,
            @RequestBody ItemToUpdateDto item,
            @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.updateItem(id, item, userId);
    }

    @DeleteMapping("/{id}")
    public ItemDto deleteItem(@PathVariable long id,
                              @RequestHeader("X-Sharer-User-Id") long userId) {
        return itemService.deleteItem(id, userId);
    }
}
