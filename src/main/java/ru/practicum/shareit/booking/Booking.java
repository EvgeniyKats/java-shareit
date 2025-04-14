package ru.practicum.shareit.booking;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
@Entity
@EqualsAndHashCode(of = "id")
@Table(name = "booking")
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "booker_id")
    private Long bookerId;

    @Column(name = "owner_id")
    private Long ownerId;

    @Enumerated
    private Status status;

    @Column(name = "start_booking_time")
    private Instant startBookingTime;

    @Column(name = "end_booking_time")
    private Instant endBookingTime;

    public enum Status {
        WAITING, APPROVED, REJECTED
    }
}
