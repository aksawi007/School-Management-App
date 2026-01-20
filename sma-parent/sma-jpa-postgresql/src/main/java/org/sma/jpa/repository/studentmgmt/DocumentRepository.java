package org.sma.jpa.repository.studentmgmt;

import org.sma.jpa.model.studentmgmt.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DocumentRepository extends JpaRepository<Document, UUID> {

    List<Document> findByStudentIdAndIsDeletedFalse(UUID studentId);

    Optional<Document> findByIdAndStudentIdAndIsDeletedFalse(UUID id, UUID studentId);

    List<Document> findByStudentIdAndDocTypeAndIsDeletedFalse(UUID studentId, String docType);

    long countByStudentIdAndIsDeletedFalse(UUID studentId);
}

