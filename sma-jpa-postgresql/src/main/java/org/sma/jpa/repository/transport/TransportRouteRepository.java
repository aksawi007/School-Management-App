package org.sma.jpa.repository.transport;

import org.sma.jpa.model.school.SchoolProfile;
import org.sma.jpa.model.transport.TransportRoute;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for TransportRoute entity
 */
@Repository
public interface TransportRouteRepository extends JpaRepository<TransportRoute, UUID> {
    
    Optional<TransportRoute> findBySchoolAndRouteCode(SchoolProfile school, String routeCode);
    
    List<TransportRoute> findBySchoolAndRouteStatus(SchoolProfile school, String routeStatus);
    
    List<TransportRoute> findBySchoolAndIsActiveTrue(SchoolProfile school);
}


