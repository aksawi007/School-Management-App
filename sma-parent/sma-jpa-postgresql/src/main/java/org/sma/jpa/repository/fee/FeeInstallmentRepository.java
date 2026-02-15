package org.sma.jpa.repository.fee;

import org.sma.jpa.model.fee.FeeInstallment;
import org.sma.jpa.model.fee.FeePlan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for FeeInstallment entity
 */
@Repository
public interface FeeInstallmentRepository extends JpaRepository<FeeInstallment, Long> {
    
    List<FeeInstallment> findByFeePlanOrderByInstallmentNo(FeePlan feePlan);
    
    List<FeeInstallment> findByFeePlanAndStatus(FeePlan feePlan, String status);
    
    Optional<FeeInstallment> findByFeePlanAndInstallmentNo(FeePlan feePlan, Integer installmentNo);
}
