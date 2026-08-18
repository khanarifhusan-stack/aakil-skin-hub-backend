package com.example.demo.service;

import org.springframework.stereotype.Service;

import com.example.demo.model.Settings;
import com.example.demo.repository.SettingsRepository;

@Service
public class SettingsService {

    private final SettingsRepository settingsRepository;

    public SettingsService(SettingsRepository settingsRepository) {
        this.settingsRepository = settingsRepository;
    }

    public Settings getSettingsByUserId(Long userId) {
        return settingsRepository.findByUserId(userId).orElseGet(() -> {
            Settings defaultSettings = new Settings();
            defaultSettings.setUserId(userId);
            defaultSettings.setThemeMode("LIGHT"); // Default display light/theme
            defaultSettings.setHighContrast(false);
            defaultSettings.setProfileVisibility(true);
            defaultSettings.setBiometricLock(false);
            defaultSettings.setTwoFactorAuth(false);
            defaultSettings.setAppointmentReminders(true);
            return settingsRepository.save(defaultSettings);
        });
    }

    public Settings updateSettings(Long userId, Settings newSettings) {
        Settings existing = getSettingsByUserId(userId);

        existing.setThemeMode(newSettings.getThemeMode());
        existing.setHighContrast(newSettings.isHighContrast());
        existing.setProfileVisibility(newSettings.isProfileVisibility());
        existing.setBiometricLock(newSettings.isBiometricLock());
        existing.setTwoFactorAuth(newSettings.isTwoFactorAuth());
        existing.setAppointmentReminders(newSettings.isAppointmentReminders());

        return settingsRepository.save(existing);
    }
}