import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import {Router} from "@angular/router";
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {Role} from "../../../user/model/role.enum";
import {User} from "../../../user/model/user.model";
import Swal from "sweetalert2";


@Component({
  selector: 'app-registration',
  templateUrl: './registration.component.html',
  styleUrls: ['./registration.component.css']
})
export class RegistrationComponent {
  showHostText = false;

  constructor(private authService: AuthService, private router: Router) {
  }

  registrationForm = new FormGroup({
    email: new FormControl('', Validators.required),
    firstName: new FormControl('', Validators.required),
    lastName: new FormControl('', Validators.required),
    streetNumber: new FormControl('', Validators.required),
    city: new FormControl('', Validators.required),
    postalCode: new FormControl('', Validators.required),
    country: new FormControl('', Validators.required),
    phone: new FormControl(0, Validators.required),
    role: new FormControl(Role.Guest),
    password: new FormControl('', Validators.required),
    passwordAgain: new FormControl('', Validators.required),

  });

  toggleRole() {
    const currentRole = this.registrationForm.get('role')?.value;
    this.registrationForm.get('role')?.setValue(currentRole === Role.Host ? Role.Guest : Role.Host);
  }

  register() {
    const formValues = this.registrationForm.value;

    if (this.registrationForm.valid && this.checkForEmptyValues(formValues) && this.checkPasswordsMatch(formValues)) {
      const user: User = {
        firstName: formValues.firstName || '',
        lastName: formValues.lastName || '',
        address: {
          country: formValues.country || '',
          city: formValues.city || '',
          postalCode: formValues.postalCode || '',
          streetAndNumber: formValues.streetNumber || '',
          latitude: 0,
          longitude: 0
        },
        phone: formValues.phone || 0,
        email: formValues.email || '',
        password: formValues.password || '',
        role: formValues.role || Role.Guest,

        profilePicture:{
          url:"images/usx.jpg",
          active:true
        }
      };

      this.authService.register(user).subscribe({
        next: (registeredUser: User) => {
          Swal.fire('Success', 'Successfully registered user!', 'success');
          const emailValue = this.registrationForm.get('email')?.value;
          this.router.navigate(['/check-inbox', { email: emailValue }]);

        },
        error: (error) => {
          Swal.fire('Info', 'Your request has been successfully created,but you must activate account.', 'success');
          const emailValue = this.registrationForm.get('email')?.value;
          this.router.navigate(['/check-inbox', { email: emailValue }]);

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

  checkPasswordsMatch(formValues: any): boolean {
    const password = formValues.password;
    const passwordAgain = formValues.passwordAgain;
    return password === passwordAgain;
  }
}
