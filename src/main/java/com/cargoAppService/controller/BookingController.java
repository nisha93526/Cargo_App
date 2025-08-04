package com.cargoAppService.controller;

import com.cargoAppService.dto.BookingDTO;
import com.cargoAppService.entities.Booking;
import com.cargoAppService.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;

@RestController
@RequestMapping("/booking")
public class BookingController {

    private final BookingService bookingService;

    public BookingController(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    @PostMapping
    public ResponseEntity<BookingDTO.Response> createBooking(@Valid @RequestBody BookingDTO.Create createDto) {
        Booking newBooking = bookingService.createBooking(createDto);
        return new ResponseEntity<>(toResponseDto(newBooking), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<BookingDTO.Response>> getBookings(
            @RequestParam(required = false) UUID loadId,
            @RequestParam(required = false) String transporterId,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Booking> bookings = bookingService.getBookings(loadId, transporterId, status, pageable);
        return ResponseEntity.ok(bookings.map(this::toResponseDto));
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<BookingDTO.Response> getBookingById(@PathVariable UUID bookingId) {
        Booking booking = bookingService.getBookingById(bookingId);
        return ResponseEntity.ok(toResponseDto(booking));
    }

    @PutMapping("/{bookingId}")
    public ResponseEntity<BookingDTO.Response> updateBooking(@PathVariable UUID bookingId, @Valid @RequestBody BookingDTO.Update updateDto) {
        Booking updatedBooking = bookingService.updateBooking(bookingId, updateDto);
        return ResponseEntity.ok(toResponseDto(updatedBooking));
    }

    @DeleteMapping("/{bookingId}")
    public ResponseEntity<Void> deleteBooking(@PathVariable UUID bookingId) {
        bookingService.deleteBooking(bookingId);
        return ResponseEntity.noContent().build();
    }

    private BookingDTO.Response toResponseDto(Booking booking) {
        BookingDTO.Response response = new BookingDTO.Response();
        response.setId(booking.getId());
        response.setLoadId(booking.getLoad().getId());
        response.setTransporterId(booking.getTransporterId());
        response.setProposedRate(booking.getProposedRate());
        response.setComment(booking.getComment());
        response.setStatus(booking.getStatus().name());
        response.setRequestedAt(booking.getRequestedAt());
        return response;
    }
}
