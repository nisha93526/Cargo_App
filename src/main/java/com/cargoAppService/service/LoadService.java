package com.cargoAppService.service;


import com.cargoAppService.dto.LoadDTO;
import com.cargoAppService.entities.Load;
import com.cargoAppService.repositories.LoadRepository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Predicate;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class LoadService {

    private final LoadRepository loadRepository;

    public LoadService(LoadRepository loadRepository) {
        this.loadRepository = loadRepository;
    }

    @Transactional
    public Load createLoad(LoadDTO.Create createDto) {
        Load load = new Load();

        load.setShipperId(createDto.getShipperId());
        load.setLoadingPoint(createDto.getLoadingPoint());
        load.setUnloadingPoint(createDto.getUnloadingPoint());
        load.setLoadingDate(createDto.getLoadingDate());
        load.setUnloadingDate(createDto.getUnloadingDate());
        load.setProductType(createDto.getProductType());
        load.setTruckType(createDto.getTruckType());
        load.setNoOfTrucks(createDto.getNoOfTrucks());
        load.setWeight(createDto.getWeight());
        load.setComment(createDto.getComment());

        // Set default status and post date
        load.setStatus(Load.Status.POSTED);
        load.setDatePosted(new Timestamp(System.currentTimeMillis()));

        return loadRepository.save(load);
    }

    @Transactional(readOnly = true)
    public Page<Load> getLoads(String shipperId, String truckType, String status, Pageable pageable) {
        Specification<Load> spec = (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (shipperId != null) {
                predicates.add(criteriaBuilder.equal(root.get("shipperId"), shipperId));
            }
            if (truckType != null) {
                predicates.add(criteriaBuilder.equal(root.get("truckType"), truckType));
            }
            if (status != null) {
                predicates.add(criteriaBuilder.equal(root.get("status"), Load.Status.valueOf(status.toUpperCase())));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
        return loadRepository.findAll(spec, pageable);
    }

    @Transactional(readOnly = true)
    public Load getLoadById(UUID loadId) {
        return loadRepository.findById(loadId)
                             .orElseThrow(() -> new RuntimeException("Load not found with id: " + loadId));
    }

    @Transactional
    public Load updateLoad(UUID loadId, LoadDTO.Update updateDto) {
        Load existingLoad = getLoadById(loadId);
        // Update fields if they are provided in the DTO
        if (updateDto.getLoadingPoint() != null) {
            existingLoad.setLoadingPoint(updateDto.getLoadingPoint());
        }
        if (updateDto.getUnloadingPoint() != null) {
            existingLoad.setUnloadingPoint(updateDto.getUnloadingPoint());
        }
        if (updateDto.getProductType() != null) {
            existingLoad.setProductType(updateDto.getProductType());
        }
        if (updateDto.getTruckType() != null) {
            existingLoad.setTruckType(updateDto.getTruckType());
        }
        if (updateDto.getNoOfTrucks() != null) {
            existingLoad.setNoOfTrucks(updateDto.getNoOfTrucks());
        }
        if (updateDto.getWeight() != null) {
            existingLoad.setWeight(updateDto.getWeight());
        }
        if (updateDto.getComment() != null) {
            existingLoad.setComment(updateDto.getComment());
        }
        existingLoad.setStatus(Load.Status.POSTED);
        return loadRepository.save(existingLoad);
    }

    @Transactional
    public void deleteLoad(UUID loadId) {
        Load load = getLoadById(loadId);
        load.setStatus(Load.Status.CANCELLED);
        loadRepository.save(load);
    }
}
