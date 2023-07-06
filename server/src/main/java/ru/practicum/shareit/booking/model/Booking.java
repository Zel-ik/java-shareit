package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @NotNull
    @Column(name = "start_date")
    private LocalDateTime startDate;
    @NotNull
    @Column(name = "end_date")
    private LocalDateTime endDate;
    @NotNull
    @Column(name = "status")
    @Enumerated(EnumType.STRING)
    private BookingStatus status;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booker_id")
    private User booker;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    public Booking(User booker, Item item, LocalDateTime startDate, LocalDateTime endDate, BookingStatus status) {
        this.booker = booker;
        this.item = item;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }
}
