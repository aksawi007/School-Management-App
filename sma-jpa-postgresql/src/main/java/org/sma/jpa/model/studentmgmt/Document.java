package org.sma.jpa.model.studentmgmt;

import org.sma.jpa.model.BaseEntity;

import javax.persistence.*;
import java.util.UUID;

/**
 * Document entity - student document metadata
 */
@Entity
@Table(name = "document", schema = "sma_student",
    indexes = {
        @Index(name = "idx_document_student", columnList = "student_id"),
        @Index(name = "idx_document_type", columnList = "doc_type")
    })
public class Document extends BaseEntity {

    @Column(name = "student_id", nullable = false)
    private UUID studentId;

    @Column(name = "doc_type", nullable = false, length = 50)
    private String docType; // PHOTO, AADHAR, BIRTH_CERTIFICATE, TC, MARKSHEET, MEDICAL, OTHER

    @Column(name = "doc_name", nullable = false, length = 200)
    private String docName;

    @Column(name = "storage_key", length = 500)
    private String storageKey; // S3 key or blob storage reference

    @Column(name = "storage_url", length = 1000)
    private String storageUrl; // Public or signed URL

    @Column(name = "file_size")
    private Long fileSize; // in bytes

    @Column(name = "mime_type", length = 100)
    private String mimeType;

    @Column(name = "description", length = 500)
    private String description;

    // Getters and Setters
    public UUID getStudentId() {
        return studentId;
    }

    public void setStudentId(UUID studentId) {
        this.studentId = studentId;
    }

    public String getDocType() {
        return docType;
    }

    public void setDocType(String docType) {
        this.docType = docType;
    }

    public String getDocName() {
        return docName;
    }

    public void setDocName(String docName) {
        this.docName = docName;
    }

    public String getStorageKey() {
        return storageKey;
    }

    public void setStorageKey(String storageKey) {
        this.storageKey = storageKey;
    }

    public String getStorageUrl() {
        return storageUrl;
    }

    public void setStorageUrl(String storageUrl) {
        this.storageUrl = storageUrl;
    }

    public Long getFileSize() {
        return fileSize;
    }

    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
