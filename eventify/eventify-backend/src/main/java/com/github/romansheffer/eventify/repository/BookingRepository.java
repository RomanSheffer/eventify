package com.github.romansheffer.eventify.repository;

import com.github.romansheffer.eventify.model.Booking;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {


    List<Booking> findByCustomerEmail(String email);

    @Query("SELECT b FROM Booking b WHERE " +
            "(:eventId IS NULL OR b.event.id = :eventId) AND " +
            "(:confirmed IS NULL OR b.confirmed = :confirmed)")
    Page<Booking> findAllByEventIdAndConfirmed(
            @Param("eventId") Long eventId,
            @Param("confirmed") Boolean confirmed,
            Pageable pageable
    );
    void deleteByEventId(Long id);

  }
