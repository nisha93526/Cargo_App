package com.cargoAppService.dto;


import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Positive;
import java.sql.Timestamp;
import java.util.UUID;

public class BookingDTO {
    @Data
    public static class Create {
        @NotNull(message = "Load ID cannot be null.")
        private UUID loadId;

        @NotBlank(message = "Transporter ID is required.")
        private String transporterId;

        @Positive(message = "Proposed rate must be positive.")
        private double proposedRate;

        private String comment;
    }

    @Data
    public static class Update {
        @NotBlank(message = "Status is required for an update.")
        @Pattern(regexp = "ACCEPTED|REJECTED", message = "Status must be either ACCEPTED or REJECTED.")
        private String status;
    }

    @Data
    public static class Response {
        private UUID id;
        private UUID loadId;
        private String transporterId;
        private double proposedRate;
        private String comment;
        private String status;
        private Timestamp requestedAt;
    }
}