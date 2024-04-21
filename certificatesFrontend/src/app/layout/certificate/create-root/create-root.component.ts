import { Component, Inject, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { AuthResponse } from 'src/app/infrastructure/auth/model/auth-response';
import { Login } from 'src/app/infrastructure/auth/model/login';
import { Role } from 'src/app/user/model/role.enum';
import { User } from 'src/app/user/model/user.model';
import { UserService } from 'src/app/user/user.service';
import Swal from 'sweetalert2';
import { CertificatesService } from '../certificates.service';
import { CertificateRequest } from '../../request/model/certificateRequest.model';
import { RequestsService } from '../../request/requests.service';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Template } from '../model/enum/template.enum';
import { Certificate } from '../model/certificate.model';

@Component({
  selector: 'app-create-root',
  templateUrl: './create-root.component.html',
  styleUrls: ['./create-root.component.css']
})
export class CreateRootComponent implements OnInit{

  certificationRootForm: FormGroup | undefined;

  constructor(public dialogRef: MatDialogRef<CreateRootComponent>, private authService: AuthService, private router: Router, private userService: UserService,private formBuilder: FormBuilder, private requestService: RequestsService, private  certificationService: CertificatesService) {
    this.certificationRootForm =  this.formBuilder.group({
      alias: [{value: '', disabled: true}],
      issuer_alias: [{value: '', disabled: true}],
      template: [{value: '', disabled: true}],
      commonName:  ['', Validators.required],
      organization:  ['', Validators.required],
      unit:  ['', Validators.required],
      email:  [{value: '', disabled: true}],
      country:  [{value: '', disabled: true}],
      uid: [{value: '', disabled: true}]
    });
  }

  loggedUser!: User;

  ngOnInit() {
    this.userService.getUser(this.authService.getUserID()).subscribe(
        (user: User) => {
          this.loggedUser = user;
          

          this.certificationRootForm!.setValue({
            alias: this.generateRootAlias(),
            issuer_alias: this.generateRootAlias(),
            template: Template.ROOT,
            commonName: '',
            organization: '',
            unit: '',
            email: user.email,
            country: user.address?.country,
            uid: this.requestService.generateUniqueUID()
          });
        },
        (error) => {
          console.error('Error loading user:', error);
        }
    );
  }

  closeDialog(): void {
    this.dialogRef.close();
  }
  

  createRoot(){
    const formValues = this.certificationRootForm?.value;
    if (this.checkForEmptyValues(formValues)) {
      const certificate: Certificate = {

        validFrom: undefined,
        validTo: undefined,
        alias: this.certificationRootForm?.controls['alias'].value || '',
        issuerAlias: this.certificationRootForm?.controls['issuer_alias'].value || '',
        isRevoked: false,
        reason: undefined,
        template: Template.ROOT,
        commonName: this.certificationRootForm?.controls['commonName'].value || '',
        organization:  this.certificationRootForm?.controls['organization'].value || '',
        organizationUnit:  this.certificationRootForm?.controls['unit'].value || '',
        country: this.certificationRootForm?.controls['country'].value || '',
        ownerEmail:  this.certificationRootForm?.controls['email'].value || ''
      };

      this.certificationService.createRootCertificate(certificate, this.authService.getUserID().toString()).subscribe({
        next: (createdCertificate: Certificate) => {
          Swal.fire('Success', 'Successfully created!', 'success');
          this.router.navigate(['/certificates']);
          this.closeDialog();
        },
        error: (error) => {
          Swal.fire('Error', 'Error', 'error'); 
        }
      });
    }
  }

  generateRootAlias(): string {
    const uniqueNumber = Date.now().toString();
    const prefix = 'root';
    const alias = prefix + uniqueNumber;
    return alias;
}

  checkForEmptyValues(formValues: any): boolean {
    return Object.values(formValues).every(value => {
      if (typeof value === 'string') {
        return value.trim() !== '';
      } else if (typeof value === 'number') {
        return value !== 0;
      } else {
        return true;
      }
    });
  }

}
