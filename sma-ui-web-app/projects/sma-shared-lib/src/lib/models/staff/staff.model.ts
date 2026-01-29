/**
 * Staff Type Enum
 */
export enum StaffType {
  TEACHING = 'TEACHING',
  NON_TEACHING = 'NON_TEACHING',
  ADMINISTRATIVE = 'ADMINISTRATIVE'
}

/**
 * Employment Type Enum
 */
export enum EmploymentType {
  PERMANENT = 'PERMANENT',
  CONTRACT = 'CONTRACT',
  TEMPORARY = 'TEMPORARY',
  PROBATION = 'PROBATION'
}

/**
 * Staff Status Enum
 */
export enum StaffStatus {
  ACTIVE = 'ACTIVE',
  INACTIVE = 'INACTIVE',
  ON_LEAVE = 'ON_LEAVE',
  TERMINATED = 'TERMINATED',
  RETIRED = 'RETIRED'
}

/**
 * Staff Model
 * Represents a staff member - matches backend StaffRequestDTO
 */
export interface Staff {
  id?: number;
  schoolId: number;
  employeeCode?: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  dateOfBirth?: string;
  gender?: string;
  bloodGroup?: string;
  email: string;
  phoneNumber: string;
  addressLine1?: string;
  addressLine2?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  staffType: string;
  designation?: string;
  departmentIds?: number[];
  qualification?: string;
  specialization?: string;
  experienceYears?: number;
  joiningDate?: string;
  employmentType?: string;
  salary?: number;
  staffStatus?: string;
  photoUrl?: string;
  aadharNumber?: string;
  panNumber?: string;
  bankAccountNumber?: string;
  bankName?: string;
  bankIfscCode?: string;
}

/**
 * Staff Request DTO
 */
export interface StaffRequest {
  schoolId: number;
  employeeCode?: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  dateOfBirth?: string;
  gender?: string;
  bloodGroup?: string;
  email: string;
  phoneNumber: string;
  addressLine1?: string;
  addressLine2?: string;
  city?: string;
  state?: string;
  postalCode?: string;
  staffType: string;
  designation?: string;
  departmentIds?: number[];
  qualification?: string;
  specialization?: string;
  experienceYears?: number;
  joiningDate?: string;
  employmentType?: string;
  salary?: number;
  staffStatus?: string;
  photoUrl?: string;
  aadharNumber?: string;
  panNumber?: string;
  bankAccountNumber?: string;
  bankName?: string;
  bankIfscCode?: string;
}

/**
 * Staff Response DTO
 */
export interface StaffResponse extends Staff {
  id: number;
}
