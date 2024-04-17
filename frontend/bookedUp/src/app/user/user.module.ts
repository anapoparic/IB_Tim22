import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { MaterialModule } from '../infrastructure/material/material.module';
import { RouterModule } from '@angular/router';
import { ReactiveFormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { SharedModule } from '../shared/shared.module';
import { LayoutModule } from '../layout/layout.module';

import { ManageProfileComponent } from './manage-profile/manage-profile.component';
import { UserReportsComponent } from './user-reports/user-reports.component';
import { BlockUsersComponent } from './block-users/block-users.component';
import { HostReportsComponent } from './host-reports/host-reports.component';


@NgModule({
  declarations: [
    ManageProfileComponent,
    UserReportsComponent,
    BlockUsersComponent,
    HostReportsComponent,
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
export class UserModule { }
