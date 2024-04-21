import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { LoginComponent } from './infrastructure/auth/login/login.component';
import { RequestsComponent } from './layout/request/requests/requests.component';
import { CertificatesComponent } from './layout/certificate/certificates/certificates.component';

const routes: Routes = [
  { path: '', component: LoginComponent },
  { path: 'requests', component: RequestsComponent },
  { path: 'certificates', component: CertificatesComponent },

];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
