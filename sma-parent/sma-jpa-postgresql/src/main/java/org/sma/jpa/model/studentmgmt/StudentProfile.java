package org.sma.jpa.model.studentmgmt;

import org.sma.jpa.model.BaseEntity;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.UUID;

/**
 * StudentProfile entity - comprehensive student information for Student Management Service
 * Uses sma_student schema for student-specific operations
 */
@Entity
@Table(name = "student_profile", schema = "sma_student",
    uniqueConstraints = @UniqueConstraint(columnNames = {"school_id", "admission_no"}),
    indexes = {
        @Index(name = "idx_student_school", columnList = "school_id"),
        @Index(name = "idx_student_admission", columnList = "admission_no"),
        @Index(name = "idx_student_status", columnList = "status"),
        @Index(name = "idx_student_name", columnList = "first_name, last_name")
    })
public class StudentProfile extends BaseEntity {

    @Column(name = "school_id", nullable = false)
    private UUID schoolId;

    @Column(name = "admission_no", nullable = false, length = 50)
    private String admissionNo;

    @Column(name = "first_name", nullable = false, length = 100)
    private String firstName;

    @Column(name = "middle_name", length = 100)
    private String middleName;

    @Column(name = "last_name", nullable = false, length = 100)
    private String lastName;

    @Column(name = "gender", length = 20)
    private String gender;

    @Column(name = "date_of_birth", nullable = false)
    private LocalDate dateOfBirth;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "blood_group", length = 10)
    private String bloodGroup;

    @Column(name = "religion", length = 50)
    private String religion;

    @Column(name = "caste", length = 50)
    private String caste;

    @Column(name = "nationality", length = 50)
    private String nationality;

    @Column(name = "mother_tongue", length = 50)
    private String motherTongue;

    @Column(name = "aadhar_no", length = 20)
    private String aadharNo;

    @Column(name = "status", nullable = false, length = 20)
    private String status; // ACTIVE, INACTIVE, TRANSFERRED, GRADUATED, WITHDRAWN

    @Column(name = "admission_date", nullable = false)
    private LocalDate admissionDate;

    @Column(name = "photo_url", length = 500)
    private String photoUrl;

    @Column(name = "remarks", length = 1000)
    private String remarks;

    @Column(name = "medical_conditions", length = 500)
    private String medicalConditions;

    @Column(name = "allergies", length = 500)
    private String allergies;

    // Getters and Setters
    public UUID getSchoolId() { return schoolId; }
    public void setSchoolId(UUID schoolId) { this.schoolId = schoolId; }

    public String getAdmissionNo() { return admissionNo; }
    public void setAdmissionNo(String admissionNo) { this.admissionNo = admissionNo; }

    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }

    public String getMiddleName() { return middleName; }
    public void setMiddleName(String middleName) { this.middleName = middleName; }

    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public String getReligion() { return religion; }
    public void setReligion(String religion) { this.religion = religion; }

    public String getCaste() { return caste; }
    public void setCaste(String caste) { this.caste = caste; }

    public String getNationality() { return nationality; }
    public void setNationality(String nationality) { this.nationality = nationality; }

    public String getMotherTongue() { return motherTongue; }
    public void setMotherTongue(String motherTongue) { this.motherTongue = motherTongue; }

    public String getAadharNo() { return aadharNo; }
    public void setAadharNo(String aadharNo) { this.aadharNo = aadharNo; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDate getAdmissionDate() { return admissionDate; }
    public void setAdmissionDate(LocalDate admissionDate) { this.admissionDate = admissionDate; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }

    public String getRemarks() { return remarks; }
    public void setRemarks(String remarks) { this.remarks = remarks; }

    public String getMedicalConditions() { return medicalConditions; }
    public void setMedicalConditions(String medicalConditions) { this.medicalConditions = medicalConditions; }

    public String getAllergies() { return allergies; }
    public void setAllergies(String allergies) { this.allergies = allergies; }
}
