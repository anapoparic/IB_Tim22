import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../infrastructure/material/material.module';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { SharedModule } from '../shared/shared.module';
import { LayoutModule } from '../layout/layout.module';

import { ReservationRequestsComponent } from './reservation-requests/reservation-requests.component';
import { CreateReservationComponent } from './create-reservation/create-reservation.component';
import { ReservationsComponent } from './reservations/reservations.component';
import { ReservationDetailsComponent } from './reservation-details/reservation-details.component';

@NgModule({
  declarations: [
    ReservationRequestsComponent,
    CreateReservationComponent,
    ReservationsComponent,
    ReservationDetailsComponent,
  ],
  imports: [
    CommonModule,
    MaterialModule,
    RouterModule,
    ReactiveFormsModule,
    HttpClientModule,
    SharedModule,
    LayoutModule,
  ],
  exports:[
    //if needed add here components
  ]
})
export class ReservationModule { }
