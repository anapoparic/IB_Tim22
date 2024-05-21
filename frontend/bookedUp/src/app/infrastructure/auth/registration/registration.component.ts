import { Component } from '@angular/core';
import { AuthService } from '../auth.service';
import {Router} from "@angular/router";
import {FormBuilder, FormControl, FormGroup, Validators} from "@angular/forms";
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
  registrationForm: FormGroup;

  passwordValid: boolean = false;
  passwordsMatch: boolean = false;
  passwordPwned: boolean = false;

  constructor(private authService: AuthService, private router: Router, private fb: FormBuilder) {
    this.registrationForm = this.fb.group({
      email: ['', [Validators.required, Validators.email]],
      password: ['', [Validators.required, this.passwordValidator.bind(this)]],
      passwordAgain: ['', Validators.required],
      firstName: ['', Validators.required],
      lastName: ['', Validators.required],
      streetNumber: ['', Validators.required],
      city: ['', Validators.required],
      postalCode: ['', Validators.required],
      country: ['', Validators.required],
      phone: [0, Validators.required],
      role: [Role.Guest],
    });

    this.registrationForm.valueChanges.subscribe(() => {
      this.passwordValid = this.registrationForm.get('password')?.valid || false;
      this.passwordsMatch = this.registrationForm.get('password')?.value === this.registrationForm.get('passwordAgain')?.value && this.registrationForm.get('passwordAgain')?.value !== '';
    });
  }

  passwordValidator(control: FormControl) {
    const value = control.value;
    if (!value) return null;

    const hasUpperCase = /[A-Z]/.test(value);
    const hasLowerCase = /[a-z]/.test(value);
    const hasNumber = /[0-9]/.test(value);
    const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(value);
    const validLength = value.length >= 8;

    const passwordValid = hasUpperCase && hasLowerCase && hasNumber && hasSpecialChar && validLength;

    if (!passwordValid) {
      return { invalidPassword: true };
    }

    this.authService.checkPassword(value).subscribe(pwned => {
      this.passwordPwned = pwned;
      if (pwned) {
        control.setErrors({ pwnedPassword: true });
      }
    });

    return null;
  }

  toggleRole() {
    const currentRole = this.registrationForm.get('role')?.value;
    this.registrationForm.get('role')?.setValue(currentRole === Role.Host ? Role.Guest : Role.Host);
  }

  register() {
    const formValues = this.registrationForm.value;

    if (this.registrationForm.valid && this.checkForEmptyValues(formValues) && !this.passwordPwned) {
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

}
