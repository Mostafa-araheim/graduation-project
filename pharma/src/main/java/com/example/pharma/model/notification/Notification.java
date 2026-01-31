package com.example.pharma.model.notification;

import com.example.pharma.model.core.CreatedAtColumn;
import com.example.pharma.model.core.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "notification")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer notificationId;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    private String type;
    private String message;
    private boolean isRead;
    @Embedded
    private CreatedAtColumn createdAt;}
