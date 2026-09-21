package com.github.romansheffer.eventify.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "date_time")
    private Instant dateTime;

    @Column(name = "total_tickets")
    private Long totalTickets;

    @Column(name = "available_tickets")
    private Long availableTickets;

    @Column(name = "cover_url")
    private String coverUrl;

    @Version
    private Long version;

    @OneToMany(
            mappedBy = "event",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    List<Booking> bookings;
}

