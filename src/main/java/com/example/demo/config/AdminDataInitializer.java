package com.example.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.example.demo.entity.Admin;
import com.example.demo.repository.AdminRepository;

@Configuration
public class AdminDataInitializer {

    @Bean
    CommandLineRunner initializeAdmin(AdminRepository adminRepository) {

        return args -> {

            String adminUsername = "6399630693";
            String adminPassword = "a1k2i3l4";

            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

            Admin admin = adminRepository
                    .findByUsername(adminUsername)
                    .orElse(null);

            if (admin == null) {

                Admin newAdmin = new Admin(
                        adminUsername,
                        passwordEncoder.encode(adminPassword));

                newAdmin.setRole("ADMIN");
                newAdmin.setActive(true);

                adminRepository.save(newAdmin);

                System.out.println("Default Admin user created successfully!");

            } else {

                // Convert old plain-text password to BCrypt only once
                if (!admin.getPassword().startsWith("$2a$")
                        && !admin.getPassword().startsWith("$2b$")
                        && !admin.getPassword().startsWith("$2y$")) {

                    admin.setPassword(
                            passwordEncoder.encode(admin.getPassword()));

                    adminRepository.save(admin);

                    System.out.println(
                            "Existing Admin password converted to BCrypt successfully!");

                } else {

                    System.out.println("Admin user already exists and password is already secured.");
                }
            }
        };
    }
}