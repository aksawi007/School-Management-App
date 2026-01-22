export interface Guardian {
  id?: string;
  guardianType: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  fullName?: string;
  relationship: string;
  phoneNumber: string;
  email?: string;
  occupation?: string;
  annualIncome?: number;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  isPrimaryContact: boolean;
  aadharNumber?: string;
}

export interface GuardianRequest {
  guardianType: string;
  firstName: string;
  middleName?: string;
  lastName: string;
  relationship: string;
  phoneNumber: string;
  email?: string;
  occupation?: string;
  annualIncome?: number;
  addressLine1: string;
  addressLine2?: string;
  city: string;
  state: string;
  postalCode: string;
  isPrimaryContact: boolean;
  aadharNumber?: string;
}
