import { Component, OnInit } from '@angular/core';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { UserService } from 'src/app/user/user.service';

import { Router } from '@angular/router';
import {User} from "../../../user/model/user.model";
import {Observable} from "rxjs";
import {PhotoService} from "../../../shared/photo/photo.service";

@Component({
  selector: 'app-admin-nav-bar',
  templateUrl: './admin-nav-bar.component.html',
  styleUrls: ['./admin-nav-bar.component.css', '../../../../styles.css']
})
export class AdminNavBarComponent implements OnInit{
  isPopupVisible = false;

  role: string = '' ;
  loggedUser!: User;
  displayedImageUrl: string | null = null;

  constructor(private router: Router, private photoService:PhotoService, private authService: AuthService, private userService: UserService) {}

  ngOnInit(): void {
    this.authService.userState.subscribe((result) => {
      this.role = result;
    })

    this.userService.getUser(this.authService.getUserID()).subscribe(
        (user: User) => {
          this.loggedUser = user;
          this.loadPhotos();
        },
        (error) => {
          console.error('Error loading user:', error);
          // Handle error as needed
        }
    );
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

  loadPhotos() {
    if(this.loggedUser.profilePicture){
      this.photoService.loadPhoto(this.loggedUser.profilePicture).subscribe(
        (data) => {
          this.createImageFromBlob(data).then((url: string) => {
            this.displayedImageUrl=url;
          }).catch(error => {
            console.error("Greška prilikom konverzije slike ${imageName}: ", error);
          });
        },
        (error) => {
          console.log("Doslo je do greske pri ucitavanju slike ${imageName}:" , error);
        }
      );
    }
  }

  createImageFromBlob(imageBlob: Blob): Promise<string> {
    const reader = new FileReader();

    return new Promise<string>((resolve, reject) => {
      reader.onloadend = () => {
        resolve(reader.result as string);
      };
      reader.onerror = reject;
      reader.readAsDataURL(imageBlob);
    });
  }
}
