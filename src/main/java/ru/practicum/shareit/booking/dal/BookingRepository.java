package ru.practicum.shareit.booking.dal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.booking.model.Booking;
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

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 AND b.item.id = ?2 AND b.startBookingTime > CURRENT_TIMESTAMP")
    Page<Booking> findNextBooking(Long ownerId, Long itemId, Pageable pageable);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 AND b.item.id = ?2 AND b.startBookingTime < CURRENT_TIMESTAMP")
    Page<Booking> findLastBooking(Long ownerId, Long itemId, Pageable pageable);

    // Все букинги
    List<Booking> findByBooker_id(Long bookerId, Sort Sort);

    @Query(value = "SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1")
    List<Booking> findByOwner(Long ownerId, Sort Sort);

    // Прошедшие букинги
    List<Booking> findByBooker_IdAndStartBookingTimeBefore(Long bookerId, LocalDateTime time, Sort Sort);

    @Query(value = "SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 AND b.startBookingTime < ?2")
    List<Booking> findByOwner_IdAndStartBookingTimeBefore(Long ownerId, LocalDateTime time, Sort Sort);

    // Будущие букинги
    List<Booking> findByBooker_IdAndEndBookingTimeAfter(Long bookerId, LocalDateTime time, Sort Sort);

    @Query(value = "SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 AND b.startBookingTime > ?2")
    List<Booking> findByOwner_IdAndEndBookingTimeAfter(Long ownerId, LocalDateTime time, Sort Sort);

    // Текущие букинги
    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 AND b.startBookingTime < CURRENT_TIMESTAMP AND b.endBookingTime > CURRENT_TIMESTAMP")
    List<Booking> findByBookerIdCurrentBookings(Long bookerId, Sort Sort);

    @Query("SELECT b " +
            "FROM Booking b " +
            "WHERE b.item.ownerId = ?1 AND b.startBookingTime < CURRENT_TIMESTAMP AND b.endBookingTime > CURRENT_TIMESTAMP")
    List<Booking> findByOwnerIdCurrentBookings(Long ownerId, Sort Sort);

    // Букинги по статусу
    List<Booking> findByBookerAndStatus(Long bookerId, Integer statusOrdinal, Sort Sort);

    @Query(value = "SELECT b " +
            "FROM Booking b " +
            "WHERE b.booker.id = ?1 AND b.status.ordinal() = ?2")
    List<Booking> findByOwnerAndStatus(Long ownerId, Integer statusOrdinal, Sort Sort);
}
