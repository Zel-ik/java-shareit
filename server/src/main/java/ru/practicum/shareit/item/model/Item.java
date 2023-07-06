package ru.practicum.shareit.item.model;

import jdk.jfr.BooleanFlag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.validateInterfaces.Create;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@Entity
@Table(name = "items")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;
    @NotBlank(groups = {Create.class})
    @Column(name = "name")
    private String name;
    @NotBlank(groups = {Create.class})
    @Column(name = "description")
    private String description;
    @BooleanFlag
    @NotNull(groups = {Create.class})
    @Column(name = "available")
    private Boolean available;
    @Transient
    private List<Comment> comments;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequest itemRequest;

    public Item(Long id, User user, String name, String description, Boolean available, List<Comment> comments) {
        this.id = id;
        this.user = user;
        this.name = name;
        this.description = description;
        this.available = available;
        this.comments = comments;
    }
}

