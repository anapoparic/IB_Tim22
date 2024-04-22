import { User } from './user.model';

export interface Host extends User {

  averageRating?: number;
  reservationCreatedNotificationEnabled?: boolean;
  cancellationNotificationEnabled?: boolean;
  hostRatingNotificationEnabled?: boolean;
  accommodationRatingNotificationEnabled?: boolean;
}
