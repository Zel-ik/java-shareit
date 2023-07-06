package ru.practicum.shareit.item.model.mapper;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
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
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class CommentMapperImpl implements CommentMapper {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final CommentRepository commentRepository;

    @Override
    public Comment mapFrom(CommentDto entity, Item item, long userId) {
        LocalDateTime currentTime = LocalDateTime.now();

        bookingRepository.findPastBookingsByBookerIdAndItemId(userId, item.getId(), currentTime).stream().findFirst()
                .orElseThrow(() -> new BadRequestException("Вы не брали вещь в аренду"));

        User author = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException(String.format("Пользователь с идентификатором %d" +
                        " не найден", userId)));

        return new Comment(entity.getId(), entity.getText(), item, author, currentTime);
    }

    @Override
    public CommentDto mapTo(Comment entity) {
        return new CommentDto(entity.getId(), entity.getText(), entity.getAuthor().getName(), entity.getCreated());
    }

    @Override
    public List<CommentDto> getComments(Long itemId) {
        List<Comment> comments = commentRepository.findAllByItemId(itemId);
        return comments.stream().filter(Objects::nonNull).map(this::mapTo).collect(Collectors.toList());
    }
}
