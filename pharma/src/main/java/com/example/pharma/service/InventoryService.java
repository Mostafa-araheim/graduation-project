package com.example.pharma.service;

import com.example.pharma.model.entity.inventory.Inventory;
import com.example.pharma.model.entity.pharmacy.Pharmacy;
import com.example.pharma.repository.Inventory.InventoryRecordRepository;
import com.example.pharma.repository.Inventory.InventoryRepository;
import com.example.pharma.repository.Pharmacy.PharmacyRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

// let the pharmacist to add his inventory manually
@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryRecordRepository inventoryRecordRepository;
    private final PharmacyRepository pharmacyRepository;
    // add invenbtory to the pharmacy
    public Long AddInventory(Long pharmacyId)
    {
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId).orElseThrow(() ->
                new EntityNotFoundException("Pharmacy is not found for"));

        Inventory existedInventory = inventoryRepository.findByPharmacy_PharmacyId(pharmacyId).orElseThrow( ()-> new EntityNotFoundException("the pharmacy already has inventory"));
        Inventory inventory = new Inventory();
        inventory.setPharmacy(pharmacy);
        var x = inventoryRepository.save(inventory);
        return x.getInventoryId();
    }
}
