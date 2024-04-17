import { Component, OnInit  } from '@angular/core';
import { AccommodationService } from '../../accommodation/accommodation.service';
import { Router, ActivatedRoute} from '@angular/router';
import { Accommodation } from 'src/app/accommodation/model/accommodation.model';
import { Observable } from 'rxjs';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { DatePipe } from '@angular/common';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import {User} from "../../user/model/user.model";
import {UserService} from "../../user/user.service";
import { ReservationService } from '../reservation.service';
import { Reservation } from '../model/reservation.model';
import { ReservationStatus } from '../model/reservationStatus.enum';
import { Guest } from 'src/app/user/model/guest.model';
import {PhotoService} from "../../shared/photo/photo.service";

import { GuestService } from 'src/app/user/guest/guest.service';
import Swal from 'sweetalert2';
import { Notification } from 'src/app/shared/notifications/model/notification.model';
import { NotificationsService } from 'src/app/shared/notifications/service/notifications.service';
import { WebSocketService } from 'src/app/shared/notifications/service/web-socket.service';
import { NotificationType } from 'src/app/shared/notifications/model/enum/notificationType.enum';

@Component({
  selector: 'app-create-reservation',
  templateUrl: './create-reservation.component.html',
  styleUrls: ['./create-reservation.component.css']
})
export class CreateReservationComponent implements OnInit {

  reservationForm: FormGroup;
  accommodationId: number = 1;
  startDate!: string ;
  formattedStartDate: string | null = null;
  endDate!: string;
  formattedEndDate: string | null = null;
  totalPrice: number = 1;
  numberGuests: number = 0;
  status!: ReservationStatus;
  reservation! : Observable<Reservation>;
  createdStatus : string = "CREATED";
  acceptedStatus : string = "ACCEPTED";
  guest!: Guest;
  pictureUrls: string[] = [];
  acc!: Accommodation;
  newReservation! : Reservation;
  nightNumber: number = 1;

  loggedUser!: User;

  constructor(
    private fb: FormBuilder,
    private userService: UserService, 
    private router: Router, 
    private route: ActivatedRoute, 
    private accommodationService: AccommodationService, 
    private authService: AuthService, 
    private reservationService: ReservationService, 
    private photoService: PhotoService, 
    private guestService: GuestService,
    private notificationService: NotificationsService,
    private webSocketService: WebSocketService)

  {
    this.reservationForm = this.fb.group({
      firstName: [{value: '', disabled: true}],
      lastName: [{value: '', disabled: true}],
      email: [{value: '', disabled: true}],
      phoneNumber: [{value: '', disabled: true}],
    });
  }

  ngOnInit(): void {


    this.route.params.subscribe((params) => {
      this.accommodationId = params['id'];


      this.accommodationService.getAccommodationById(this.accommodationId).subscribe(
          (acc: Accommodation) => {
            this.acc = acc;
            this.loadPhotos();
          },
          (error) => {
            console.error('Error loading acc:', error);
          }
      );

      this.userService.getUser(this.authService.getUserID()).subscribe(
          (user: User) => {
            this.loggedUser = user;

            this.reservationForm!.setValue({
              firstName: user.firstName,
              lastName: user.lastName,
              email: user.email,
              phoneNumber: user.phone,
            });
          },
          (error) => {
            console.error('Error loading user:', error);
          }
      );

      this.guestService.getGuestById(this.authService.getUserID()).subscribe(
        (guest: Guest) => {
          this.guest = guest;
        },
        (error) => {
          console.error('Error loading guest:', error);
        }
      );



      this.route.queryParams.subscribe(queryParams => {
        this.startDate = queryParams['startDate'];
        const dateStart = new Date(this.startDate ?? new Date());
        this.formattedStartDate = new DatePipe('en-US').transform(dateStart, 'EEEE, MMMM d, y');

        this.endDate = queryParams['endDate'];
        const dateEnd = new Date(this.endDate ?? new Date());
        this.formattedEndDate = new DatePipe('en-US').transform(dateEnd, 'EEEE, MMMM d, y');

        this.totalPrice = queryParams['totalPrice'];
        this.numberGuests = queryParams['numberGuests'];
        this.nightNumber = queryParams['days'];



      });
    });
  }

  reserve() {

    let start = new Date(this.startDate);
    start.setHours(13,0,0,);
    let end = new Date(this.endDate);
    end.setHours(13,0,0,0);

    this.newReservation = {
      startDate: start,
      endDate : end,
      totalPrice : this.totalPrice,
      guestsNumber : this.numberGuests,
      accommodation : this.acc,
      guest : this.guest,
      status : this.acc.automaticReservationAcceptance ? this.acceptedStatus as ReservationStatus : this.createdStatus as ReservationStatus
    };

    
    this.reservationService.createReservation(this.newReservation).subscribe(
      (createdReservation: Reservation) => {

        const notificationCreate: Notification = {
          fromUserDTO: this.guest,
          toUserDTO: createdReservation.accommodation.host,
          title: 'New Reservation!',
          message: 'Exciting news,' + createdReservation.accommodation.host.firstName + '! A new reservation has just been made for your ' + createdReservation.accommodation.name + ' accommodation. Get ready to welcome your next guest! ',
          timestamp: new Date(),
          type: NotificationType.reservationCreated,
          active: true
        };

        this.notificationService.createNotification(notificationCreate).subscribe(
          (createdNotification) => {
            console.log(createdNotification);
          },
          (error) => {
            console.error('Error creating review:', error);
          }
        );
        this.webSocketService.sendMessageUsingSocket(notificationCreate);
        
        if(this.acc.automaticReservationAcceptance == true){
          const notification: Notification = {
            fromUserDTO: createdReservation.accommodation.host,
            toUserDTO: this.guest,
            title: 'Reservation Confirmed Instantly!',
            message: 'Hey '+ this.guest.firstName + '! Good news - your reservation has been automatically accepted. Your stay is confirmed and ready to go!',
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
        }

        
        
        
        console.log('Created Reservation:', createdReservation);
        Swal.fire({icon: 'success', title: 'Reservation created successfully!', text: 'You will be redirected to the home page.',});
        
        this.delayNavigation();
        
      },
      (error) => {
        Swal.fire({
          icon: 'error',
          title: 'Error',
          text: 'Sorry, an error occurred while creating the reservation. Please try again later.',
        });
      }
    );
    
    
  }

  delayNavigation(): void {
    setTimeout(() => {
      this.router.navigate(['/']);
    }, 1000);
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

  roundHalf(value: number): number {
    return Math.round(value * 2) / 2;
  }

  loadPhotos() {
    this.acc.photos.forEach((imageName) => {
      this.photoService.loadPhoto(imageName).subscribe(
          (data) => {
            this.createImageFromBlob(data).then((url: string) => {
              this.pictureUrls.push(url);
            }).catch(error => {
              console.error("Greška prilikom konverzije slike ${imageName}:" , error);
            });
          },
          (error) => {
            console.log("Doslo je do greske pri ucitavanju slike ${imageName}:" , error);
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
}
