import {Component, OnInit} from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, concatMap, map, of } from 'rxjs';
import { PhotoService } from 'src/app/shared/photo/photo.service';
import Swal from 'sweetalert2';
import { User } from '../model/user.model';
import { UserService } from '../user.service';
import {AuthService} from "../../infrastructure/auth/auth.service";


import {HostService} from "../host/host.service";
import {UserReportService} from "../user-report/user-report.service";
import {GuestService} from "../guest/guest.service";
import {Host} from "../model/host.model";

@Component({
  selector: 'app-host-reports',
  templateUrl: './host-reports.component.html',
  styleUrls: ['./host-reports.component.css']
})
export class HostReportsComponent implements OnInit {
  users: Observable<User[]> = new Observable();
  filter: string = 'all';

  photoDict: { accId: number, url: string }[] = [];
  user: User[] = [];


  hosts: Observable<Host[]> = new Observable();
  host: Host[] = [];
  constructor(private userService: UserService, private router: Router, private route: ActivatedRoute, private photoService: PhotoService, private authService: AuthService, private guestservice: GuestService, private userReportService: UserReportService) {
  }


  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.loadUsers();
      this.loadHosts();
    });
  }

  private loadHosts() {
    this.hosts = this.guestservice.getHostsByGuestId(this.authService.getUserID());
    this.guestservice.getHostsByGuestId(this.authService.getUserID()).subscribe((results) => {
      this.host = results;
      this.loadPhotos();

      console.log('Hosts:', this.host);

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
    console.log('id:', id);

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
          console.log('reportedUser:', reportedUser);
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
