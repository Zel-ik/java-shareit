package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.mapper.CommentMapper;
import ru.practicum.shareit.item.model.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ItemServiceImplUnitTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemMapper itemMapper;
    @Mock
    private CommentMapper commentMapper;

    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void testCreateItem() {
        User user = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, user, "Name", "Description", true, null);

        ItemDto itemDtoFrom = new ItemDto(null, "Name", "Description", true, null,
                null, new ArrayList<>(), null);
        ItemDto itemDtoTo = new ItemDto(1L, "Name", "Description", true, null,
                null, new ArrayList<>(), null);

        when(itemMapper.mapFrom(itemDtoFrom, 1)).thenReturn(item);
        when(itemRepository.save(item)).thenReturn(item);
        when(itemMapper.mapTo(item, 1)).thenReturn(itemDtoTo);

        ItemDto result = itemService.createItem(itemDtoFrom, 1);

        assertNotNull(result);
        assertEquals("Name", result.getName());
        assertEquals("Description", result.getDescription());

        verify(itemMapper, times(1)).mapFrom(itemDtoFrom, 1);
        verify(itemRepository, times(1)).save(item);
        verify(itemMapper, times(1)).mapTo(item, 1);
    }

    @Test
    public void updateItemTest() {
        long itemId = 1L;
        long userId = 1L;

        User user = new User(1L, "test1@test.com", "Test1");
        Item itemBefore = new Item(1L, user, "Name", "Description", true, null);
        Item itemAfter = new Item(1L, user, "Update", "Description", true, null);

        ItemDto itemDtoFrom = new ItemDto(1L, "Update", "Description", true, null,
                null, new ArrayList<>(), null);
        ItemDto itemDtoTo = new ItemDto(1L, "Update", "Description", true, null,
                null, new ArrayList<>(), null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemBefore));
        when(itemMapper.mapFrom(itemDtoFrom, itemBefore)).thenReturn(itemAfter);
        when(itemMapper.mapTo(itemAfter, userId)).thenReturn(itemDtoTo);

        ItemDto result = itemService.updateItem(itemDtoFrom, itemId, userId);

        verify(itemRepository).findById(itemId);
        verify(itemMapper).mapFrom(itemDtoFrom, itemBefore);
        verify(itemMapper).mapTo(itemAfter, userId);

        assertNotNull(result);
    }

    @Test
    public void testUpdateItemWhenItemNotFound() {
        long itemId = 1L;
        long userId = 1L;

        ItemDto itemDto = new ItemDto();
        itemDto.setId(itemId);

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(itemDto, itemId, userId));

        verify(itemRepository).findById(itemId);
        verifyNoMoreInteractions(itemRepository, itemMapper, commentMapper, commentRepository);
    }

    @Test
    public void testUpdateItemWhenItemBelongsToAnotherUser() {
        long itemId = 1L;
        long userId = 3L;

        User user = new User(1L, "test1@test.com", "Test1");
        Item itemBefore = new Item(1L, user, "Name", "Description", true, null);

        ItemDto itemDtoFrom = new ItemDto(1L, "Update", "Description", true, null,
                null, new ArrayList<>(), null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(itemBefore));

        assertThrows(EntityNotFoundException.class, () -> itemService.updateItem(itemDtoFrom, itemId, userId));

        verify(itemRepository).findById(itemId);
        verifyNoMoreInteractions(itemRepository, itemMapper, commentMapper, commentRepository);
    }

    @Test
    void shouldReturnItemDtoWhenItemExists() {
        long itemId = 1L;
        long userId = 1L;

        User user = new User(1L, "test1@test.com", "Test1");
        Item item = new Item(1L, user, "Name", "Description", true, null);

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        ItemDto itemDtoTo = new ItemDto(1L, "Name", "Description", true, null,
                null, new ArrayList<>(), null);
        when(itemMapper.mapTo(item, userId)).thenReturn(itemDtoTo);

        ItemDto result = itemService.getItem(itemId, userId);

        assertEquals(itemDtoTo, result);
    }

    @Test
    void shouldThrowEntityNotFoundExceptionWhenItemDoesNotExist() {
        long itemId = 1L;
        long userId = 2L;

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.getItem(itemId, userId));
    }

    @Test
    void testGetItems() {
        long userId = 1L;

        User user = new User(1L, "test1@test.com", "Test1");
        Item item1 = new Item(1L, user, "Name", "Description", true, null);
        Item item2 = new Item(2L, user, "Name2", "Description2", true, null);

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        ItemDto itemDto1 = new ItemDto(1L, "Name", "Description", true, null,
                null, new ArrayList<>(), null);

        ItemDto itemDto2 = new ItemDto(2L, "Name2", "Description2", true, null,
                null, new ArrayList<>(), null);

        List<ItemDto> expectedItems = new ArrayList<>();
        expectedItems.add(itemDto1);
        expectedItems.add(itemDto2);

        when(itemRepository.findAllByUserId(userId)).thenReturn(items);
        when(itemMapper.mapTo(item1, userId)).thenReturn(itemDto1);
        when(itemMapper.mapTo(item2, userId)).thenReturn(itemDto2);

        List<ItemDto> actualItems = itemService.getItems(userId);

        assertEquals(expectedItems.size(), actualItems.size());
        assertEquals(expectedItems.get(0).getId(), actualItems.get(0).getId());
        assertEquals(expectedItems.get(0).getLastBooking(), actualItems.get(0).getLastBooking());
        assertEquals(expectedItems.get(0).getNextBooking(), actualItems.get(0).getNextBooking());
        assertEquals(expectedItems.get(1).getId(), actualItems.get(1).getId());
        assertEquals(expectedItems.get(1).getLastBooking(), actualItems.get(1).getLastBooking());
        assertEquals(expectedItems.get(1).getNextBooking(), actualItems.get(1).getNextBooking());
        assertEquals(expectedItems.get(0).getComments().size(), actualItems.get(0).getComments().size());
    }

    @Test
    public void testGetItemsByText_Success() {
        String text = "item";
        long userId = 1L;

        List<Item> items = new ArrayList<>();
        Item item1 = new Item();
        item1.setId(1L);
        item1.setName("Item 1");
        items.add(item1);

        when(itemRepository.itemsByText(text.toUpperCase())).thenReturn(items);

        ItemDto itemDto1 = new ItemDto();
        itemDto1.setId(1L);
        itemDto1.setName("Item 1");

        when(itemMapper.mapTo(item1, userId)).thenReturn(itemDto1);

        List<ItemDto> expected = new ArrayList<>();
        expected.add(itemDto1);

        List<ItemDto> actual = itemService.getItemsByText(text, userId);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetItemsByText_BlankText() {
        String text = "";
        long userId = 1L;

        List<ItemDto> expected = new ArrayList<>();

        List<ItemDto> actual = itemService.getItemsByText(text, userId);

        assertEquals(expected, actual);
    }

    @Test
    void createComment_Success() {
        CommentDto commentDto = new CommentDto();
        commentDto.setText("test comment");
        long userId = 1L;
        long itemId = 2L;
        Item item = new Item();
        Comment comment = new Comment();

        when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        when(commentMapper.mapFrom(commentDto, item, userId)).thenReturn(comment);
        when(commentRepository.save(comment)).thenReturn(comment);
        when(commentMapper.mapTo(comment)).thenReturn(commentDto);

        CommentDto result = itemService.createComment(commentDto, userId, itemId);

        assertEquals(commentDto, result);
        verify(itemRepository, times(1)).findById(itemId);
        verify(commentMapper, times(1)).mapFrom(commentDto, item, userId);
        verify(commentRepository, times(1)).save(comment);
        verify(commentMapper, times(1)).mapTo(comment);
    }

    @Test
    void createComment_ItemNotFound() {
        long userId = 1L;
        long itemId = 2L;

        CommentDto commentDto = new CommentDto();

        when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemService.createComment(commentDto, userId, itemId));
        verify(itemRepository, times(1)).findById(itemId);
        verifyNoInteractions(commentMapper, commentRepository);
    }
}