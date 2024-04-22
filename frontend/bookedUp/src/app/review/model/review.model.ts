import { Guest } from 'src/app/user/model/guest.model';
import { ReviewType } from './enum/reviewType.enum';
import { Accommodation } from 'src/app/accommodation/model/accommodation.model';
import { Host } from 'src/app/user/model/host.model';

export interface Review {
    id?: number;
    guest?: Guest;
    review?: number;
    comment?: string;
    date?: Date;
    host?: Host;
    accommodation?: Accommodation;
    type?: ReviewType;
    approved?: boolean;
}
