package com.example.pharma.model.P2P;

import com.example.pharma.model.core.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "p2p_transaction")
public class P2PTransaction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer transactionId;

    @OneToOne
    @JoinColumn(name = "listing_id")
    private P2PListing listing;

    @ManyToOne
    @JoinColumn(name = "buyer_id")
    private User buyer;

    private String status;
    private LocalDateTime createdAt;
}
