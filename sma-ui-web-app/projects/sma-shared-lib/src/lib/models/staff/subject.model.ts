/**
 * Subject Model
 * Represents a subject taught in the school
 */
export interface Subject {
  id?: number;
  subjectName: string;
  subjectCode: string;
  description?: string;
  credits?: number;
}

/**
 * Subject Request DTO
 */
export interface SubjectRequest {
  subjectName: string;
  subjectCode: string;
  description?: string;
  credits?: number;
}

/**
 * Subject Response DTO
 */
export interface SubjectResponse extends Subject {
  id: number;
}

/**
 * Staff Subject Assignment Model
 */
export interface StaffSubjectAssignment {
  id?: number;
  staffId: number;
  subjectId: number;
  classId?: number;
  sectionId?: number;
  assignedDate?: string;
}
