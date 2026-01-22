package org.sma.jpa.repository.studentmgmt;

import org.sma.jpa.model.studentmgmt.Guardian;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GuardianRepository extends JpaRepository<Guardian, Long> {

    List<Guardian> findByStudentIdAndIsDeletedFalse(Long studentId);

    Optional<Guardian> findByIdAndStudentIdAndIsDeletedFalse(Long id, Long studentId);

    Optional<Guardian> findByStudentIdAndIsPrimaryTrueAndIsDeletedFalse(Long studentId);

    long countByStudentIdAndIsDeletedFalse(Long studentId);
}

