package com.example.pharma.repository.Notification;

import com.example.pharma.model.entity.notification.MedicationReminder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MedicationReminderRepository extends JpaRepository<MedicationReminder, Long> {
    List<MedicationReminder> findByCustomer_UserId(Long userId);
}