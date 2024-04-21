import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from "@angular/router";
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../infrastructure/material/material.module';
import { NavBarComponent } from "./nav-bar/nav-bar.component";
import { ShorterFooterComponent } from './shorter-footer/shorter-footer.component';
import { CertificatesComponent } from './certificate/certificates/certificates.component';
import { RequestsComponent } from './request/requests/requests.component';

import { MatDialogModule } from '@angular/material/dialog';
import { MatButtonModule } from '@angular/material/button';
import { CreateRequestComponent } from './certificate/create-request/create-request.component';


@NgModule({
  declarations: [
    CreateRequestComponent,
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
    MatDialogModule,
    MatButtonModule    
  ]
})
export class LayoutModule { }
