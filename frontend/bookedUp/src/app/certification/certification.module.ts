import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LayoutModule } from '../layout/layout.module';
import { RouterModule } from '@angular/router';
import { MaterialModule } from '../infrastructure/material/material.module';
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../shared/shared.module';



@NgModule({
  declarations: [],
  imports: [
    CommonModule,
    LayoutModule,
    RouterModule,
    MaterialModule,
    ReactiveFormsModule,
    SharedModule,
  ], 
  exports: [
  ]
})
export class CertificationModule { }
