/**
 * SectionMaster Model
 * Represents a section within a class
 */
export interface SectionMaster {
  id?: string;
  schoolId: number;
  classId: string;
  className?: string;
  sectionCode: string;
  sectionName: string;
  capacity?: number;
  roomNumber?: string;
  description?: string;
}

/**
 * SectionMaster Request DTO
 */
export interface SectionMasterRequest {
  schoolId: number;
  classId: string;
  sectionCode: string;
  sectionName: string;
  capacity?: number;
  roomNumber?: string;
  description?: string;
}

/**
 * SectionMaster Response DTO
 */
export interface SectionMasterResponse extends SectionMaster {
  id: string;
  className?: string;
}
