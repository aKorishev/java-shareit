package ru.practicum.shareit.request.dto;

import ru.practicum.shareit.request.ItemRequest;

public class ItemRequestMapper {
    public ItemRequestDto toDto(ItemRequest itemRequest) {
        return ItemRequestDto
                .builder()
                .id(itemRequest.getId())
                .userId(itemRequest.getUserId())
                .text(itemRequest.getText())
                .build();
    }

    public ItemRequest toModel(ItemRequestDto itemRequestDto) {
        return ItemRequest
                .builder()
                .id(itemRequestDto.getId())
                .userId(itemRequestDto.getUserId())
                .text(itemRequestDto.getText())
                .build();
    }
}
