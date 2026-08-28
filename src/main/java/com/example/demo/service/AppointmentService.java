package com.example.demo.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.demo.model.Appointment;
import com.example.demo.repository.AppointmentRepository;
import com.example.demo.repository.PatientRepository;

@Service
public class AppointmentService {

        private final AppointmentRepository appointmentRepository;
        private final PatientRepository patientRepository;
        private final EmailService emailService;

        public AppointmentService(
                        AppointmentRepository appointmentRepository,
                        PatientRepository patientRepository,
                        EmailService emailService) {

                this.appointmentRepository = appointmentRepository;
                this.patientRepository = patientRepository;
                this.emailService = emailService;
        }

        // ============================================================
        // CREATE APPOINTMENT
        // ============================================================

        public Appointment createAppointment(Appointment appointment) {

                if (appointment.getDoctorName() == null ||
                                appointment.getDoctorName().trim().isEmpty()) {

                        appointment.setDoctorName("Mohd Aakil");
                }

                if (appointment.getStatus() == null ||
                                appointment.getStatus().trim().isEmpty()) {

                        appointment.setStatus("Scheduled");
                }

                // Save appointment first
                Appointment savedAppointment = appointmentRepository.save(appointment);

                // Send email notification to doctor
                emailService.sendAppointmentNotification(savedAppointment);

                return savedAppointment;
        }

        // ============================================================
        // GET ALL APPOINTMENTS
        // ============================================================

        public List<Appointment> getAllAppointments() {

                return appointmentRepository.findAll();
        }

        // ============================================================
        // GET APPOINTMENT BY ID
        // ============================================================

        public Appointment getAppointmentById(Long id) {

                return appointmentRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException(
                                                "Appointment not found with ID: " + id));
        }

        // ============================================================
        // UPDATE APPOINTMENT
        // ============================================================

        public Appointment updateAppointment(
                        Long id,
                        Appointment updatedAppointment) {

                Appointment existingAppointment = getAppointmentById(id);

                existingAppointment.setPatientId(
                                updatedAppointment.getPatientId());

                existingAppointment.setDoctorName(
                                updatedAppointment.getDoctorName());

                existingAppointment.setAppointmentDate(
                                updatedAppointment.getAppointmentDate());

                existingAppointment.setAppointmentTime(
                                updatedAppointment.getAppointmentTime());

                existingAppointment.setStatus(
                                updatedAppointment.getStatus());

                existingAppointment.setReason(
                                updatedAppointment.getReason());

                existingAppointment.setConditions(
                                updatedAppointment.getConditions());

                existingAppointment.setNotes(
                                updatedAppointment.getNotes());

                return appointmentRepository.save(existingAppointment);
        }

        // ============================================================
        // DELETE APPOINTMENT
        // ============================================================

        public void deleteAppointment(Long id) {

                if (!appointmentRepository.existsById(id)) {

                        throw new RuntimeException(
                                        "Appointment not found with ID: " + id);
                }

                appointmentRepository.deleteById(id);
        }
}