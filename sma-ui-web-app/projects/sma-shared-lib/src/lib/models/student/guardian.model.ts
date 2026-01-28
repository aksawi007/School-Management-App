export interface Guardian {
  id?: number;
  relation: string; // FATHER, MOTHER, GUARDIAN, OTHER
  name: string;
  phone: string;
  alternatePhone?: string;
  email?: string;
  occupation?: string;
  annualIncome?: string;
  education?: string;
  isPrimary: boolean;
  aadharNo?: string;
  panNo?: string;
  photoUrl?: string;
}

export interface GuardianRequest {
  relation: string; // FATHER, MOTHER, GUARDIAN, OTHER
  name: string;
  phone: string;
  alternatePhone?: string;
  email?: string;
  occupation?: string;
  annualIncome?: string;
  education?: string;
  isPrimary?: boolean;
  aadharNo?: string;
  panNo?: string;
  photoUrl?: string;
}
