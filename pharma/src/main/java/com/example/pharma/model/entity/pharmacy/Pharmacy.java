package com.example.pharma.model.entity.pharmacy;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.example.pharma.model.entity.core.CreatedAtColumn;
import com.example.pharma.model.entity.core.OwnerProfile;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;

import java.math.BigDecimal;
import java.time.LocalTime;

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

    @Column(name = "total_rating")
    private BigDecimal totalRating;

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

    @Column(name = "opening_time")
    private LocalTime openingTime;

    @Column(name = "closing_time")
    private LocalTime closingTime;

    @Column(name = "is_24_hours")
    private Boolean is24Hours;
    @PrePersist
    @PreUpdate
    public void updateGeometry() {
        if (latitude != null && longitude != null) {
            GeometryFactory factory = new GeometryFactory(new PrecisionModel(), 4326);
            this.location = factory.createPoint(new Coordinate(this.longitude, this.latitude));
        }
    }
}