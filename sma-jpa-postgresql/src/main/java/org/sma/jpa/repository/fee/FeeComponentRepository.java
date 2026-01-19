package org.sma.jpa.repository.fee;

import org.sma.jpa.model.master.ClassMaster;
import org.sma.jpa.model.fee.FeeComponent;
import org.sma.jpa.model.fee.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for FeeComponent entity
 */
@Repository
public interface FeeComponentRepository extends JpaRepository<FeeComponent, Long> {
    
    List<FeeComponent> findByFeeStructure(FeeStructure feeStructure);
    
    List<FeeComponent> findByFeeStructureAndClassMaster(FeeStructure feeStructure, ClassMaster classMaster);
}
