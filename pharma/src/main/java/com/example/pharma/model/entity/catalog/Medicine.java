package com.example.pharma.model.entity.catalog;

import com.example.pharma.model.entity.inventory.InventoryRecord;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "medicine")
public class Medicine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer medicineId;

    private String name;
    private String description;
    private boolean requiresPrescription;
    @Enumerated(EnumType.STRING)
    private DosageForm dosageForm;
    private String strength;
    private String manufacturer;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;
    @JsonIgnore
    @OneToMany(mappedBy = "medicine")
    private List<InventoryRecord> inventoryRecords;
}