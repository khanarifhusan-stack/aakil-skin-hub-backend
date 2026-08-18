package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.Settings;

public interface SettingsRepository extends JpaRepository<Settings, Long> {
    Optional<Settings> findByUserId(Long userId);
}