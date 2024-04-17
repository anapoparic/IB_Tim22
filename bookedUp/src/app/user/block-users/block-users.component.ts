import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import {Observable, concatMap, map, of, catchError} from 'rxjs';
import { PhotoService } from 'src/app/shared/photo/photo.service';
import Swal from 'sweetalert2';
import { User } from '../model/user.model';
import { UserService } from '../user.service';
import {Guest} from "../model/guest.model";
import {AuthService} from "../../infrastructure/auth/auth.service";
import {HostService} from "../host/host.service";
import {UserReportService} from "../user-report/user-report.service";

@Component({
  selector: 'app-block-users',
  templateUrl: './block-users.component.html',
  styleUrls: ['./block-users.component.css']
})
export class BlockUsersComponent implements OnInit {
  users: Observable<User[]> = new Observable();
  filter: string = 'all';

  photoDict: { accId: number, url: string }[] = [];
  user: User[] = [];


  guests: Observable<Guest[]> = new Observable();
  guest: Guest[] = [];
  constructor(private userService: UserService, private router: Router, private route: ActivatedRoute, private photoService: PhotoService, private authService: AuthService, private hostService: HostService, private userReportService: UserReportService) {
  }


  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.loadUsers();
      this.loadGuests();
    });
  }

  private loadGuests() {
    this.guests = this.hostService.getHostGuests(this.authService.getUserID());
    this.hostService.getHostGuests(this.authService.getUserID()).subscribe((results) => {
      this.guest = results;
      this.loadPhotos();

      console.log('Hosts:', this.guest);

    });

  }

  private loadUsers(): void {
    this.users = this.userService.getUsers();
      this.userService.getUsers().subscribe((results) => {
        this.user = results;
        this.loadPhotos();
    });
  }

  reportUser(id: number): void {
    Swal.fire({
      title: 'Report User',
      input: 'text',
      inputLabel: 'Reason for Reporting:',
      showCancelButton: true,
      confirmButtonText: 'Report',
      cancelButtonText: 'Cancel',
      showLoaderOnConfirm: true,
      preConfirm: async (reason) => {
        try {
          const reportedUser = await this.userService.getUser(id).toPromise();
          const createdUserReport = await this.userReportService.createUserReport({
            reportedUser: reportedUser,
            reason: reason,
            status: true
          }).toPromise();

          return createdUserReport;  // Vratite kreirani izveštaj
        } catch (error) {
          Swal.showValidationMessage(`Request failed: ${error}`);
          return null;  // Ako dođe do greške, vratite null ili odgovarajuću vrednost
        }
      },
      allowOutsideClick: () => !Swal.isLoading(),
    }).then((result) => {
      if (result.isConfirmed) {
        Swal.fire({
          icon: 'success',
          title: 'User Reported!',
          text: 'The user has been successfully reported.',
        });
      } else if (result.dismiss === Swal.DismissReason.cancel) {
        Swal.fire('Cancelled', 'User reporting was cancelled', 'info');
      }
    });
  }




  loadPhotos() {
    this.user.forEach((acc) => {
      if (acc.profilePicture) {
        this.photoService.loadPhoto(acc.profilePicture).subscribe(
            (data) => {
              this.createImageFromBlob(data).then((url: string) => {
                if (acc.id) {
                  this.photoDict.push({accId: acc.id, url: url});
                }
              }).catch(error => {
                console.error("Greška prilikom konverzije slike ${imageName}:", error);
              });
            },
            (error) => {
              console.log("Doslo je do greske pri ucitavanju slike ${imageName}:", error);
            }
        );
      }
    });
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

  getPhotoUrl(accId: number | undefined): string | undefined {
    const photo = this.photoDict.find((item) => item.accId === accId);
    return photo ? photo.url : '';
  }


}
