package ru.practicum.shareit.item.model.mapper;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BadRequestException;
import ru.practicum.shareit.exception.EntityNotFoundException;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.CommentDto;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CommentMapperImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private CommentRepository commentRepository;
    @InjectMocks
    private CommentMapperImpl commentMapper;


    @Test
    public void mapFrom_ShouldThrowBadRequestException_WhenNoPastBookingsFound() {
        Item item = mock(Item.class);
        when(item.getId()).thenReturn(1L);

        when(bookingRepository.findPastBookingsByBookerIdAndItemId(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<>());

        CommentDto commentDto = new CommentDto();
        commentDto.setText("test");
        commentDto.setId(1L);

        assertThrows(BadRequestException.class,
                () -> commentMapper.mapFrom(commentDto, item, 1L));
    }

    @Test
    public void mapFrom_ShouldThrowEntityNotFoundException_WhenAuthorNotFound() {
        Item item = mock(Item.class);
        when(item.getId()).thenReturn(1L);

        LocalDateTime localDateTime = LocalDateTime.now();

        Booking booking = new Booking();
        booking.setEndDate(localDateTime.minusDays(1));

        when(bookingRepository.findPastBookingsByBookerIdAndItemId(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<Booking>() {{
                    add(booking);
                }
                });

        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.empty());

        CommentDto commentDto = new CommentDto();
        commentDto.setText("test");
        commentDto.setId(1L);

        assertThrows(EntityNotFoundException.class,
                () -> commentMapper.mapFrom(commentDto, item, 1L));
    }

    @Test
    public void mapFrom_ShouldReturnComment() {
        Item item = mock(Item.class);
        when(item.getId()).thenReturn(1L);

        LocalDateTime localDateTime = LocalDateTime.now();

        Booking booking = new Booking();
        booking.setEndDate(localDateTime.minusDays(1));

        when(bookingRepository.findPastBookingsByBookerIdAndItemId(anyLong(), anyLong(), any(LocalDateTime.class)))
                .thenReturn(new ArrayList<Booking>() {{
                    add(booking);
                }
                });

        User user = new User();
        user.setEmail("test@test.com");
        user.setName("Test");

        when(userRepository.findById(anyLong())).thenReturn(java.util.Optional.of(user));

        CommentDto commentDto = new CommentDto();
        commentDto.setText("test");
        commentDto.setId(1L);

        Comment comment = commentMapper.mapFrom(commentDto, item, 1L);

        assertNotNull(comment);
        assertEquals(commentDto.getId(), comment.getId());
        assertEquals(commentDto.getText(), comment.getText());
        assertEquals(item, comment.getItem());
        assertEquals(user, comment.getAuthor());
    }

    @Test
    public void testMapTo() {
        User user = new User(1L, "john.doe@example.com", "John Doe");
        Item item = new Item(1L, user, "Item 1", "Description of Item 1", true, null);
        LocalDateTime created = LocalDateTime.now();
        Comment comment = new Comment(1L, "Comment 1", item, user, created);

        CommentDto dto = commentMapper.mapTo(comment);

        assertNotNull(dto);
        assertEquals(comment.getId(), dto.getId());
        assertEquals(comment.getText(), dto.getText());
        assertEquals(comment.getAuthor().getName(), dto.getAuthorName());
        assertEquals(created, dto.getCreated());
    }

    @Test
    public void testGetCommentsSuccess() {
        Long itemId = 1L;
        User user = new User(1L, "john.doe@example.com", "John Doe");
        Item item = new Item(itemId, user, "Item 1", "Description of Item 1", true, null);
        List<Comment> comments = new ArrayList<>();
        comments.add(new Comment(1L, "Comment 1", item, user, LocalDateTime.now()));
        comments.add(new Comment(2L, "Comment 2", item, user, LocalDateTime.now()));
        when(commentRepository.findAllByItemId(itemId)).thenReturn(comments);

        List<CommentDto> dtos = commentMapper.getComments(itemId);

        assertNotNull(dtos);
        assertEquals(comments.size(), dtos.size());
        for (int i = 0; i < comments.size(); i++) {
            Comment comment = comments.get(i);
            CommentDto dto = dtos.get(i);
            assertEquals(comment.getId(), dto.getId());
            assertEquals(comment.getText(), dto.getText());
            assertEquals(comment.getAuthor().getName(), dto.getAuthorName());
            assertEquals(comment.getCreated(), dto.getCreated());
        }
    }

    @Test
    public void testGetCommentsFailure() {
        Long itemId = 1L;
        when(commentRepository.findAllByItemId(itemId)).thenReturn(null);

        assertThrows(NullPointerException.class, () -> commentMapper.getComments(itemId));
    }
}