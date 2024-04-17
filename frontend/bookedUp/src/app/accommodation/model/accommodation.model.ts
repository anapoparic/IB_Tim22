import { Address } from '../../shared/model/address.model';
import { Amenity } from './enum/amenity.enum';
import { Photo } from '../../shared/model/photo.model';
import { AccommodationType } from './enum/accommodationType.enum';
import { DateRange } from './dateRange.model';
import { PriceType } from './enum/priceType.enum';
import { PriceChange } from './priceChange.model';
import { Host } from '../../user/model/host.model';
import { AccommodationStatus } from './enum/accommodationStatus.enum';

export interface Accommodation {
  id?: number;
  name: string;
  description: string;
  address: Address;
  amenities: Amenity[];
  photos: Photo[];
  minGuests: number;
  maxGuests: number;
  type: AccommodationType;
  availability: DateRange[];
  priceType: PriceType;
  priceChanges: PriceChange[];
  automaticReservationAcceptance: boolean;
  status: AccommodationStatus;
  host: Host;
  price: number;
  totalPrice?: number;
  averageRating?: number;
  cancellationDeadline: number;
}
