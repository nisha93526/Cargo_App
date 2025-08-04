package com.cargoAppService.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

@Entity
@Table(name = "bookings")
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String transporterId;
    private double proposedRate;
    private String comment;

    @Enumerated(EnumType.STRING)
    private Status status;

    private Timestamp requestedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "load_id", nullable = false)
    private Load load;

    public enum Status {
        PENDING, ACCEPTED, REJECTED
    }
}