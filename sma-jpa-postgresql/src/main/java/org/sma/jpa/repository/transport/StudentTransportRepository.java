package org.sma.jpa.repository.transport;

import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.model.transport.StudentTransport;
import org.sma.jpa.model.transport.TransportRoute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository for StudentTransport entity
 */
@Repository
public interface StudentTransportRepository extends JpaRepository<StudentTransport, UUID> {
    
    Optional<StudentTransport> findByStudentIdAndAcademicYear(UUID studentId, AcademicYear academicYear);
    
    List<StudentTransport> findByRouteAndAcademicYear(TransportRoute route, AcademicYear academicYear);
    
    List<StudentTransport> findByAcademicYearAndAllocationStatus(AcademicYear academicYear, String allocationStatus);
}

