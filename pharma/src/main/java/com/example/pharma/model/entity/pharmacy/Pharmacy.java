package com.example.pharma.model.entity.pharmacy;

import com.example.pharma.model.entity.core.CreatedAtColumn;
import com.example.pharma.model.entity.core.OwnerProfile;
import com.example.pharma.model.entity.inventory.Inventory;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.math.BigDecimal;
import java.time.LocalTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "pharmacy")
public class Pharmacy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "pharmacy_id")
    private Long pharmacyId;

    @ManyToOne(optional = true, fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id", nullable = true)
    private OwnerProfile owner;

    private String name;
    @Column(name = "image_url")
    private String imageUrl;
    @JsonIgnore
    @Column(columnDefinition = "geometry(Point, 4326)")
    private Point location;
    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;
    @Embedded
    private CreatedAtColumn createdAt;

    @OneToOne(mappedBy = "pharmacy", cascade = CascadeType.ALL, orphanRemoval = true)
    private PharmacyAddress address;

    @OneToOne(mappedBy = "pharmacy", cascade = CascadeType.ALL, orphanRemoval = true)
    private Inventory inventory;

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "is_24_hours")
    private Boolean is24Hours;

    @Column(name = "average_rating")
    private BigDecimal averageRating;

    @Column(name = "rating_count")
    private Long ratingCount;

    @Column(name = "review_count")
    private Long reviewCount;

    @OneToMany(mappedBy = "pharmacy", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PharmacyStaff> staff;

    @PrePersist
    @PreUpdate
    public void updateGeometry() {
        if (latitude != null && longitude != null) {
            GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
            this.location = factory.createPoint(new Coordinate(this.longitude, this.latitude));
        }
    }

    public boolean isOpen() {

        if (Boolean.TRUE.equals(is24Hours)) {
            return true;
        }

        if (openingTime == null || closingTime == null) {
            return false;
        }

        LocalTime now = LocalTime.now();


        if (closingTime.isAfter(openingTime)) {
            return !now.isBefore(openingTime) && !now.isAfter(closingTime);
        }

        return !now.isBefore(openingTime) || !now.isAfter(closingTime);
    }
    public boolean isClosed()
    {
        return !isOpen();
    }

    public void setInventory(Inventory inventory) {
        this.inventory = inventory;
        if (inventory != null) {
            inventory.setPharmacy(this);
        }
    }

    public void setAddress(PharmacyAddress address) {
        this.address = address;
        if (address != null) {
            address.setPharmacy(this);
        }
    }
}