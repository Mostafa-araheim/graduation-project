package com.example.pharma.model.notification;

import com.example.pharma.model.core.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalTime;

@Entity
@Getter
@Setter
@Table(name = "medication_reminder")
public class MedicationReminder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer reminderId;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    private User customer;

    private String medicineName;
    private LocalTime dosageTime;
    private Integer notifyBeforeMinutes;
}