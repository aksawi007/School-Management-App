/**
 * ClassMaster Model
 * Represents a class/grade level
 */
export interface ClassMaster {
  id?: string;
  schoolId: number;
  academicYearId: number;
  academicYearName?: string;
  classCode: string;
  className: string;
  displayOrder?: number;
  description?: string;
}

/**
 * ClassMaster Request DTO
 */
export interface ClassMasterRequest {
  schoolId: number;
  academicYearId: number;
  classCode: string;
  className: string;
  displayOrder?: number;
  description?: string;
}

/**
 * ClassMaster Response DTO
 */
export interface ClassMasterResponse extends ClassMaster {
  id: string;
}
