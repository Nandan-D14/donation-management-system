package com.donation.system;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

/**
 * Main Spring Boot Application
 * @author Nandani
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.donation.system")
@EntityScan(basePackages = "com.donation.system.model.entity")
@EnableJpaRepositories(basePackages = "com.donation.system.repository")
public class DonationManagementSystemApplication {

    public static void main(String[] args) {
        SpringApplication.run(DonationManagementSystemApplication.class, args);
    }
}