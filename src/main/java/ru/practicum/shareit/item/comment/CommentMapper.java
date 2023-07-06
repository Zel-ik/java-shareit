package ru.practicum.shareit.item.comment;

public class CommentMapper {

    private CommentMapper() {
    }

    public static CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public static CommentOutDto toCommentOutDto(Comment comment) {
        return CommentOutDto.builder()
                .text(comment.getText())
                .build();
    }
}