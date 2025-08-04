package com.cargoAppService.entities;

import lombok.Data;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "loads")
@Data
public class Load {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    private String shipperId;
    private String loadingPoint;
    private String unloadingPoint;
    private Timestamp loadingDate;
    private Timestamp unloadingDate;
    private String productType;
    private String truckType;
    private int noOfTrucks;
    private double weight;
    private String comment;
    private Timestamp datePosted;

    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "load", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings;

    public enum Status {
        POSTED, BOOKED, CANCELLED
    }
}