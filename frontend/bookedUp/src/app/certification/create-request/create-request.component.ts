import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { AuthResponse } from 'src/app/infrastructure/auth/model/auth-response';
import { Login } from 'src/app/infrastructure/auth/model/login';
import { Role } from 'src/app/user/model/role.enum';
import { User } from 'src/app/user/model/user.model';
import { UserService } from 'src/app/user/user.service';
import Swal from 'sweetalert2';
import { CertificateRequest } from '../model/certificateRequest.model';
import { CertificationService } from '../certification.service';

@Component({
  selector: 'app-create-request',
  templateUrl: './create-request.component.html',
  styleUrls: ['./create-request.component.css']
})
export class CreateRequestComponent implements OnInit{


  certificationForm: FormGroup | undefined;

  constructor(private authService: AuthService, private router: Router, private userService: UserService,private formBuilder: FormBuilder, private  certificationService: CertificationService) {
    this.certificationForm =  this.formBuilder.group({
      commonName:  ['', Validators.required],
      uid:  [{value: '', disabled: true}],
      organization:  ['', Validators.required],
      unit:  ['', Validators.required],
      firstName:  [{value: '', disabled: true}],
      lastName:  [{value: '', disabled: true}],
      country:  [{value: '', disabled: true}],
      email:  [{value: '', disabled: true}]
    });
  }

  loggedUser!: User;

  ngOnInit() {
    this.userService.getUser(this.authService.getUserID()).subscribe(
        (user: User) => {
          this.loggedUser = user;

          this.certificationForm!.setValue({
            commonName: '',
            uid: this.certificationService.generateUniqueUID(),
            organization: '',
            unit: '',
            firstName: user.firstName,
            lastName: user.lastName,
            country: user.address?.country,
            email: user.email,
          });
        },
        (error) => {
          console.error('Error loading user:', error);
        }
    );
  }

  sendRequest() {
    const formValues = this.certificationForm?.value;

    if (this.checkForEmptyValues(formValues)) {
      const request: CertificateRequest = {
        commonName: formValues.commonName || '',
        firstName: this.certificationForm?.controls['firstName'].value || '',
        lastName: this.certificationForm?.controls['lastName'].value || '',
        organization: formValues.organization || '',
        unit: formValues.unit || '',
        country: this.certificationForm?.controls['country'].value || '',
        email: this.certificationForm?.controls['email'].value || '',
        uid: this.certificationForm?.controls['uid'].value || ''
      };

      this.certificationService.createRequest(request).subscribe({
        next: (createdRequest: CertificateRequest) => {
          Swal.fire('Success', 'Successfully created!', 'success');
          this.router.navigate(['/']);
        },
        error: (error) => {
          if (error.status === 400 && error.error === 'You have already sent a request with this email.') {
            Swal.fire('Error', 'You have already sent a request with this email.', 'error');
          } else {
            Swal.fire('Error', 'You have already sent a request with this email.', 'error');
          }
        }
      });
    } else {
      Swal.fire('Error', 'Some fields are empty or have invalid values.', 'error');
    }
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

  downloadCertificate() {
    throw new Error('Method not implemented.');
  }

}
