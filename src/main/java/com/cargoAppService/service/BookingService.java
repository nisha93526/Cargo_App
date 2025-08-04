package com.cargoAppService.service;


import com.cargoAppService.dto.BookingDTO;
import com.cargoAppService.entities.Booking;
import com.cargoAppService.entities.Load;
import com.cargoAppService.exceptions.BookingValidationException;
import com.cargoAppService.exceptions.ResourceNotFoundException;
import com.cargoAppService.repositories.BookingRepository;
import com.cargoAppService.repositories.LoadRepository;
import javax.persistence.criteria.Predicate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class BookingService {

    private final BookingRepository bookingRepository;
    private final LoadRepository loadRepository;

    public BookingService(BookingRepository bookingRepository, LoadRepository loadRepository) {
        this.bookingRepository = bookingRepository;
        this.loadRepository = loadRepository;
    }

    @Transactional
    public Booking createBooking(BookingDTO.Create createDto) {
        Load load = loadRepository.findById(createDto.getLoadId())
                                  .orElseThrow(() -> new ResourceNotFoundException("Load not found with id: " + createDto.getLoadId()));

        if (load.getStatus() == Load.Status.CANCELLED) {
            throw new BookingValidationException("Cannot create booking for a cancelled load.");
        }

        Booking booking = new Booking();
        booking.setLoad(load);
        booking.setTransporterId(createDto.getTransporterId());
        booking.setProposedRate(createDto.getProposedRate());
        booking.setComment(createDto.getComment());
        booking.setStatus(Booking.Status.PENDING);
        booking.setRequestedAt(new Timestamp(System.currentTimeMillis()));

        load.setStatus(Load.Status.BOOKED);

        loadRepository.save(load);
        return bookingRepository.save(booking);
    }

    @Transactional(readOnly = true)
    public Page<Booking> getBookings(UUID loadId, String transporterId, String status, Pageable pageable) {
        return bookingRepository.findAll((root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (loadId != null) {
                predicates.add(cb.equal(root.get("load").get("id"), loadId));
            }
            if (transporterId != null) {
                predicates.add(cb.equal(root.get("transporterId"), transporterId));
            }
            if (status != null) {
                predicates.add(cb.equal(root.get("status"), Booking.Status.valueOf(status.toUpperCase())));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        }, pageable);
    }

    @Transactional(readOnly = true)
    public Booking getBookingById(UUID bookingId) {
        return bookingRepository.findById(bookingId)
                                .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));
    }

    @Transactional
    public Booking updateBooking(UUID bookingId, BookingDTO.Update updateDto) {
        Booking booking = getBookingById(bookingId);
        Booking.Status newStatus = Booking.Status.valueOf(updateDto.getStatus().toUpperCase());

        // When a booking is accepted, update the status to ACCEPTED.
        booking.setStatus(newStatus);

        return bookingRepository.save(booking);
    }

    @Transactional
    public void deleteBooking(UUID bookingId) {
        Booking booking = getBookingById(bookingId);
        Load load = booking.getLoad();

        bookingRepository.delete(booking);

        loadRepository.flush();
        Load refreshedLoad = loadRepository.findById(load.getId()).get();

        boolean hasActiveBookings = refreshedLoad.getBookings().stream()
                                                 .anyMatch(b -> b.getStatus() == Booking.Status.PENDING || b.getStatus() == Booking.Status.ACCEPTED);

        if (!hasActiveBookings) {
            refreshedLoad.setStatus(Load.Status.POSTED);
            loadRepository.save(refreshedLoad);
        }
    }
}
