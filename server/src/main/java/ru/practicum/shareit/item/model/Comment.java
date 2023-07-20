package ru.practicum.shareit.item.model;

import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    private Long id;
    @NotBlank
    @Column(name = "text")
    private String text;
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "item_id")
    private Item item;
    @NotNull
    @ManyToOne(optional = false)
    @JoinColumn(name = "author_id")
    private User author;
    @NotNull
    @Column(name = "created")
    private LocalDateTime created;
}
