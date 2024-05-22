import { Component, OnInit } from '@angular/core';
import {FormControl, FormGroup, Validators} from "@angular/forms";
import {AuthService} from "../auth.service";
import {Login} from "../model/login";
import {AuthResponse} from "../model/auth-response";
import {Router} from "@angular/router";
import Swal from 'sweetalert2';
import { WebSocketService } from 'src/app/shared/notifications/service/web-socket.service';
import { Role } from 'src/app/user/model/role.enum';
import { KeycloakService } from 'src/app/keycloak/keycloak.service';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit{
  isPasswordVisible: boolean = false;

  constructor(private authService: AuthService,
              private router: Router,
              private webSocketService: WebSocketService,
              private ss:KeycloakService) {
  }

  async ngOnInit(): Promise<void> {
    await this.ss.init();
    await this.ss.login();
  }

  // loginForm = new FormGroup({
  //   username: new FormControl('', Validators.required),
  //   password: new FormControl('', Validators.required)
  // })

  // login(): void {
  //   const emailControl = this.loginForm.get('username');
  //   const passwordControl = this.loginForm.get('password');

  //   if (!emailControl || !passwordControl) {
  //     console.error('Form controls not found');
  //     return;
  //   }

  //   const emailValue = emailControl.value?.trim();
  //   const passwordValue = passwordControl.value?.trim();

  //   if (!emailValue || !passwordValue) {
  //     Swal.fire({
  //       icon: 'error',
  //       title: 'Incomplete Information',
  //       text: 'Please enter both email and password.',
  //     });
  //     return;
  //   }

  //   const login: Login = {
  //     email: emailValue,
  //     password: passwordValue,
  //   };

  //   this.authService.login(login).subscribe({
  //     next: (response: AuthResponse) => {
        
  //       localStorage.setItem('user', response.token);
  //       this.authService.setUser();

  //       if(this.authService.getRole() != "ROLE_SUPER_ADMIN"){
  //         this.router.navigate(['/'])
  //       }else{
  //         this.authService.logout();
  //         Swal.fire({
  //           icon: 'error',
  //           title: 'Incorrect Login',
  //           text: 'Incorrect login credentials. Please try again.',
  //         });
  //       }
  //     },
  //     error: (error) => {
  //       Swal.fire({
  //         icon: 'error',
  //         title: 'Incorrect Login',
  //         text: 'Incorrect login credentials. Please try again.',
  //       });
  //     }
  //   });
  // }

  // togglePasswordVisibility() {
  //   this.isPasswordVisible = !this.isPasswordVisible;
  // }

}
