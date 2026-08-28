package com.example.demo.controller;

import java.util.Optional;

import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Admin;
import com.example.demo.repository.AdminRepository;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final AdminRepository adminRepository;
    private final BCryptPasswordEncoder passwordEncoder;

    public AdminController(AdminRepository adminRepository) {
        this.adminRepository = adminRepository;
        this.passwordEncoder = new BCryptPasswordEncoder();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Admin loginRequest) {

        Optional<Admin> adminOptional = adminRepository.findByUsername(loginRequest.getUsername());

        if (adminOptional.isEmpty()) {
            return ResponseEntity
                    .status(401)
                    .body("Invalid username or password");
        }

        Admin admin = adminOptional.get();

        if (!admin.isActive()) {
            return ResponseEntity
                    .status(403)
                    .body("Admin account is inactive");
        }

        if (!passwordEncoder.matches(
                loginRequest.getPassword(),
                admin.getPassword())) {

            return ResponseEntity
                    .status(401)
                    .body("Invalid username or password");
        }

        return ResponseEntity.ok(admin);
    }
}