import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from "@angular/router";
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../infrastructure/material/material.module';
import { NavBarComponent } from "./nav-bar/nav-bar.component";
import { ShorterFooterComponent } from './shorter-footer/shorter-footer.component';
import { CertificatesComponent } from './certificate/certificates/certificates.component';
import { RequestsComponent } from './request/requests/requests.component';


@NgModule({
  declarations: [
    NavBarComponent,
    ShorterFooterComponent,
    CertificatesComponent,
    RequestsComponent,
  ],
  exports: [
    NavBarComponent,
    ShorterFooterComponent,
  ],
  imports: [
    CommonModule,
    RouterModule,
    MaterialModule,
    ReactiveFormsModule,
  ]
})
export class LayoutModule { }
