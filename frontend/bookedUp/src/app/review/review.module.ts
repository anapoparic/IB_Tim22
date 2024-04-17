import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../infrastructure/material/material.module';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { SharedModule } from '../shared/shared.module';
import { LayoutModule } from '../layout/layout.module';

import { AccommodationReviewsComponent } from './accommodation-reviews/accommodation-reviews.component';
import { AddReviewComponent } from './add-review/add-review.component';
import { GuestReviewsComponent } from './guest-reviews/guest-reviews.component';
import { HostReviewsComponent } from './host-reviews/host-reviews.component';
import { ReviewReportsComponent } from './review-reports/review-reports.component';
import { AccommodationHostReviewComponent } from './accommodation-host-review/accommodation-host-review.component';


@NgModule({
  declarations: [
    AccommodationReviewsComponent,
    AddReviewComponent,
    GuestReviewsComponent,
    HostReviewsComponent,
    ReviewReportsComponent,
    AccommodationHostReviewComponent,
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
export class ReviewModule { }
