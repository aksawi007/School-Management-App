/**
 * Department Model
 * Represents a department in the school
 */
export interface Department {
  id?: number;
  departmentName: string;
  departmentCode: string;
  headOfDepartmentId?: number;
  description?: string;
}

/**
 * Department Request DTO
 */
export interface DepartmentRequest {
  departmentName: string;
  departmentCode: string;
  headOfDepartmentId?: number;
  description?: string;
}

/**
 * Department Response DTO
 */
export interface DepartmentResponse extends Department {
  id: number;
}
