import { GuardianRequest } from './guardian.model';
import { AddressRequest } from './address.model';

export interface Student {
  id?: number;
  schoolId: number;
  admissionNo: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  fullName?: string;
  dateOfBirth: string;
  gender: string;
  bloodGroup?: string;
  email?: string;
  phone?: string;
  religion?: string;
  caste?: string;
  nationality?: string;
  motherTongue?: string;
  aadharNo?: string;
  admissionDate: string;
  photoUrl?: string;
  remarks?: string;
  medicalConditions?: string;
  allergies?: string;
  status?: string;
  isActive?: boolean;
}

export interface StudentRequest {
  schoolId: number;
  admissionNo?: string; // Auto-generated if not provided
  firstName: string;
  middleName?: string;
  lastName: string;
  dateOfBirth: string;
  gender: string;
  phone?: string;
  email?: string;
  bloodGroup?: string;
  religion?: string;
  caste?: string;
  nationality?: string;
  motherTongue?: string;
  aadharNo?: string;
  admissionDate: string;
  photoUrl?: string;
  remarks?: string;
  medicalConditions?: string;
  allergies?: string;
  guardians?: GuardianRequest[];
  addresses?: AddressRequest[];
}

