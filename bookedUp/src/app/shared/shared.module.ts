import { NgModule } from '@angular/core';
import { MaterialModule } from '../infrastructure/material/material.module';
import { CommonModule } from '@angular/common';

import { MapComponent } from './map/map.component';
import { CalendarComponent } from './calendar/calendar.component';
import { ImageFrameComponent } from './image-frame/image-frame.component';
import { StarRatingComponent } from './star-rating/star-rating.component';
import { NotificationsComponent } from './notifications/notifications.component';


@NgModule({
  declarations: [
    MapComponent,
    CalendarComponent,
    ImageFrameComponent,
    StarRatingComponent,
    NotificationsComponent
  ],
  imports: [
    CommonModule,
    MaterialModule,
  ],
  exports: [
    MapComponent,
    CalendarComponent,
    ImageFrameComponent,
    StarRatingComponent,
    NotificationsComponent
  ]
})
export class SharedModule { }
