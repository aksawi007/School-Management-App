export interface Enrollment {
  id?: string;
  academicYearId: string;
  classId: string;
  sectionId: string;
  rollNumber?: string;
  enrollmentDate: string;
  enrollmentStatus: string;
  isActive?: boolean;
  className?: string;
  sectionName?: string;
  academicYear?: string;
}

export interface EnrollmentRequest {
  academicYearId: string;
  classId: string;
  sectionId: string;
  rollNumber?: string;
  enrollmentDate: string;
  enrollmentStatus: string;
}

export interface PromoteRequest {
  targetAcademicYearId: string;
  targetClassId: string;
  targetSectionId: string;
  promotionDate: string;
}

export interface WithdrawRequest {
  withdrawalDate: string;
  withdrawalReason: string;
}
