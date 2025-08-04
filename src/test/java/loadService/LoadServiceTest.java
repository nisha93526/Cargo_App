package loadService;

import com.cargoAppService.service.LoadService;

import com.cargoAppService.dto.LoadDTO;
import com.cargoAppService.entities.Load;
import com.cargoAppService.repositories.LoadRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Timestamp;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class LoadServiceTest {

    @Mock
    private LoadRepository loadRepository;

    @InjectMocks
    private LoadService loadService;

    @Test
    void testCreateLoad() {
        LoadDTO.Create createDto = new LoadDTO.Create();
        createDto.setShipperId("shipper-123");
        createDto.setLoadingPoint("Point A");
        createDto.setUnloadingPoint("Point B");
        createDto.setLoadingDate(new Timestamp(System.currentTimeMillis()));
        createDto.setUnloadingDate(new Timestamp(System.currentTimeMillis() + 86400000));
        createDto.setProductType("Electronics");
        createDto.setTruckType("20ft");
        createDto.setNoOfTrucks(1);
        createDto.setWeight(5000);

        Load load = new Load();
        load.setId(UUID.randomUUID());
        load.setStatus(Load.Status.POSTED);

        when(loadRepository.save(any(Load.class))).thenReturn(load);

        Load result = loadService.createLoad(createDto);

        assertNotNull(result);
        assertEquals(Load.Status.POSTED, result.getStatus());
    }
}