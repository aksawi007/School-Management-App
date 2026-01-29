/**
 * Department Model
 * Represents a department in the school
 */
export interface Department {
  departmentId?: number;
  schoolId: number;
  departmentCode: string;
  departmentName: string;
  departmentType: string;
  hodStaffId?: number;
  hodEmployeeCode?: string;
  hodFullName?: string;
  hodEmail?: string;
  hodPhone?: string;
  description?: string;
}

/**
 * Department Request DTO
 */
export interface DepartmentRequest {
  schoolId: number;
  departmentCode: string;
  departmentName: string;
  departmentType: string;
  hodStaffId?: number;
  description?: string;
}

/**
 * Department Response DTO
 */
export interface DepartmentResponse {
  departmentId: number;
  schoolId: number;
  departmentCode: string;
  departmentName: string;
  departmentType: string;
  hodStaffId?: number;
  hodEmployeeCode?: string;
  hodFullName?: string;
  hodEmail?: string;
  hodPhone?: string;
  description?: string;
}
