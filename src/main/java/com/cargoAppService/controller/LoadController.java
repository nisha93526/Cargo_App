package com.cargoAppService.controller;

import com.cargoAppService.dto.LoadDTO;

import com.cargoAppService.entities.Load;
import com.cargoAppService.service.LoadService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.UUID;


@RestController
@RequestMapping("/load")
public class LoadController {

    private final LoadService loadService;

    public LoadController(LoadService loadService) {
        this.loadService = loadService;
    }

    @PostMapping
    public ResponseEntity<LoadDTO.Response> createLoad(@Valid @RequestBody LoadDTO.Create createDto) {
        Load createdLoad = loadService.createLoad(createDto);
        return new ResponseEntity<>(toResponseDto(createdLoad), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<Page<LoadDTO.Response>> getLoads(
            @RequestParam(required = false) String shipperId,
            @RequestParam(required = false) String truckType,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Load> loads = loadService.getLoads(shipperId, truckType, status, pageable);
        return ResponseEntity.ok(loads.map(this::toResponseDto));
    }

    @GetMapping("/{loadId}")
    public ResponseEntity<LoadDTO.Response> getLoadById(@PathVariable UUID loadId) {
        Load load = loadService.getLoadById(loadId);
        return ResponseEntity.ok(toResponseDto(load));
    }

    @PutMapping("/{loadId}")
    public ResponseEntity<LoadDTO.Response> updateLoad(@PathVariable UUID loadId, @RequestBody LoadDTO.Update updateDto) {
        Load updatedLoad = loadService.updateLoad(loadId, updateDto);
        return ResponseEntity.ok(toResponseDto(updatedLoad));
    }

    @DeleteMapping("/{loadId}")
    public ResponseEntity<String> deleteLoad(@PathVariable UUID loadId) {
        loadService.deleteLoad(loadId);
        return ResponseEntity.ok("Load Status is changed to Cancelled");
    }

    private LoadDTO.Response toResponseDto(Load load) {
        LoadDTO.Response response = new LoadDTO.Response();
        response.setId(load.getId());
        response.setShipperId(load.getShipperId());
        response.setLoadingPoint(load.getLoadingPoint());
        response.setUnloadingPoint(load.getUnloadingPoint());
        response.setLoadingDate(load.getLoadingDate());
        response.setUnloadingDate(load.getUnloadingDate());
        response.setProductType(load.getProductType());
        response.setTruckType(load.getTruckType());
        response.setNoOfTrucks(load.getNoOfTrucks());
        response.setWeight(load.getWeight());
        response.setComment(load.getComment());
        response.setStatus(load.getStatus().name());
        response.setDatePosted(load.getDatePosted());
        return response;
    }
}
