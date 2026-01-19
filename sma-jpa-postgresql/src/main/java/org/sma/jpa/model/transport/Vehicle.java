package org.sma.jpa.model.transport;

import org.sma.jpa.model.BaseEntity;
import org.sma.jpa.model.school.SchoolProfile;

import javax.persistence.*;

/**
 * Entity for Vehicle/Bus
 */
@Entity
@Table(name = "vehicle", schema = "sma_admin",
        uniqueConstraints = @UniqueConstraint(columnNames = {"school_id", "vehicle_number"}))
public class Vehicle extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "school_id", nullable = false)
    private SchoolProfile school;

    @Column(name = "vehicle_number", nullable = false, length = 50)
    private String vehicleNumber;

    @Column(name = "vehicle_type", length = 30)
    private String vehicleType; // BUS, VAN, CAR

    @Column(name = "vehicle_model", length = 100)
    private String vehicleModel;

    @Column(name = "seating_capacity", nullable = false)
    private Integer seatingCapacity;

    @Column(name = "driver_name", length = 150)
    private String driverName;

    @Column(name = "driver_phone", length = 20)
    private String driverPhone;

    @Column(name = "driver_license_number", length = 50)
    private String driverLicenseNumber;

    @Column(name = "conductor_name", length = 150)
    private String conductorName;

    @Column(name = "conductor_phone", length = 20)
    private String conductorPhone;

    @Column(name = "insurance_number", length = 100)
    private String insuranceNumber;

    @Column(name = "fitness_certificate_number", length = 100)
    private String fitnessCertificateNumber;

    @Column(name = "vehicle_status", length = 20)
    private String vehicleStatus; // ACTIVE, INACTIVE, MAINTENANCE, RETIRED

    @Column(name = "remarks", length = 500)
    private String remarks;

    // Getters and Setters
    public SchoolProfile getSchool() {
        return school;
    }

    public void setSchool(SchoolProfile school) {
        this.school = school;
    }

    public String getVehicleNumber() {
        return vehicleNumber;
    }

    public void setVehicleNumber(String vehicleNumber) {
        this.vehicleNumber = vehicleNumber;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public void setVehicleType(String vehicleType) {
        this.vehicleType = vehicleType;
    }

    public String getVehicleModel() {
        return vehicleModel;
    }

    public void setVehicleModel(String vehicleModel) {
        this.vehicleModel = vehicleModel;
    }

    public Integer getSeatingCapacity() {
        return seatingCapacity;
    }

    public void setSeatingCapacity(Integer seatingCapacity) {
        this.seatingCapacity = seatingCapacity;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhone() {
        return driverPhone;
    }

    public void setDriverPhone(String driverPhone) {
        this.driverPhone = driverPhone;
    }

    public String getDriverLicenseNumber() {
        return driverLicenseNumber;
    }

    public void setDriverLicenseNumber(String driverLicenseNumber) {
        this.driverLicenseNumber = driverLicenseNumber;
    }

    public String getConductorName() {
        return conductorName;
    }

    public void setConductorName(String conductorName) {
        this.conductorName = conductorName;
    }

    public String getConductorPhone() {
        return conductorPhone;
    }

    public void setConductorPhone(String conductorPhone) {
        this.conductorPhone = conductorPhone;
    }

    public String getInsuranceNumber() {
        return insuranceNumber;
    }

    public void setInsuranceNumber(String insuranceNumber) {
        this.insuranceNumber = insuranceNumber;
    }

    public String getFitnessCertificateNumber() {
        return fitnessCertificateNumber;
    }

    public void setFitnessCertificateNumber(String fitnessCertificateNumber) {
        this.fitnessCertificateNumber = fitnessCertificateNumber;
    }

    public String getVehicleStatus() {
        return vehicleStatus;
    }

    public void setVehicleStatus(String vehicleStatus) {
        this.vehicleStatus = vehicleStatus;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}
