package org.sma.jpa.repository.transport;

import org.sma.jpa.model.transport.TransportRoute;
import org.sma.jpa.model.transport.TransportStop;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for TransportStop entity
 */
@Repository
public interface TransportStopRepository extends JpaRepository<TransportStop, UUID> {
    
    List<TransportStop> findByRouteAndIsActiveTrue(TransportRoute route);
    
    List<TransportStop> findByRouteOrderByStopSequenceAsc(TransportRoute route);
}


