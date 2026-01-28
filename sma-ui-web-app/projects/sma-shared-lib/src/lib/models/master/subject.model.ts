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
  id?: number;
  schoolId: number;
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
  id: number;
}
