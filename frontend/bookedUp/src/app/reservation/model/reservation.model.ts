// reservation.model.ts
import { Time } from '@angular/common';
import { ReservationStatus } from './reservationStatus.enum';
import { Accommodation } from '../../accommodation/model/accommodation.model';
import { Guest } from '../../user/model/guest.model';

export interface Reservation {
  id?: number;
  startDate: Date;
  endDate: Date;
  totalPrice: number;
  guestsNumber: number;
  accommodation: Accommodation;
  guest: Guest;
  status: ReservationStatus;
}
