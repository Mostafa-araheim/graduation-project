package com.example.pharma.model.entity.P2P;

import com.example.pharma.model.entity.core.CreatedAtColumn;
import com.example.pharma.model.entity.core.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

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
    @Embedded
    private CreatedAtColumn createdAt;}
