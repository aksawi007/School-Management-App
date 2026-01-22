/**
 * School Profile Model
 * Represents the complete school information
 */
export interface SchoolProfile {
  schoolId?: number;
  schoolName: string;
  schoolCode: string;
  address: string;
  city: string;
  state: string;
  country: string;
  pincode: string;
  phone: string;
  email: string;
  website?: string;
  principalName: string;
  establishedYear?: string;
  affiliationNumber?: string;
  board: string;
}

/**
 * School Profile Request
 * Used for creating/updating school profiles
 */
export interface SchoolProfileRequest {
  schoolName: string;
  schoolCode: string;
  address: string;
  city: string;
  state: string;
  country: string;
  pincode: string;
  phone: string;
  email: string;
  website?: string;
  principalName: string;
  establishedYear?: string;
  affiliationNumber?: string;
  board: string;
}

/**
 * School Profile Response
 * Response structure from backend
 */
export interface SchoolProfileResponse extends SchoolProfile {
  schoolId: number;
}
