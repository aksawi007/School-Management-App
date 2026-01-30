/**
 * Student Class Section Models
 * Manages student assignments to classes and sections
 */

export interface StudentClassSectionRequest {
  studentId: number;
  schoolId: number;
  academicYearId: number;
  classId: number;
  sectionId: number;
  enrollmentDate?: string;
  rollNumber?: string;
  remarks?: string;
}

export interface StudentClassSectionResponse {
  mappingId: number;
  studentId: number;
  admissionNumber: string;
  studentName: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  schoolId: number;
  academicYearId: number;
  academicYearName: string;
  classId: number;
  className: string;
  classCode: string;
  sectionId: number;
  sectionName: string;
  sectionCode: string;
  enrollmentDate?: string;
  rollNumber?: string;
  isActive: boolean;
  remarks?: string;
}
