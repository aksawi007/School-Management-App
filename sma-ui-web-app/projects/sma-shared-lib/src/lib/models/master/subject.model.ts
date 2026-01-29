/**
 * Subject Type Enum
 */
export enum SubjectType {
  CORE = 'CORE',
  ELECTIVE = 'ELECTIVE',
  OPTIONAL = 'OPTIONAL',
  EXTRA_CURRICULAR = 'EXTRA_CURRICULAR'
}

/**
 * SubjectMaster Model
 * Represents a subject
 */
export interface SubjectMaster {
  id?: string;
  schoolId: number;
  classId: string;
  className?: string;
  subjectCode: string;
  subjectName: string;
  subjectType?: string;
  isMandatory?: boolean;
  credits?: number;
  maxMarks?: number;
  passMarks?: number;
  description?: string;
}

/**
 * SubjectMaster Request DTO
 */
export interface SubjectMasterRequest {
  schoolId: number;
  classId: string;
  subjectCode: string;
  subjectName: string;
  subjectType?: string;
  isMandatory?: boolean;
  credits?: number;
  maxMarks?: number;
  passMarks?: number;
  description?: string;
}

/**
 * SubjectMaster Response DTO
 */
export interface SubjectMasterResponse extends SubjectMaster {
  id: string;
  className?: string;
}
