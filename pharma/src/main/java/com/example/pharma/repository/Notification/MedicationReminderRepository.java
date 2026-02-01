package com.example.pharma.repository.Notification;

import com.example.pharma.model.entity.notification.MedicationReminder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicationReminderRepository extends JpaRepository<MedicationReminder, Integer> { }
