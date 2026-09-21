package com.github.romansheffer.eventify.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.time.LocalDateTime;


@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "bookings")
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne()
    @JoinColumn(name = "event_id")
    private Event event;

    @Column(name = "customer_email")
    private String customerEmail;

    @Column(name = "ticket_count")
    private Long ticketCount;

    @Column(name = "confirmed")
    private Boolean confirmed;

    @Column(name = "created_at")
    private Instant createdAt;

    @Column(name = "expiry_time")
    private Instant expiryTime;

    @Version
    private Long version;
}
