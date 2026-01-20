package org.sma.jpa.repository.studentmgmt;

import org.sma.jpa.model.studentmgmt.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface AddressRepository extends JpaRepository<Address, UUID> {

    List<Address> findByStudentIdAndIsDeletedFalse(UUID studentId);

    Optional<Address> findByIdAndStudentIdAndIsDeletedFalse(UUID id, UUID studentId);

    Optional<Address> findByStudentIdAndAddressTypeAndIsDeletedFalse(UUID studentId, String addressType);
}

