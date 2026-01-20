package org.sma.jpa.model.transport;

import org.sma.jpa.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalTime;

/**
 * Entity for Transport Stop - Stops along a route
 */
@Entity
@Table(name = "transport_stop", schema = "sma_admin")
public class TransportStop extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "route_id", nullable = false)
    private TransportRoute route;

    @Column(name = "stop_name", nullable = false, length = 150)
    private String stopName;

    @Column(name = "stop_address", length = 255)
    private String stopAddress;

    @Column(name = "stop_sequence", nullable = false)
    private Integer stopSequence;

    @Column(name = "pickup_time")
    private LocalTime pickupTime;

    @Column(name = "drop_time")
    private LocalTime dropTime;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    // Getters and Setters
    public TransportRoute getRoute() {
        return route;
    }

    public void setRoute(TransportRoute route) {
        this.route = route;
    }

    public String getStopName() {
        return stopName;
    }

    public void setStopName(String stopName) {
        this.stopName = stopName;
    }

    public String getStopAddress() {
        return stopAddress;
    }

    public void setStopAddress(String stopAddress) {
        this.stopAddress = stopAddress;
    }

    public Integer getStopSequence() {
        return stopSequence;
    }

    public void setStopSequence(Integer stopSequence) {
        this.stopSequence = stopSequence;
    }

    public LocalTime getPickupTime() {
        return pickupTime;
    }

    public void setPickupTime(LocalTime pickupTime) {
        this.pickupTime = pickupTime;
    }

    public LocalTime getDropTime() {
        return dropTime;
    }

    public void setDropTime(LocalTime dropTime) {
        this.dropTime = dropTime;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
}
