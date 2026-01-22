package org.sma.jpa.repository.studentmgmt;

import org.sma.jpa.model.studentmgmt.Document;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DocumentRepository extends JpaRepository<Document, Long> {

    List<Document> findByStudentIdAndIsDeletedFalse(Long studentId);

    Optional<Document> findByIdAndStudentIdAndIsDeletedFalse(Long id, Long studentId);

    List<Document> findByStudentIdAndDocTypeAndIsDeletedFalse(Long studentId, String docType);

    long countByStudentIdAndIsDeletedFalse(Long studentId);
}

