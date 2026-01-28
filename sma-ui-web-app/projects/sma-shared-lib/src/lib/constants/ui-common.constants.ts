/**
 * UI Common Constants
 * Reusable constants for UI components across all apps
 */

/**
 * Blood Group Options
 * Standard blood groups for student/staff information
 */
export const BLOOD_GROUPS = [
  'A+',
  'A-',
  'B+',
  'B-',
  'AB+',
  'AB-',
  'O+',
  'O-'
] as const;

export type BloodGroup = typeof BLOOD_GROUPS[number];

/**
 * Religion Options
 */
export const RELIGIONS = [
  'Hindu',
  'Muslim',
  'Christian',
  'Sikh',
  'Buddhist',
  'Jain',
  'Other'
] as const;

export type Religion = typeof RELIGIONS[number];

/**
 * Caste Categories
 */
export const CASTE_CATEGORIES = [
  'General',
  'OBC',
  'SC',
  'ST',
  'Other'
] as const;

export type CasteCategory = typeof CASTE_CATEGORIES[number];

/**
 * Guardian Relation Types
 */
export const GUARDIAN_RELATIONS = [
  'FATHER',
  'MOTHER',
  'GUARDIAN',
  'OTHER'
] as const;

export type GuardianRelation = typeof GUARDIAN_RELATIONS[number];

/**
 * Address Types
 */
export const ADDRESS_TYPES = [
  'CURRENT',
  'PERMANENT'
] as const;

export type AddressType = typeof ADDRESS_TYPES[number];

/**
 * Nationality Options
 */
export const NATIONALITIES = [
  'Indian',
  'Other'
] as const;

export type Nationality = typeof NATIONALITIES[number];

/**
 * Indian States
 * List of Indian states and union territories
 */
export const INDIAN_STATES = [
  'Andhra Pradesh',
  'Arunachal Pradesh',
  'Assam',
  'Bihar',
  'Chhattisgarh',
  'Goa',
  'Gujarat',
  'Haryana',
  'Himachal Pradesh',
  'Jharkhand',
  'Karnataka',
  'Kerala',
  'Madhya Pradesh',
  'Maharashtra',
  'Manipur',
  'Meghalaya',
  'Mizoram',
  'Nagaland',
  'Odisha',
  'Punjab',
  'Rajasthan',
  'Sikkim',
  'Tamil Nadu',
  'Telangana',
  'Tripura',
  'Uttar Pradesh',
  'Uttarakhand',
  'West Bengal',
  'Andaman and Nicobar Islands',
  'Chandigarh',
  'Dadra and Nagar Haveli and Daman and Diu',
  'Delhi',
  'Jammu and Kashmir',
  'Ladakh',
  'Lakshadweep',
  'Puducherry'
] as const;

export type IndianState = typeof INDIAN_STATES[number];
