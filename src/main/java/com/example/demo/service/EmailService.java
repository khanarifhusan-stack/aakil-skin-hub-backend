package com.example.demo.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.demo.model.Appointment;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendAppointmentNotification(Appointment appointment) {

        SimpleMailMessage message = new SimpleMailMessage();

        // Doctor's email
        message.setTo("aakilskinhubclinic@gmail.com");

        // Email subject
        message.setSubject("New Appointment Booked - Aakil Skin Hub");

        // Email body
        String emailBody = "New Appointment Booking\n\n" +

                "Appointment ID: " + appointment.getId() + "\n" +
                "Patient ID: " + appointment.getPatientId() + "\n" +
                "Doctor: " + appointment.getDoctorName() + "\n" +
                "Appointment Date: " + appointment.getAppointmentDate() + "\n" +
                "Appointment Time: " + appointment.getAppointmentTime() + "\n" +
                "Status: " + appointment.getStatus() + "\n" +
                "Reason: " + appointment.getReason() + "\n" +
                "Conditions: " + appointment.getConditions() + "\n" +
                "Notes: " + appointment.getNotes() + "\n\n" +

                "Please check the AakilSkin Hub Admin Dashboard for complete details.";

        message.setText(emailBody);

        mailSender.send(message);
    }
}