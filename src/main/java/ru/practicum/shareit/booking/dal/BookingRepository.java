package ru.practicum.shareit.booking.dal;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStartEnd;
import ru.practicum.shareit.booking.model.StatusBooking;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    @Query(value = "SELECT b " +
            "FROM Booking b " +
            "WHERE b.id = ?1 AND (b.booker.id = ?2 OR b.item.ownerId = ?2)")
    Optional<Booking> findByIdWithUserAccess(Long id, Long userId);

    Optional<Booking> findByIdAndStatus(Long id, StatusBooking status);

    Optional<Booking> findByBookerIdAndItemId(Long bookerId, Long itemId, Sort sort);

    @Query(value = """
            SELECT b.id,
                   b.start_booking_time,
                   b.end_booking_time
            FROM booking AS b
            WHERE b.item_id = ?2
              AND b.item_id = (SELECT i.id
                               FROM item AS i
                               WHERE i.id = ?2 AND i.owner_id = ?1
                               LIMIT 1)
              AND b.start_booking_time > CURRENT_TIMESTAMP
              AND b.status = 1
            ORDER BY b.start_booking_time ASC
            LIMIT 1
            """, nativeQuery = true)
    Optional<BookingStartEnd> findNextBookingTime(Long ownerId, Long itemId);

    @Query(value = """
            SELECT b.id,
                   b.start_booking_time,
                   b.end_booking_time
            FROM booking AS b
            WHERE b.item_id = ?2
              AND b.item_id = (SELECT i.id
                               FROM item AS i
                               WHERE i.id = ?2 AND i.owner_id = ?1
                               LIMIT 1)
              AND b.start_booking_time < CURRENT_TIMESTAMP
              AND b.status = 1
            ORDER BY b.start_booking_time DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<BookingStartEnd> findLastBookingTime(Long ownerId, Long itemId);

    // Все букинги
    List<Booking> findByBooker_id(Long bookerId, Sort sort);

    @Query(value = "SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1")
    List<Booking> findByOwner(Long ownerId, Sort sort);

    // Прошедшие букинги
    List<Booking> findByBooker_IdAndStartBookingTimeBefore(Long bookerId, LocalDateTime time, Sort sort);

    @Query(value = "SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 AND b.startBookingTime < ?2")
    List<Booking> findByOwner_IdAndStartBookingTimeBefore(Long ownerId, LocalDateTime time, Sort sort);

    // Будущие букинги
    List<Booking> findByBooker_IdAndEndBookingTimeAfter(Long bookerId, LocalDateTime time, Sort sort);

    @Query(value = "SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 AND b.startBookingTime > ?2")
    List<Booking> findByOwner_IdAndEndBookingTimeAfter(Long ownerId, LocalDateTime time, Sort sort);

    // Текущие букинги
    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 AND b.startBookingTime < CURRENT_TIMESTAMP AND b.endBookingTime > CURRENT_TIMESTAMP")
    List<Booking> findByBookerIdCurrentBookings(Long bookerId, Sort sort);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.ownerId = ?1 AND b.startBookingTime < CURRENT_TIMESTAMP AND b.endBookingTime > CURRENT_TIMESTAMP")
    List<Booking> findByOwnerIdCurrentBookings(Long ownerId, Sort sort);

    // Букинги по статусу
    List<Booking> findByBookerAndStatus(Long bookerId, Integer statusOrdinal, Sort sort);

    @Query(value = "SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 AND b.status.ordinal() = ?2")
    List<Booking> findByOwnerAndStatus(Long ownerId, Integer statusOrdinal, Sort sort);
}
