import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { UserService } from 'src/app/user/user.service';

import { Router } from '@angular/router';
import {Observable} from "rxjs";

@Component({
  selector: 'app-admin-nav-bar',
  templateUrl: './admin-nav-bar.component.html',
  styleUrls: ['./admin-nav-bar.component.css', '../../../../styles.css']
})
export class AdminNavBarComponent implements OnInit{
  isPopupVisible = false;

  role: string = '' ;

  constructor(private router: Router, private authService: AuthService, private userService: UserService) {}

  ngOnInit(): void {
    this.authService.userState.subscribe((result) => {
      this.role = result;
    })
  }

  onProfilePictureClick(): void {
    this.isPopupVisible = !this.isPopupVisible;
  }

  onPopupClick(event: Event): void {
    if (this.isPopupVisible && event.target instanceof HTMLElement && !event.target.closest('.admin-dropdown')) {
       this.isPopupVisible = false;
    }
  }
  
  navigateTo(route: string): void {
    this.isPopupVisible = false; // Close the popup when navigating
    this.router.navigate([route]);
  }

  logOut(): void {
    this.authService.logout().subscribe({
      next: (_) => {
        localStorage.removeItem('user');
        this.authService.setUser();
        this.router.navigate(['/']);
      }
    })
  }

}
