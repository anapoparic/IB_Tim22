import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterModule } from "@angular/router";
import { ReactiveFormsModule } from '@angular/forms';
import { SharedModule } from '../shared/shared.module';
import { MaterialModule } from '../infrastructure/material/material.module';
import { AdminNavBarComponent } from "./nav-bar/admin-nav-bar/admin-nav-bar.component";
import { NavBarComponent } from "./nav-bar/nav-bar.component";
import { GuestNavBarComponent } from "./nav-bar/guest-nav-bar/guest-nav-bar.component";
import { HostNavBarComponent } from "./nav-bar/host-nav-bar/host-nav-bar.component";
import { UnloggedNavBarComponent } from './nav-bar/unlogged-nav-bar/unlogged-nav-bar.component';
import { FooterComponent } from './footer/footer.component';
import { ShorterFooterComponent } from './shorter-footer/shorter-footer.component';
import { IndexComponent } from './index/index.component';
import { SearchComponent } from './search/search.component';
import { CheckInboxComponent } from './check-inbox/check-inbox.component';
import { ForgotPasswordComponent } from './forgot-password/forgot-password.component';


@NgModule({
  declarations: [
    NavBarComponent,
    AdminNavBarComponent,
    GuestNavBarComponent,
    HostNavBarComponent,
    UnloggedNavBarComponent,
    FooterComponent,
    ShorterFooterComponent,
    IndexComponent,
    SearchComponent,
    CheckInboxComponent, 
    ForgotPasswordComponent,
  ],
  exports: [
    NavBarComponent,
    FooterComponent,
    ShorterFooterComponent,
    
  ],
  imports: [
    CommonModule,
    RouterModule,
    MaterialModule,
    ReactiveFormsModule,
    SharedModule,
  ]
})
export class LayoutModule { }
