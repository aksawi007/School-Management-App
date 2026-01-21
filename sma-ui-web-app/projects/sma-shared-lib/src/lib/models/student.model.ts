export interface Student {
  id?: string;
  schoolId: string;
  studentId: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  fullName?: string;
  dateOfBirth: string;
  gender: string;
  bloodGroup?: string;
  email?: string;
  phoneNumber?: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  admissionDate: string;
  admissionNumber: string;
  studentStatus: string;
  photoUrl?: string;
  aadharNumber?: string;
  isActive?: boolean;
}

export interface StudentRequest {
  schoolId: string;
  studentId: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  dateOfBirth: string;
  gender: string;
  bloodGroup?: string;
  email?: string;
  phoneNumber?: string;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  admissionDate: string;
  admissionNumber: string;
  studentStatus: string;
  photoUrl?: string;
  aadharNumber?: string;
}
