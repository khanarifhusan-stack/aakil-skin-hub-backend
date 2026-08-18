package com.example.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.example.demo.entity.Patient;
import com.example.demo.repository.PatientRepository;

@Service
public class PatientService {

    private final PatientRepository patientRepository;

    public PatientService(PatientRepository patientRepository) {
        this.patientRepository = patientRepository;
    }

    // Create patient
    public Patient createPatient(Patient patient) {
        return patientRepository.save(patient);
    }

    // Get all patients
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }

    // Get patient by ID
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }

    // Update patient
    public Optional<Patient> updatePatient(Long id, Patient updatedPatient) {

        return patientRepository.findById(id).map(patient -> {

            patient.setName(updatedPatient.getName());
            patient.setMobile(updatedPatient.getMobile());
            patient.setAge(updatedPatient.getAge());
            patient.setGender(updatedPatient.getGender());
            patient.setSkinCondition(updatedPatient.getSkinCondition());

            return patientRepository.save(patient);
        });
    }

    // Delete patient
    public boolean deletePatient(Long id) {

        if (!patientRepository.existsById(id)) {
            return false;
        }

        patientRepository.deleteById(id);
        return true;
    }
}