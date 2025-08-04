package bookingService;



import com.cargoAppService.dto.BookingDTO;
import com.cargoAppService.entities.Booking;
import com.cargoAppService.entities.Load;
import com.cargoAppService.exceptions.BookingValidationException;
import com.cargoAppService.exceptions.ResourceNotFoundException;
import com.cargoAppService.repositories.BookingRepository;
import com.cargoAppService.repositories.LoadRepository;
import com.cargoAppService.service.BookingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock
    private BookingRepository bookingRepository;

    @Mock
    private LoadRepository loadRepository;

    @InjectMocks
    private BookingService bookingService;

    private Load testLoad;
    private Booking testBooking;
    private BookingDTO.Create createDto;

    @BeforeEach
    void setUp() {
        // Prepare a reusable Load object for tests
        testLoad = new Load();
        testLoad.setId(UUID.randomUUID());
        testLoad.setStatus(Load.Status.POSTED);
        testLoad.setBookings(new ArrayList<>()); // Initialize bookings list

        // Prepare a reusable Booking object
        testBooking = new Booking();
        testBooking.setId(UUID.randomUUID());
        testBooking.setLoad(testLoad);
        testBooking.setStatus(Booking.Status.PENDING);

        // Prepare a reusable DTO for creating bookings
        createDto = new BookingDTO.Create();
        createDto.setLoadId(testLoad.getId());
        createDto.setTransporterId("transporter-1");
        createDto.setProposedRate(1000.0);
    }

    @Test
    void createBooking_Success_ShouldChangeLoadStatusToBooked() {
        // Arrange
        when(loadRepository.findById(testLoad.getId())).thenReturn(Optional.of(testLoad));
        when(bookingRepository.save(any(Booking.class))).thenReturn(testBooking);
        when(loadRepository.save(any(Load.class))).thenReturn(testLoad);

        // Act
        Booking result = bookingService.createBooking(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(Booking.Status.PENDING, result.getStatus());

        // Use ArgumentCaptor to capture the Load object passed to save()
        ArgumentCaptor<Load> loadCaptor = ArgumentCaptor.forClass(Load.class);
        verify(loadRepository, times(1)).save(loadCaptor.capture());

        Load savedLoad = loadCaptor.getValue();
        assertEquals(Load.Status.BOOKED, savedLoad.getStatus(), "Load status should be updated to BOOKED.");

        verify(bookingRepository, times(1)).save(any(Booking.class));
    }

    @Test
    void createBooking_Fail_WhenLoadIsCancelled() {
        // Arrange
        testLoad.setStatus(Load.Status.CANCELLED);
        when(loadRepository.findById(testLoad.getId())).thenReturn(Optional.of(testLoad));

        // Act & Assert
        BookingValidationException exception = assertThrows(BookingValidationException.class, () -> {
            bookingService.createBooking(createDto);
        });

        assertEquals("Cannot create booking for a cancelled load.", exception.getMessage());
        verify(bookingRepository, never()).save(any());
        verify(loadRepository, never()).save(any());
    }

    @Test
    void createBooking_Fail_WhenLoadNotFound() {
        // Arrange
        when(loadRepository.findById(any(UUID.class))).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            bookingService.createBooking(createDto);
        });
    }

    @Test
    void updateBooking_Success_ShouldChangeStatusToAccepted() {
        // Arrange
        BookingDTO.Update updateDto = new BookingDTO.Update();
        updateDto.setStatus("ACCEPTED");

        when(bookingRepository.findById(testBooking.getId())).thenReturn(Optional.of(testBooking));
        when(bookingRepository.save(any(Booking.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Booking result = bookingService.updateBooking(testBooking.getId(), updateDto);

        // Assert
        assertNotNull(result);
        assertEquals(Booking.Status.ACCEPTED, result.getStatus());
        verify(bookingRepository, times(1)).save(testBooking);
    }

    @Test
    void deleteBooking_Success_AndRevertLoadStatusWhenNoBookingsLeft() {
        // Arrange: The booking to be deleted is the only one for the load.
        when(bookingRepository.findById(testBooking.getId())).thenReturn(Optional.of(testBooking));
        // After deletion, the findById on the load should return a load with an empty booking list.
        when(loadRepository.findById(testLoad.getId())).thenReturn(Optional.of(testLoad));

        // Act
        bookingService.deleteBooking(testBooking.getId());

        // Assert
        verify(bookingRepository, times(1)).delete(testBooking);

        // Capture the load that is saved to check its status
        ArgumentCaptor<Load> loadCaptor = ArgumentCaptor.forClass(Load.class);
        verify(loadRepository, times(1)).save(loadCaptor.capture());

        Load savedLoad = loadCaptor.getValue();
        assertEquals(Load.Status.POSTED, savedLoad.getStatus(), "Load status should revert to POSTED.");
    }

    @Test
    void deleteBooking_Success_AndKeepLoadStatusWhenOtherBookingsExist() {
        // Arrange: Setup a load with two bookings.
        Booking anotherBooking = new Booking();
        anotherBooking.setId(UUID.randomUUID());
        anotherBooking.setStatus(Booking.Status.PENDING);

        testLoad.setBookings(List.of(testBooking, anotherBooking));
        testBooking.setLoad(testLoad);

        when(bookingRepository.findById(testBooking.getId())).thenReturn(Optional.of(testBooking));

        // Simulate the state of the load after the booking is deleted.
        // The load will still have 'anotherBooking' associated with it.
        Load refreshedLoad = new Load();
        refreshedLoad.setId(testLoad.getId());
        refreshedLoad.setBookings(List.of(anotherBooking)); // Only one booking remains.

        when(loadRepository.findById(testLoad.getId())).thenReturn(Optional.of(refreshedLoad));

        // Act
        bookingService.deleteBooking(testBooking.getId());

        // Assert
        verify(bookingRepository, times(1)).delete(testBooking);
        // The save method should NOT be called on the load repository because an active booking still exists.
        verify(loadRepository, never()).save(any());
    }
}
