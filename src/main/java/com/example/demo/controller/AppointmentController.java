package com.example.demo.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Appointment;
import com.example.demo.service.AppointmentService;

@RestController
@RequestMapping("/api/appointments")
public class AppointmentController {

    private final AppointmentService appointmentService;

    public AppointmentController(
            AppointmentService appointmentService) {

        this.appointmentService = appointmentService;
    }

    // CREATE
    @PostMapping
    public ResponseEntity<Appointment> createAppointment(
            @RequestBody Appointment appointment) {

        return ResponseEntity.ok(
                appointmentService.createAppointment(appointment));
    }

    // GET ALL
    @GetMapping
    public ResponseEntity<List<Appointment>> getAllAppointments() {

        return ResponseEntity.ok(
                appointmentService.getAllAppointments());
    }

    // GET BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Appointment> getAppointmentById(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                appointmentService.getAppointmentById(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Appointment> updateAppointment(
            @PathVariable Long id,
            @RequestBody Appointment appointment) {

        return ResponseEntity.ok(
                appointmentService.updateAppointment(
                        id, appointment));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteAppointment(
            @PathVariable Long id) {

        appointmentService.deleteAppointment(id);

        return ResponseEntity.ok(
                "Appointment deleted successfully");
    }
}