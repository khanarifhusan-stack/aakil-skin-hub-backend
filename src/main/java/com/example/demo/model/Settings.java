package com.example.demo.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "user_settings")
public class Settings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long userId;

    // Display & Appearance (Light/Dark Mode light settings)
    private String themeMode; // "LIGHT", "DARK", "SYSTEM"
    private boolean highContrast;

    // Privacy & Security Settings
    private boolean profileVisibility;
    private boolean biometricLock;
    private boolean twoFactorAuth;
    private boolean appointmentReminders;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getThemeMode() {
        return themeMode;
    }

    public void setThemeMode(String themeMode) {
        this.themeMode = themeMode;
    }

    public boolean isHighContrast() {
        return highContrast;
    }

    public void setHighContrast(boolean highContrast) {
        this.highContrast = highContrast;
    }

    public boolean isProfileVisibility() {
        return profileVisibility;
    }

    public void setProfileVisibility(boolean profileVisibility) {
        this.profileVisibility = profileVisibility;
    }

    public boolean isBiometricLock() {
        return biometricLock;
    }

    public void setBiometricLock(boolean biometricLock) {
        this.biometricLock = biometricLock;
    }

    public boolean isTwoFactorAuth() {
        return twoFactorAuth;
    }

    public void setTwoFactorAuth(boolean twoFactorAuth) {
        this.twoFactorAuth = twoFactorAuth;
    }

    public boolean isAppointmentReminders() {
        return appointmentReminders;
    }

    public void setAppointmentReminders(boolean appointmentReminders) {
        this.appointmentReminders = appointmentReminders;
    }
}