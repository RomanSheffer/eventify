package com.github.romansheffer.eventify.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.AnyDiscriminatorImplicitValues;

@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "notify_new_events")
    private boolean notifyNewEvents;

    @Column(name = "notify_upcoming")
    private boolean notifyUpcoming;

    @Column(name = "notify_before_hours")
    private Long notifyBeforeHours;

    @Version
    private Long version;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;


}
