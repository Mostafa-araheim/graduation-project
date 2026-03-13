package com.example.pharma.model.entity.order;

import com.example.pharma.model.entity.inventory.InventoryRecord;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
@Entity
@Getter
@Setter
@Table(name = "order_item")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderItemId;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inventory_record_id", nullable = false)
    private InventoryRecord inventoryRecord;

    private Integer quantity;

    private BigDecimal price;
}
