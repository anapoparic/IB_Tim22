import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { MAT_DIALOG_DATA, MatDialogRef } from '@angular/material/dialog';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { UserService } from 'src/app/user/user.service';
import { RequestsService } from '../requests.service';
import { CertificatesService } from '../../certificate/certificates.service';
import { User } from 'src/app/user/model/user.model';
import { Template } from '../../certificate/model/enum/template.enum';
import { Certificate } from '../../certificate/model/certificate.model';
import Swal from 'sweetalert2';
import { CertificateRequest } from '../model/certificateRequest.model';
import { Subscription } from 'rxjs';

@Component({
  selector: 'app-accept-request',
  templateUrl: './accept-request.component.html',
  styleUrls: ['./accept-request.component.css']
})
export class AcceptRequestComponent implements OnInit{

  acceptForm: FormGroup | undefined;
  loggedUser!: User;
  request: CertificateRequest | undefined;
  alias: string | undefined;

  constructor(public dialogRef: MatDialogRef<AcceptRequestComponent>, 
    @Inject(MAT_DIALOG_DATA) public data: any, 
    private authService: AuthService, 
    private router: Router, 
    private userService: UserService,
    private formBuilder: FormBuilder, 
    private requestService: RequestsService, 
    private certificationService: CertificatesService) {
    this.acceptForm =  this.formBuilder.group({
      alias: [{value: '', disabled: true}],
      issuer_alias: [{value: '', disabled: true}],
      template: [{value: '', disabled: true}]
    });
  }

  ngOnInit() {

    const commonName = 'Booking';
    this.certificationService.getAliasByCommonName(commonName).subscribe(
      (certificate: Certificate) => {
        this.alias = certificate.alias;
      },
      (error) => {
        console.error(`Error fetching alias for commonName '${commonName}':`, error);
        this.closeDialog();
        Swal.fire('Error', 'There is no certificate available to sign your request.', 'error');
      }
    );

    this.data.subscribe(
      (request: CertificateRequest) => {
        this.request = request;
      }
    );

    this.userService.getUser(this.authService.getUserID()).subscribe(
        (user: User) => {
          this.loggedUser = user;
          
          // const generatedAlias = this.certificationService.generateAlias('endEntity');
          const generatedAlias = this.request?.alias;

          this.acceptForm!.setValue({
            alias: generatedAlias,
            issuer_alias: this.alias,
            template: Template.END_ENTITY,
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
  

  acceptRequest(){
    const formValues = this.acceptForm?.value;
    const certificate: Certificate = {
      alias: this.acceptForm?.controls['alias'].value || '',
      issuerAlias: this.acceptForm?.controls['issuer_alias'].value || '',
      revoked: false,
      template: Template.END_ENTITY,
      commonName: this.request?.commonName || '',
      organization: this.request?.organization || '',
      organizationUnit: this.request?.unit || '',
      country: this.request?.country || '',
      ownerEmail:  this.request?.email || ''
    };

    this.certificationService.createCertificate(this.request!, certificate.alias, certificate.issuerAlias, certificate.template.toString()).subscribe({
      next: (createdCertificate: Certificate) => {
        Swal.fire({
          icon: 'success',
          title: 'Certificate Request Accepted',
          text: `Certificate request with ID ${this.request!.id} has been successfully accepted.`,
        });
        this.router.navigate(['/certificates']);
        this.closeDialog();
      },
      error: (error) => {
        Swal.fire('Error', 'Error', 'error'); 
      }
    });
  }
  
}
