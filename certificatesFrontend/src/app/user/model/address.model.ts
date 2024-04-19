// address.dto.ts

export interface Address {
  id?: number;
  country: string;
  city: string;
  postalCode: string;
  streetAndNumber: string;
  active?: boolean
  latitude: number;
  longitude: number;
}
