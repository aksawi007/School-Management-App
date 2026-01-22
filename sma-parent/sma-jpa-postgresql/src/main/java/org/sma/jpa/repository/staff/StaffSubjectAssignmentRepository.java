package org.sma.jpa.repository.staff;

import org.sma.jpa.model.school.AcademicYear;
import org.sma.jpa.model.master.ClassMaster;
import org.sma.jpa.model.master.SectionMaster;
import org.sma.jpa.model.master.SubjectMaster;
import org.sma.jpa.model.staff.Staff;
import org.sma.jpa.model.staff.StaffSubjectAssignment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for StaffSubjectAssignment entity
 */
@Repository
public interface StaffSubjectAssignmentRepository extends JpaRepository<StaffSubjectAssignment, Long> {
    
    List<StaffSubjectAssignment> findByStaffAndAcademicYear(Staff staff, AcademicYear academicYear);
    
    List<StaffSubjectAssignment> findByAcademicYearAndClassMasterAndSection(AcademicYear academicYear, ClassMaster classMaster, SectionMaster section);
    
    List<StaffSubjectAssignment> findBySubjectAndAcademicYear(SubjectMaster subject, AcademicYear academicYear);
}


