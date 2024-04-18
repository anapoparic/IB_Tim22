import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from "@angular/router";
import { ReactiveFormsModule } from '@angular/forms';
import { MaterialModule } from '../infrastructure/material/material.module';
import { AdminNavBarComponent } from "./nav-bar/admin-nav-bar/admin-nav-bar.component";
import { NavBarComponent } from "./nav-bar/nav-bar.component";
import { ShorterFooterComponent } from './shorter-footer/shorter-footer.component';
import { IndexComponent } from './index/index.component';


@NgModule({
  declarations: [
    NavBarComponent,
    ShorterFooterComponent,
    AdminNavBarComponent,
    IndexComponent,
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
