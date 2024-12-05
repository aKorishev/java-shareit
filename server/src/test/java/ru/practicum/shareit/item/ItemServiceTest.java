package ru.practicum.shareit.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.booking.BookingStorage;
import ru.practicum.shareit.exceptions.NotFoundException;
import ru.practicum.shareit.exceptions.NotValidException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemToUpdateDto;
import ru.practicum.shareit.item.storage.ItemEntity;
import ru.practicum.shareit.request.RequestStorage;
import ru.practicum.shareit.request.storage.RequestEntity;
import ru.practicum.shareit.user.UserStorage;
import ru.practicum.shareit.user.storage.UserEntity;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

@SpringBootTest
public class ItemServiceTest {
    @Mock
    UserStorage userStorage;
    @Mock
    ItemStorage itemStorage;
    @Mock
    RequestStorage requestStorage;
    @Mock
    BookingStorage bookingStorage;

    @InjectMocks
    ItemService itemService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    public void findItemIdWhileNotFoundUserTest() {
        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            itemService.findItem(10L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Вещь не найдена", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Вещь не найдена\")");
    }

    @Test
    public void findItemIdTest() {
        var itemEntity = new ItemEntity();
        itemEntity.setId(54L);;

        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.of(itemEntity));

        var actualItemDto = itemService.findItem(4L);

        var expectedItemDto = itemEntity.toDto();

        Assertions.assertEquals(expectedItemDto, actualItemDto);

        Mockito.verify(itemStorage, Mockito.times(1))
                .findItem(4L);
    }

    @Test
    public void createItemWhileNotFoundUserIdTest() {
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            itemService.createItem(ItemDto.builder().build(), 14L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Пользователь не найден", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Пользователь не найден\")");
    }

    @Test
    public void createItemWhileNotFoundRequestIdTest() {
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(new UserEntity()));

        Mockito.when(requestStorage.findRequestById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            itemService.createItem(ItemDto.builder().requestId(1L).build(), 14L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Запрос не найден", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Запрос не найден\")");
    }

    @Test
    public void createItemWhileRequestIdIsNullTest() {
        var userEntity = new UserEntity();
        userEntity.setId(10L);

        RequestEntity requestEntity = null;

        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(userEntity));

        Mockito.when(requestStorage.findRequestById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        var itemDto = ItemDto.builder()
                .id(14L)
                .available(true)
                .build();

        var itemEntity = ItemMapper.toEntity(itemDto, userEntity, null);

        var actualItemDto = itemService.createItem(itemDto, 16L);

        var expectedItemDto = itemDto.toBuilder()
                .comments(List.of())
                .build();

        Assertions.assertEquals(expectedItemDto, actualItemDto);

        Mockito.verify(userStorage, Mockito.times(1))
                .findUserId(16L);

        Mockito.verify(requestStorage, Mockito.never())
                .findRequestById(Mockito.anyLong());

        Mockito.verify(itemStorage, Mockito.times(1))
                .updateItem(itemEntity);
    }

    @Test
    public void createItemTest() {
        var userEntity = new UserEntity();
        userEntity.setId(10L);

        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setId(17L);

        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(userEntity));

        Mockito.when(requestStorage.findRequestById(Mockito.anyLong()))
                .thenReturn(Optional.of(requestEntity));

        var itemDto = ItemDto.builder()
                .id(14L)
                .available(true)
                .requestId(17L)
                .build();

        var itemEntity = ItemMapper.toEntity(itemDto, userEntity, requestEntity);

        var actualItemDto = itemService.createItem(itemDto, 16L);

        var expectedItemDto = itemDto.toBuilder()
                .comments(List.of())
                .build();

        Assertions.assertEquals(expectedItemDto, actualItemDto);

        Mockito.verify(userStorage, Mockito.times(1))
                .findUserId(16L);

        Mockito.verify(requestStorage, Mockito.times(1))
                .findRequestById(17L);

        Mockito.verify(itemStorage, Mockito.times(1))
                .updateItem(itemEntity);
    }

    @Test
    public void updateItemWhileNotFoundItemIdTest() {
        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            itemService.updateItem(ItemDto.builder().id(2L).build(), 14L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Вещь не найдена", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Вещь не найдена\")");
    }

    @Test
    public void updateItemWhileUserIdIsNotOwnerTest() {
        var ownerId = new UserEntity();
        ownerId.setId(11L);

        var itemEntity = new ItemEntity();
        itemEntity.setId(13L);
        itemEntity.setOwner(ownerId);

        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.of(itemEntity));

        try {
            itemService.updateItem(ItemDto.builder().id(10L).build(), 14L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Владелец не найден", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Владелец не найден\")");
    }

    @Test
    public void updateItemWhileNotFoundRequestIdTest() {
        var ownerId = new UserEntity();
        ownerId.setId(11L);

        var itemEntity = new ItemEntity();
        itemEntity.setId(13L);
        itemEntity.setOwner(ownerId);

        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.of(itemEntity));

        try {
            itemService.updateItem(ItemDto.builder().id(1L).requestId(1L).build(), 11L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Запрос не найден", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Запрос не найден\")");
    }

    @Test
    public void updateItemWhileRequestIdIsNullTest() {
        var ownerId = new UserEntity();
        ownerId.setId(11L);

        var itemEntity = new ItemEntity();
        itemEntity.setId(13L);
        itemEntity.setOwner(ownerId);
        itemEntity.setAvailable(true);

        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.of(itemEntity));

        Mockito.when(requestStorage.findRequestById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        var itemDto = ItemDto.builder()
                .id(13L)
                .available(true)
                .build();

        var actualItemDto = itemService.updateItem(itemDto, 11L);

        var expectedItemDto = ItemMapper.toDto(itemEntity);

        Assertions.assertEquals(expectedItemDto, actualItemDto);

        Mockito.verify(requestStorage, Mockito.never())
                .findRequestById(Mockito.anyLong());

        Mockito.verify(itemStorage, Mockito.times(1))
                .updateItem(itemEntity);
    }

    @Test
    public void updateItemItemTest() {
        RequestEntity requestEntity = new RequestEntity();
        requestEntity.setId(17L);

        Mockito.when(requestStorage.findRequestById(Mockito.anyLong()))
                .thenReturn(Optional.of(requestEntity));
        var ownerId = new UserEntity();
        ownerId.setId(11L);

        var itemEntity = new ItemEntity();
        itemEntity.setId(13L);
        itemEntity.setOwner(ownerId);
        itemEntity.setAvailable(true);
        itemEntity.setRequest(requestEntity);

        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.of(itemEntity));

        var itemDto = ItemDto.builder()
                .id(13L)
                .available(true)
                .requestId(17L)
                .build();

        var actualItemDto = itemService.updateItem(itemDto, 11L);

        var expectedItemDto = ItemMapper.toDto(itemEntity);

        Assertions.assertEquals(expectedItemDto, actualItemDto);

        Mockito.verify(requestStorage, Mockito.times(1))
                .findRequestById(17L);

        Mockito.verify(itemStorage, Mockito.times(1))
                .updateItem(itemEntity);
    }

    @Test
    public void updateItemForRequestWhileNotFoundItemIdTest() {
        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            itemService.updateItem(1L, ItemToUpdateDto.builder().build(), 2L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Вещь не найдена", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Вещь не найдена\")");
    }

    @Test
    public void updateItemForRequestWhileUserIdIsNotEqualOwnerTest() {
        var ownerEntity = new UserEntity();
        ownerEntity.setId(11L);

        var itemEntity = new ItemEntity();
        itemEntity.setId(12L);
        itemEntity.setOwner(ownerEntity);

        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.of(itemEntity));

        try {
            itemService.updateItem(1L, ItemToUpdateDto.builder().build(), 2L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Вещь не доступна", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Вещь не доступна\")");
    }

    @Test
    public void updateItemForRequestChangeNameTest() {
        var ownerEntity = new UserEntity();
        ownerEntity.setId(11L);

        var itemEntity = new ItemEntity();
        itemEntity.setId(12L);
        itemEntity.setOwner(ownerEntity);
        itemEntity.setName("test");
        itemEntity.setDescription("test");
        itemEntity.setAvailable(false);

        var expectedItemDto = ItemMapper.toDto(itemEntity).toBuilder()
                .name("updated")
                .build();

        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.of(itemEntity));

        var actualItemDto = itemService.updateItem(
                1L,
                ItemToUpdateDto.builder().name("updated").build(),
                11L);

        Assertions.assertEquals(expectedItemDto, actualItemDto);

        itemEntity = new ItemEntity();
        itemEntity.setId(12L);
        itemEntity.setOwner(ownerEntity);
        itemEntity.setName("updated");
        itemEntity.setDescription("test");
        itemEntity.setAvailable(false);

        Mockito.verify(itemStorage, Mockito.times(1))
                .updateItem(itemEntity);
    }

    @Test
    public void updateItemForRequestChangeDescriptionTest() {
        var ownerEntity = new UserEntity();
        ownerEntity.setId(11L);

        var itemEntity = new ItemEntity();
        itemEntity.setId(12L);
        itemEntity.setOwner(ownerEntity);
        itemEntity.setName("test");
        itemEntity.setDescription("test");
        itemEntity.setAvailable(false);

        var expectedItemDto = ItemMapper.toDto(itemEntity).toBuilder()
                .description("updated")
                .build();

        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.of(itemEntity));

        var actualItemDto = itemService.updateItem(
                1L,
                ItemToUpdateDto.builder().description("updated").build(),
                11L);

        Assertions.assertEquals(expectedItemDto, actualItemDto);

        itemEntity = new ItemEntity();
        itemEntity.setId(12L);
        itemEntity.setOwner(ownerEntity);
        itemEntity.setName("test");
        itemEntity.setDescription("updated");
        itemEntity.setAvailable(false);

        Mockito.verify(itemStorage, Mockito.times(1))
                .updateItem(itemEntity);
    }

    @Test
    public void updateItemForRequestChangeSetAvailableTrueTest() {
        var ownerEntity = new UserEntity();
        ownerEntity.setId(11L);

        var itemEntity = new ItemEntity();
        itemEntity.setId(12L);
        itemEntity.setOwner(ownerEntity);
        itemEntity.setName("test");
        itemEntity.setDescription("test");
        itemEntity.setAvailable(false);

        var expectedItemDto = ItemMapper.toDto(itemEntity).toBuilder()
                .available(true)
                .build();

        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.of(itemEntity));

        var actualItemDto = itemService.updateItem(
                1L,
                ItemToUpdateDto.builder().available(true).build(),
                11L);

        Assertions.assertEquals(expectedItemDto, actualItemDto);

        itemEntity = new ItemEntity();
        itemEntity.setId(12L);
        itemEntity.setOwner(ownerEntity);
        itemEntity.setName("test");
        itemEntity.setDescription("test");
        itemEntity.setAvailable(true);

        Mockito.verify(itemStorage, Mockito.times(1))
                .updateItem(itemEntity);
    }

    @Test
    public void updateItemForRequestChangeSetAvailableFalseTest() {
        var ownerEntity = new UserEntity();
        ownerEntity.setId(11L);

        var itemEntity = new ItemEntity();
        itemEntity.setId(12L);
        itemEntity.setOwner(ownerEntity);
        itemEntity.setName("test");
        itemEntity.setDescription("test");
        itemEntity.setAvailable(true);

        var expectedItemDto = ItemMapper.toDto(itemEntity).toBuilder()
                .available(false)
                .build();

        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.of(itemEntity));

        var actualItemDto = itemService.updateItem(
                1L,
                ItemToUpdateDto.builder().available(false).build(),
                11L);

        Assertions.assertEquals(expectedItemDto, actualItemDto);

        itemEntity = new ItemEntity();
        itemEntity.setId(12L);
        itemEntity.setOwner(ownerEntity);
        itemEntity.setName("test");
        itemEntity.setDescription("test");
        itemEntity.setAvailable(false);

        Mockito.verify(itemStorage, Mockito.times(1))
                .updateItem(itemEntity);
    }

    @Test
    public void deleteItemWhileNotFoundItemIdTest() {
        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            itemService.deleteItem(1L, 2L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Вещь не найдена", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Вещь не найдена\")");
    }

    @Test
    public void deleteItemWhileUserIdIsNotEqualOwnerTest() {
        var ownerEntity = new UserEntity();
        ownerEntity.setId(11L);

        var itemEntity = new ItemEntity();
        itemEntity.setId(12L);
        itemEntity.setOwner(ownerEntity);

        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.of(itemEntity));

        try {
            itemService.deleteItem(1L, 2L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Вещь не доступна", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Вещь не доступна\")");
    }

    @Test
    public void deleteItemTest() {
        var ownerEntity = new UserEntity();
        ownerEntity.setId(11L);

        var itemEntity = new ItemEntity();
        itemEntity.setId(12L);
        itemEntity.setOwner(ownerEntity);

        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.of(itemEntity));

        var expectedItemDto = ItemMapper.toDto(itemEntity);

        var actualItemDto = itemService.deleteItem(1L, 11L);

        Assertions.assertEquals(expectedItemDto, actualItemDto);

        Mockito.verify(itemStorage, Mockito.times(1))
                .deleteItem(1L);

        Mockito.verify(itemStorage, Mockito.times(1))
                .findItem(1L);
    }

    @Test
    public void getItemsWhileNotFoundUserIdTest() {
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            itemService.getItems(2L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Пользователь не найден", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Пользователь не найден\")");
    }

    @Test
    public void getItemsTest() {
        var ownerEntity = new UserEntity();
        ownerEntity.setId(1L);

        var item1 = new ItemEntity();
        item1.setId(11L);
        item1.setOwner(ownerEntity);

        var item2 = new ItemEntity();
        item2.setId(12L);
        item2.setOwner(ownerEntity);

        ownerEntity.setItems(List.of(item1, item2));

        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(ownerEntity));

        var expectedList = Stream.of(item1, item2)
                .map(ItemMapper::toDto)
                .toList();

        var actualList = itemService.getItems(14L);

        Assertions.assertEquals(expectedList, actualList);

        Mockito.verify(userStorage, Mockito.times(1))
                .findUserId(14L);
    }

    @Test
    public void findFreeItemsByTextWhileNullTextTest() {
        var item1 = new ItemEntity();
        item1.setId(11L);

        var item2 = new ItemEntity();
        item2.setId(12L);

        Mockito.when(itemStorage.findItemsByTextAndStatus(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(List.of(item1, item2));

        Assertions.assertEquals(0, itemService.findFreeItemsByText(null, true).size());

        Mockito.verify(itemStorage, Mockito.never())
                .findItemsByTextAndStatus(Mockito.anyString(), Mockito.anyBoolean());
    }

    @Test
    public void findFreeItemsByTextWhileBlankTextTest() {
        var item1 = new ItemEntity();
        item1.setId(11L);

        var item2 = new ItemEntity();
        item2.setId(12L);

        Mockito.when(itemStorage.findItemsByTextAndStatus(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(List.of(item1, item2));

        Assertions.assertEquals(0, itemService.findFreeItemsByText("", true).size());

        Mockito.verify(itemStorage, Mockito.never())
                .findItemsByTextAndStatus(Mockito.anyString(), Mockito.anyBoolean());
    }

    @Test
    public void findFreeItemsByTextWhileAvailableIsTrueTest() {
        var item1 = new ItemEntity();
        item1.setId(11L);

        var item2 = new ItemEntity();
        item2.setId(12L);

        Mockito.when(itemStorage.findItemsByTextAndStatus(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(List.of(item1, item2));

        itemService.findFreeItemsByText("a", true);

        Mockito.verify(itemStorage, Mockito.never())
                .findItemsByTextAndStatus("a", false);

        Mockito.verify(itemStorage, Mockito.times(1))
                .findItemsByTextAndStatus("a", true);
    }

    @Test
    public void findFreeItemsByTextWhileAvailableIsFalseTest() {
        var item1 = new ItemEntity();
        item1.setId(11L);

        var item2 = new ItemEntity();
        item2.setId(12L);

        Mockito.when(itemStorage.findItemsByTextAndStatus(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(List.of(item1, item2));

        itemService.findFreeItemsByText("a", false);

        Mockito.verify(itemStorage, Mockito.never())
                .findItemsByTextAndStatus("a", true);

        Mockito.verify(itemStorage, Mockito.times(1))
                .findItemsByTextAndStatus("a", false);
    }

    @Test
    public void findFreeItemsByTextTest() {
        var item1 = new ItemEntity();
        item1.setId(11L);

        var item2 = new ItemEntity();
        item2.setId(12L);

        Mockito.when(itemStorage.findItemsByTextAndStatus(Mockito.anyString(), Mockito.anyBoolean()))
                .thenReturn(List.of(item1, item2));

        var expectedList = Stream.of(item1, item2)
                .map(ItemMapper::toDto)
                .toList();

        var actualList = itemService.findFreeItemsByText("a", false);

        Assertions.assertEquals(expectedList, actualList);
    }

    @Test
    public void addCommentWhileNotFoundUserIdTest() {
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            itemService.addComment(
                    CommentDto.builder().build(),
                    1L,
                    2L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Пользователь не найден", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Пользователь не найден\")");
    }

    @Test
    public void addCommentWhileNotFoundItemIdTest() {
        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(new UserEntity()));

        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        try {
            itemService.addComment(
                    CommentDto.builder().build(),
                    1L,
                    2L);
        } catch (NotFoundException ex) {
            Assertions.assertEquals("Вешь не найдена", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotFoundException(\"Вешь не найдена\")");
    }

    @Test
    public void addCommentWhileUserNotUsedItemTest() {
        var userEntity = new UserEntity();
        userEntity.setId(11L);

        Mockito.when(userStorage.findUserId(Mockito.anyLong()))
                .thenReturn(Optional.of(userEntity));

        Mockito.when(itemStorage.findItem(Mockito.anyLong()))
                .thenReturn(Optional.of(new ItemEntity()));

        Mockito.when(
                bookingStorage.existsByBookerIdAndItemIdAndAfterEnd(Mockito.anyLong(), Mockito.anyLong()))
                .thenReturn(false);

        try {
            itemService.addComment(
                    CommentDto.builder().build(),
                    1L,
                    2L);
        } catch (NotValidException ex) {
            Assertions.assertEquals("Пользователь не брал вещь, не может оставить комментарий", ex.getMessage());
            return;
        }

        Assertions.fail("Expected NotValidException(\"Пользователь не брал вещь, не может оставить комментарий\")");
    }
}
