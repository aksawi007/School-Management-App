export interface Address {
  id?: number;
  addressType: string; // CURRENT, PERMANENT
  line1: string;
  line2?: string;
  city: string;
  state: string;
  pincode: string;
  country: string;
  landmark?: string;
}

export interface AddressRequest {
  addressType: string; // CURRENT, PERMANENT
  line1: string;
  line2?: string;
  city: string;
  state: string;
  pincode: string;
  country: string;
  landmark?: string;
}
