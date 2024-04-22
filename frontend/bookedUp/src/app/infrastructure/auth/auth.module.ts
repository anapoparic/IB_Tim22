import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginComponent } from './login/login.component';
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import { RegistrationComponent } from './registration/registration.component';
import {RouterModule} from "@angular/router";

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
