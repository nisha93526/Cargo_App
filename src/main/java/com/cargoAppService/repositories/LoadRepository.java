package com.cargoAppService.repositories;

import com.cargoAppService.entities.Load;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface LoadRepository extends JpaRepository<Load, UUID>, JpaSpecificationExecutor<Load> {
}

