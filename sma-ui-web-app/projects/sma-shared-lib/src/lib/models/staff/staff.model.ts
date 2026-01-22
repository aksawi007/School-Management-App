/**
 * Staff Type Enum
 */
export enum StaffType {
  TEACHING = 'TEACHING',
  NON_TEACHING = 'NON_TEACHING',
  ADMINISTRATIVE = 'ADMINISTRATIVE'
}

/**
 * Staff Model
 * Represents a staff member
 */
export interface Staff {
  id?: number;
  schoolId: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  dateOfBirth?: string;
  gender?: string;
  address?: string;
  city?: string;
  state?: string;
  country?: string;
  postalCode?: string;
  staffType: StaffType;
  departmentId?: number;
  designation?: string;
  qualifications?: string;
  joiningDate?: string;
  salary?: number;
  employeeCode?: string;
  status?: string;
}

/**
 * Staff Request DTO
 */
export interface StaffRequest {
  schoolId: number;
  firstName: string;
  lastName: string;
  email: string;
  phoneNumber: string;
  dateOfBirth?: string;
  gender?: string;
  address?: string;
  city?: string;
  state?: string;
  country?: string;
  postalCode?: string;
  staffType: StaffType;
  departmentId?: number;
  designation?: string;
  qualifications?: string;
  joiningDate?: string;
  salary?: number;
  employeeCode?: string;
}

/**
 * Staff Response DTO
 */
export interface StaffResponse extends Staff {
  id: number;
}
