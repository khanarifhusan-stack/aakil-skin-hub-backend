package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.entity.Patient;
import com.example.demo.service.PatientService;

@RestController
@RequestMapping("/api/patients")
@CrossOrigin(origins = "*")
public class PatientController {

    private final PatientService patientService;

    public PatientController(PatientService patientService) {
        this.patientService = patientService;
    }

    // Create a new patient
    @PostMapping
    public ResponseEntity<Patient> createPatient(
            @RequestBody Patient patient) {

        Patient savedPatient = patientService.createPatient(patient);

        return ResponseEntity.ok(savedPatient);
    }

    // Get all patients
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {

        return ResponseEntity.ok(
                patientService.getAllPatients());
    }

    // Get patient by ID
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatientById(
            @PathVariable Long id) {

        return patientService.getPatientById(id)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Update patient
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(
            @PathVariable Long id,
            @RequestBody Patient patient) {

        return patientService.updatePatient(id, patient)
                .map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete patient
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(
            @PathVariable Long id) {

        boolean deleted = patientService.deletePatient(id);

        if (!deleted) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}