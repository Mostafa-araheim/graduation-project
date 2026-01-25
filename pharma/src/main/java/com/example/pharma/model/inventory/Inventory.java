package com.example.pharma.model.inventory;

import com.example.pharma.model.pharmacy.Pharmacy;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "inventory")
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer inventoryId;

    @OneToOne
    @JoinColumn(name = "pharmacy_id")
    private Pharmacy pharmacy;
}
