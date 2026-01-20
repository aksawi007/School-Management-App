package org.sma.jpa.repository.studentmgmt;

import org.sma.jpa.model.studentmgmt.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, UUID> {

    List<Guardian> findByStudentIdAndIsDeletedFalse(UUID studentId);

    Optional<Guardian> findByIdAndStudentIdAndIsDeletedFalse(UUID id, UUID studentId);

    Optional<Guardian> findByStudentIdAndIsPrimaryTrueAndIsDeletedFalse(UUID studentId);

    long countByStudentIdAndIsDeletedFalse(UUID studentId);
}

