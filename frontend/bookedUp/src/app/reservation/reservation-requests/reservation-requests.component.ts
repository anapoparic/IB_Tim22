import { Component, OnInit } from '@angular/core';
import { Router, ActivatedRoute} from '@angular/router';
import {Observable, map, of} from 'rxjs';
import { Reservation } from 'src/app/reservation/model/reservation.model';
import { ReservationStatus } from 'src/app/reservation/model/reservationStatus.enum';
import { ReservationService } from 'src/app/reservation/reservation.service';
import {AuthService} from "../../infrastructure/auth/auth.service";
import Swal from "sweetalert2";
import {Accommodation} from "../../accommodation/model/accommodation.model";
import {PhotoService} from "../../shared/photo/photo.service";
import { DatePipe } from '@angular/common';
import { Notification } from 'src/app/shared/notifications/model/notification.model';
import { NotificationType } from 'src/app/shared/notifications/model/enum/notificationType.enum';
import { NotificationsService } from 'src/app/shared/notifications/service/notifications.service';
import { WebSocketService } from 'src/app/shared/notifications/service/web-socket.service';

@Component({
  selector: 'app-reservation-requests',
  templateUrl: './reservation-requests.component.html',
  styleUrls: ['./reservation-requests.component.css', '../../../styles.css']
})
export class ReservationRequestsComponent implements OnInit {

  reservations: Observable<Reservation[]> = new Observable<Reservation[]>();
  selectedClass: string = 'all-reservation';
  filter: string = 'all';

  photoDict: { accId: number, url: string }[] = [];
  res: Reservation[] = [];
  searchText: string = '';

  constructor(
    private reservationService: ReservationService, 
    private router: Router, private route: ActivatedRoute, 
    private authService: AuthService, 
    private photoService: PhotoService,
    private notificationService: NotificationsService,
    private webSocketService: WebSocketService) { }

  protected readonly ReservationStatus = ReservationStatus;

  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.filter = params['filter'] || 'all';
      this.loadReservations();
      console.log(this.reservations);
    });
  }

  private loadReservations(): void {
    if (this.filter === 'waiting') {
      //this.reservations = this.reservationService.getReservationsByStatusAndHostId(this.authService.getUserID(), ReservationStatus.Created);
      this.reservationService.getReservationsByStatusAndHostId(this.authService.getUserID(), ReservationStatus.Created)
      .pipe(
        map(reservations => reservations.sort((a, b) => {
          const timestampA = new Date(a.startDate).getTime();
          const timestampB = new Date(b.startDate).getTime();
          return timestampB - timestampA;
        }))
      )
      .subscribe(sortedReservations => {
        this.reservations = of(sortedReservations);
      });
      this.reservationService.getReservationsByStatusAndHostId(this.authService.getUserID(), ReservationStatus.Created).subscribe((results) => {
        this.res = results;
        this.loadPhotos();
      });
    } else if (this.filter === 'accepted') {
      this.reservationService.getReservationsByStatusAndHostId(this.authService.getUserID(), ReservationStatus.Accept).pipe(
        map(reservations => reservations.sort((a, b) => {
          const timestampA = new Date(a.startDate).getTime();
          const timestampB = new Date(b.startDate).getTime();
          return timestampB - timestampA;
        }))
      )
      .subscribe(sortedReservations => {
        this.reservations = of(sortedReservations);
      });
      this.reservationService.getReservationsByStatusAndHostId(this.authService.getUserID(), ReservationStatus.Accept).subscribe((results) => {
        this.res = results;
        this.loadPhotos();
      });
    } else if (this.filter === 'rejected') {
      this.reservationService.getReservationsByStatusAndHostId(this.authService.getUserID(), ReservationStatus.Reject).pipe(
        map(reservations => reservations.sort((a, b) => {
          const timestampA = new Date(a.startDate).getTime();
          const timestampB = new Date(b.startDate).getTime();
          return timestampB - timestampA;
        }))
      )
      .subscribe(sortedReservations => {
        this.reservations = of(sortedReservations);
      });
      this.reservationService.getReservationsByStatusAndHostId(this.authService.getUserID(), ReservationStatus.Reject).subscribe((results) => {
        this.res = results;
        this.loadPhotos();
      });
    } else if (this.filter === 'finished') {
      this.reservationService.getReservationsByStatusAndHostId(this.authService.getUserID(), ReservationStatus.Completed).pipe(
        map(reservations => reservations.sort((a, b) => {
          const timestampA = new Date(a.startDate).getTime();
          const timestampB = new Date(b.startDate).getTime();
          return timestampB - timestampA;
        }))
      )
      .subscribe(sortedReservations => {
        this.reservations = of(sortedReservations);
      });
      this.reservationService.getReservationsByStatusAndHostId(this.authService.getUserID(), ReservationStatus.Completed).subscribe((results) => {
        this.res = results;
        this.loadPhotos();
      });
    } else if (this.filter === 'cancelled') {
      this.reservationService.getReservationsByStatusAndHostId(this.authService.getUserID(), ReservationStatus.Cancelled).pipe(
        map(reservations => reservations.sort((a, b) => {
          const timestampA = new Date(a.startDate).getTime();
          const timestampB = new Date(b.startDate).getTime();
          return timestampB - timestampA;
        }))
      )
      .subscribe(sortedReservations => {
        this.reservations = of(sortedReservations);
      });
      this.reservationService.getReservationsByStatusAndHostId(this.authService.getUserID(), ReservationStatus.Cancelled).subscribe((results) => {
        this.res = results;
        this.loadPhotos();
      });
    }
    else {
      this.reservationService.getReservationsByHostId(this.authService.getUserID()).pipe(
        map(reservations => reservations.sort((a, b) => {
          const timestampA = new Date(a.startDate).getTime();
          const timestampB = new Date(b.startDate).getTime();
          return timestampB - timestampA;
        }))
      )
      .subscribe(sortedReservations => {
        this.reservations = of(sortedReservations);
      });
      this.reservationService.getReservationsByHostId(this.authService.getUserID()).subscribe((results) => {
        this.res = results;
        this.loadPhotos();
      });
    }
  }

  generateStars(rating: number): string[] {
    const stars: string[] = [];
    for (let i = 1; i <= 5; i++) {
      if (i <= rating) {
        stars.push('★');
      } else if (i - 0.5 === rating) {
        stars.push('✯');
      } else {
        stars.push('☆');
      }
    }
    return stars;
  }



  acceptReservation(id: number | undefined): void {
    if (id !== undefined) {
      this.reservationService.approveReservation(id)
          .subscribe(
              (approvedReservation) => {
                Swal.fire({
                  icon: 'success',
                  title: 'Reservation Approved!',
                  text: 'The reservation has been successfully approved.',
                }).then(() => {
                  this.loadReservations();
                });

                const notification: Notification = {
                  fromUserDTO: approvedReservation.accommodation.host,
                  toUserDTO: approvedReservation.guest,
                  title: 'Reservation Confirmed!',
                  message: 'Congratulations '+ approvedReservation.guest.firstName + '! Your reservation has been accepted by the host. Get ready for a fantastic stay!',
                  timestamp: new Date(),
                  type: NotificationType.reservationRequestResponse,
                  active: true
                };
      
                this.notificationService.createNotification(notification).subscribe(
                  (createdNotification) => {
                    console.log(createdNotification);
                  },
                  (error) => {
                    console.error('Error creating review:', error);
                  }
                );
                this.webSocketService.sendMessageUsingSocket(notification);       
              },
              (error) => {
                Swal.fire({
                  icon: 'error',
                  title: 'Error Approving Reservation',
                  text: `An error occurred: ${error.message}`,
                });
              }
          );
    } else {
      console.error('Error: reservationId is undefined.');
    }
  }

  rejectReservation(id: number | undefined): void {
    if (id !== undefined) {
      this.reservationService.rejectReservation(id)
          .subscribe(
              (rejectedReservation) => {
                Swal.fire({
                  icon: 'success',
                  title: 'Reservation Rejected!',
                  text: 'The reservation has been successfully rejected.',
                }).then(() => {
                  this.loadReservations();
                });

                const notification: Notification = {
                  fromUserDTO: rejectedReservation.accommodation.host,
                  toUserDTO: rejectedReservation.guest,
                  title: 'Reservation Update: Rejected!',
                  message: 'Hi '+ rejectedReservation.guest.firstName + ', unfortunately, the host has declined your reservation request. Do not worry, there are plenty of other great options available! Let us know if we can help you find another place!',
                  timestamp: new Date(),
                  type: NotificationType.reservationRequestResponse,
                  active: true
                };
      
                this.notificationService.createNotification(notification).subscribe(
                  (createdNotification) => {
                    console.log(createdNotification);
                  },
                  (error) => {
                    console.error('Error creating review:', error);
                  }
                );
                this.webSocketService.sendMessageUsingSocket(notification);       
              },
              (error) => {
                Swal.fire({
                  icon: 'error',
                  title: 'Error Rejecting Reservation',
                  text: `An error occurred: ${error.message}`,
                });
              }
          );
    } else {
      console.error('Error: reservationId is undefined.');
    }
  }

  getHostsReservations() {
    this.selectedClass = 'all-reservation';
    this.router.navigate(['my-reservations'], { queryParams: { filter: 'all' } });
    this.reservations = this.reservationService.getReservationsByHostId(this.authService.getUserID());
    this.reservationService.getReservationsByHostId(this.authService.getUserID()).subscribe((results) => {
      this.res = results;
      this.loadPhotos();
    });
  }


  getHostsReservationsByStatus(className: string, status: ReservationStatus) {
    this.selectedClass = className;
    if (className === 'waiting-reservation') {
      this.router.navigate(['/my-reservations'], { queryParams: { filter: 'waiting' } });
    } else if (className === 'accepted-reservation') {
      this.router.navigate(['/my-reservations'], { queryParams: { filter: 'accepted' } });
    } else if (className === 'rejected-reservation') {
      this.router.navigate(['/my-reservations'], { queryParams: { filter: 'rejected' } });
    } else if (className === 'finished-reservation') {
      this.router.navigate(['/my-reservations'], { queryParams: { filter: 'finished' } });
    } else {
      this.router.navigate(['/my-reservations'], { queryParams: { filter: 'cancelled' } });
    }

    this.reservations = this.reservationService.getReservationsByStatusAndHostId(this.authService.getUserID(), status);
    this.reservationService.getReservationsByStatusAndHostId(this.authService.getUserID(), status).subscribe((results) => {
      this.res = results;
      this.loadPhotos();
    });
  }

  loadPhotos() {
    this.res.forEach((ress) => {
      this.photoService.loadPhoto(ress.accommodation.photos[0]).subscribe(
          (data) => {
            this.createImageFromBlob(data).then((url: string) => {
              if (ress.accommodation.id) {
                this.photoDict.push({accId: ress.accommodation.id, url: url});
              }
            }).catch(error => {
              console.error("Greška prilikom konverzije slike ${imageName}:", error);
            });
          },
          (error) => {
            console.log("Doslo je do greske pri ucitavanju slike ${imageName}:", error);
          }
      );
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

  roundHalf(value: number | undefined): number {
    if (value) {
      return Math.round(value * 2) / 2;
    }
    return 0;
  }

  searchReservations() {
    if (this.searchText.trim() === '') {
      this.loadReservations();
    } else {
      const filteredReservations = this.res.filter((ress) =>
          this.containsSearchText(ress, this.searchText)
      );
      this.reservations = of(filteredReservations);
    }
  }

  private containsSearchText(reservation: Reservation, searchText: string): boolean {
    const datePipe = new DatePipe('en-US');
    const searchLower = searchText.toLowerCase();

    const nameIncludes = reservation.accommodation.name.toLowerCase().includes(searchLower);
    const startDateFormatted = datePipe.transform(reservation.startDate, 'EEEE, MMMM d, y');
    const endDateFormatted = datePipe.transform(reservation.endDate, 'EEEE, MMMM d, y');

    const startDateIncludes = startDateFormatted!.toLowerCase().includes(searchLower);
    const endDateIncludes = endDateFormatted!.toLowerCase().includes(searchLower);

    return nameIncludes || startDateIncludes || endDateIncludes;
}

}


