package org.sma.jpa.repository.exam;

import org.sma.jpa.model.master.ClassMaster;
import org.sma.jpa.model.exam.Exam;
import org.sma.jpa.model.exam.ExamSchedule;
import org.sma.jpa.model.master.SubjectMaster;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository for ExamSchedule entity
 */
@Repository
public interface ExamScheduleRepository extends JpaRepository<ExamSchedule, Long> {
    
    List<ExamSchedule> findByExam(Exam exam);
    
    List<ExamSchedule> findByExamAndClassMaster(Exam exam, ClassMaster classMaster);
    
    List<ExamSchedule> findByExamAndExamDate(Exam exam, LocalDate examDate);
}
