package com.github.romansheffer.eventify.repository;

import com.github.romansheffer.eventify.model.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    @EntityGraph(attributePaths = {"bookings"})
    Page<Event> findAllByDateTimeBetween(Instant from, Instant to, Pageable pageable);

    @Query("SELECT e FROM Event e LEFT JOIN FETCH e.bookings WHERE e.id = :id")
    Optional<Event> findByIdWithBookings(@Param("id") Long id);


}
