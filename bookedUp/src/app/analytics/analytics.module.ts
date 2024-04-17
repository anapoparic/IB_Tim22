import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';

import { NgChartsModule } from 'ng2-charts';
import { MaterialModule } from '../infrastructure/material/material.module';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { SharedModule } from '../shared/shared.module';
import { LayoutModule } from '../layout/layout.module';

import { AnalyticsComponent } from './analytics/analytics.component';
import { SingleAccommodationAnalyticsComponent } from './single-accommodation-analytics/single-accommodation-analytics.component';
import { YearlyAnalyticsComponent } from './yearly-analytics/yearly-analytics.component';



@NgModule({
  declarations: [
    AnalyticsComponent,
    SingleAccommodationAnalyticsComponent,
    YearlyAnalyticsComponent
  ],
  imports: [
    CommonModule,
    MaterialModule,
    RouterModule,
    ReactiveFormsModule,
    HttpClientModule,
    SharedModule,
    LayoutModule,
    NgChartsModule,
  ],
  exports:[
    AnalyticsComponent,
  ]
})
export class AnalyticsModule { }
