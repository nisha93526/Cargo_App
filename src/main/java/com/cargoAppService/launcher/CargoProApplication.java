package com.cargoAppService.launcher;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication(scanBasePackages = "com.cargoAppService")
@EnableJpaRepositories(basePackages = "com.cargoAppService.repositories")
@EntityScan(basePackages = "com.cargoAppService.entities")
public class CargoProApplication {

    public static void main(String[] args) {
        SpringApplication.run(CargoProApplication.class, args);

        System.out.println("\nCargoPro Backend System is running!");
        System.out.println("Access Swagger UI at: http://localhost:8080/swagger-ui.html\n");
    }
}