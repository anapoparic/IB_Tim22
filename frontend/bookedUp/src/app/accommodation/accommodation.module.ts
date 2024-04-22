import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../infrastructure/material/material.module';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { SharedModule } from '../shared/shared.module';
import { LayoutModule } from '../layout/layout.module';

import { AccommodationDetailsComponent } from './accommodation-details/accommodation-details.component';
import { AccommodationRequestsComponent } from './accommodation-requests/accommodation-requests.component';
import { AccommodationsComponent } from './accommodations/accommodations.component';
import { CreateAccommodationComponent } from './create-accommodation/create-accommodation.component';
import { UpdateAccommodationComponent } from './update-accommodation/update-accommodation.component';
import { FavouritesComponent } from './favourites/favourites.component';

@NgModule({
  declarations: [
    AccommodationDetailsComponent,
    AccommodationRequestsComponent,
    AccommodationsComponent,
    CreateAccommodationComponent,
    UpdateAccommodationComponent,
    FavouritesComponent,
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
export class AccommodationModule { }
