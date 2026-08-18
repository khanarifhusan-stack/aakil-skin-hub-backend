package com.example.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Settings;
import com.example.demo.service.SettingsService;

@RestController
@RequestMapping("/api/settings")
public class SettingsController {

    private final SettingsService settingsService;

    public SettingsController(SettingsService settingsService) {
        this.settingsService = settingsService;
    }

    @GetMapping("/{userId}")
    public ResponseEntity<Settings> getSettings(
            @PathVariable Long userId) {

        return ResponseEntity.ok(
                settingsService.getSettingsByUserId(userId));
    }

    @PutMapping("/{userId}")
    public ResponseEntity<Settings> updateSettings(
            @PathVariable Long userId,
            @RequestBody Settings newSettings) {

        Settings updated = settingsService.updateSettings(
                userId, newSettings);

        return ResponseEntity.ok(updated);
    }
}