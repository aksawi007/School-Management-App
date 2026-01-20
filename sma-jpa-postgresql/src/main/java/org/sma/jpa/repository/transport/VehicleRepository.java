package org.sma.jpa.repository.transport;

import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.transport.Vehicle;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for Vehicle entity
 */
@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, UUID> {
    
    Optional<Vehicle> findBySchoolAndVehicleNumber(SchoolProfile school, String vehicleNumber);
    
    List<Vehicle> findBySchoolAndVehicleStatus(SchoolProfile school, String vehicleStatus);
    
    List<Vehicle> findBySchoolAndIsActiveTrue(SchoolProfile school);
}


