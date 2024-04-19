import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {RouterModule} from "@angular/router";
import { LoginComponent } from './login/login.component';
import { RegistrationComponent } from './registration/registration.component';
@NgModule({
  declarations: [
    LoginComponent,
    RegistrationComponent
  ],
  imports: [
    CommonModule,
    ReactiveFormsModule,
    RouterModule,
    FormsModule
  ], 
  exports:[
    ReactiveFormsModule,
    FormsModule
  ]
})
export class AuthModule { }
