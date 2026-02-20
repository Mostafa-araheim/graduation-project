package com.example.pharma.service;
import com.example.pharma.model.entity.inventory.Inventory;
import com.example.pharma.model.entity.pharmacy.Pharmacy;
import com.example.pharma.repository.Inventory.InventoryRepository;
import com.example.pharma.repository.Inventory.InventoryRecordRepository;

import com.example.pharma.repository.Pharmacy.PharmacyRepository;
import com.example.pharma.util.OperationResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

// let the pharmacist to add his inventory manually
@Service
@RequiredArgsConstructor
public class InventoryService {
    private final InventoryRepository inventoryRepository;
    private final InventoryRecordRepository inventoryRecordRepository;
    private final PharmacyRepository pharmacyRepository;
    // add invenbtory to the pharmacy
    public OperationResult<Integer> AddInventory(Integer pharmacyId)
    {
        Optional<Pharmacy> pharmacy = pharmacyRepository.findById(pharmacyId);
        if(pharmacy.isEmpty())
            return OperationResult.Failure("pharmacy","pharmacy not found");
        Optional<Inventory> existedInventory = inventoryRepository.findByPharmacy_PharmacyId(pharmacyId);
        if(!existedInventory.isEmpty())
            return OperationResult.Failure("inventory","the pharmacy already has inventory");
        Inventory inventory = new Inventory();
        inventory.setPharmacy(pharmacy.get());
        var x = inventoryRepository.save(inventory);
        return OperationResult.Success();
    }
}
