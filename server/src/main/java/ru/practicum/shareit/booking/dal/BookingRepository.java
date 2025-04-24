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
            SELECT b.item_id,
                   MAX(b.start_booking_time) AS start_booking_time,
                   MAX(b.end_booking_time) AS end_booking_time
            FROM booking AS b
            WHERE b.item_id IN ?2
              AND b.item_id IN (SELECT i.id
                               FROM item AS i
                               WHERE i.id IN ?2 AND i.owner_id = ?1)
              AND b.start_booking_time < ?3
              AND b.status = 1
            GROUP BY b.item_id
            """, nativeQuery = true)
    List<BookingStartEnd> findLastBookingTimeForIds(Long ownerId, List<Long> itemIds, LocalDateTime now);

    @Query(value = """
            SELECT b.item_id,
                   MIN(b.start_booking_time) AS start_booking_time,
                   MIN(b.end_booking_time) AS end_booking_time
            FROM booking AS b
            WHERE b.item_id IN ?2
              AND b.item_id IN (SELECT i.id
                               FROM item AS i
                               WHERE i.id IN ?2 AND i.owner_id = ?1)
              AND b.start_booking_time > ?3
              AND b.status = 1
            GROUP BY b.item_id
            """, nativeQuery = true)
    List<BookingStartEnd> findNextBookingTimeForIds(Long ownerId, List<Long> itemIds, LocalDateTime now);

    @Query(value = """
            SELECT b.id,
                   b.item_id,
                   b.start_booking_time,
                   b.end_booking_time
            FROM booking AS b
            WHERE b.item_id = ?2
              AND b.item_id = (SELECT i.id
                               FROM item AS i
                               WHERE i.id = ?2 AND i.owner_id = ?1
                               LIMIT 1)
              AND b.start_booking_time > ?3
              AND b.status = 1
            ORDER BY b.start_booking_time ASC
            LIMIT 1
            """, nativeQuery = true)
    Optional<BookingStartEnd> findNextBookingTime(Long ownerId, Long itemId, LocalDateTime now);

    @Query(value = """
            SELECT b.id,
                   b.item_id,
                   b.start_booking_time,
                   b.end_booking_time
            FROM booking AS b
            WHERE b.item_id = ?2
              AND b.item_id = (SELECT i.id
                               FROM item AS i
                               WHERE i.id = ?2 AND i.owner_id = ?1
                               LIMIT 1)
              AND b.start_booking_time < ?3
              AND b.status = 1
            ORDER BY b.start_booking_time DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<BookingStartEnd> findLastBookingTime(Long ownerId, Long itemId, LocalDateTime now);

    // Все букинги
    List<Booking> findByBookerId(Long bookerId, Sort sort);

    @Query(value = "SELECT b " +
                   "FROM Booking AS b " +
                   "WHERE b.item.id IN (SELECT i.id FROM b.item AS i WHERE i.ownerId = ?1)")
    List<Booking> findAllBookingsForOwner(Long ownerId, Sort sort);

    // Прошедшие букинги
    List<Booking> findByBookerIdAndEndBookingTimeBeforeAndStatus(Long bookerId, LocalDateTime time, StatusBooking statusBooking, Sort sort);

    @Query(value = "SELECT b " +
                   "FROM Booking AS b " +
                   "WHERE b.item.id IN (SELECT i.id FROM b.item AS i WHERE i.ownerId = ?1) AND b.endBookingTime < ?2 AND b.status = ?3")
    List<Booking> findPastBookingsForOwnerAndStatus(Long ownerId, LocalDateTime time, StatusBooking statusBooking, Sort sort);

    // Будущие букинги
    List<Booking> findByBookerIdAndEndBookingTimeAfterAndStatus(Long bookerId, LocalDateTime time, StatusBooking statusBooking, Sort sort);

    @Query(value = "SELECT b " +
                   "FROM Booking AS b " +
                   "WHERE b.item.id IN (SELECT i.id FROM b.item AS i WHERE i.ownerId = ?1) AND b.endBookingTime > ?2 AND b.status = ?3")
    List<Booking> findFutureBookingsForOwnerAndStatus(Long ownerId, LocalDateTime time, StatusBooking statusBooking, Sort sort);

    // Текущие букинги
    @Query("SELECT b " +
           "FROM Booking b " +
           "WHERE b.booker.id = ?1 " +
           "AND b.startBookingTime < ?3 " +
           "AND b.endBookingTime > ?3 " +
           "AND b.status = ?2")
    List<Booking> findCurrentBookingsForBookerAndStatus(Long bookerId, StatusBooking statusBooking, LocalDateTime now, Sort sort);


    @Query(value = "SELECT b " +
                   "FROM Booking AS b " +
                   "WHERE b.item.id IN (SELECT i.id FROM b.item AS i WHERE i.ownerId = ?1) " +
                   "AND b.startBookingTime < ?3 " +
                   "AND b.endBookingTime > ?3 " +
                   "AND b.status = ?2")
    List<Booking> findCurrentBookingsForOwnerAndStatus(Long ownerId, StatusBooking statusBooking, LocalDateTime now, Sort sort);

    // Букинги по статусу
    List<Booking> findByBookerIdAndStatus(Long bookerId, StatusBooking status, Sort sort);

    @Query(value = "SELECT b " +
                   "FROM Booking b " +
                   "WHERE b.item.id IN (SELECT i.id FROM b.item AS i WHERE i.ownerId = ?1) AND b.status = ?2")
    List<Booking> findByStatusForOwner(Long ownerId, StatusBooking status, Sort sort);
}
