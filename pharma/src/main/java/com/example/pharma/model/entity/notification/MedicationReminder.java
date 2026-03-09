package com.example.pharma.model.entity.notification;

import com.example.pharma.model.entity.core.CustomerProfile;
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
    private Long reminderId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id", nullable = false)
    private CustomerProfile customer;

    private String medicineName;
    private LocalTime dosageTime;
    private Long notifyBeforeMinutes;
}