package com.cargoAppService.dto;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import java.sql.Timestamp;
import java.util.UUID;

public class LoadDTO {

    @Data
    public static class Create {
        @NotBlank
        private String shipperId;
        @NotBlank
        private String loadingPoint;
        @NotBlank
        private String unloadingPoint;
        @NotNull
        private Timestamp loadingDate;
        @NotNull
        private Timestamp unloadingDate;
        @NotBlank
        private String productType;
        @NotBlank
        private String truckType;
        @Min(1)
        private int noOfTrucks;
        @Positive
        private double weight;
        private String comment;
    }

    @Data
    public static class Update {
        private String loadingPoint;
        private String unloadingPoint;
        private String productType;
        private String truckType;
        private Integer noOfTrucks;
        private Double weight;
        private String comment;
    }

    @Data
    public static class Response {
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
        private String status;
        private Timestamp datePosted;
    }
}
