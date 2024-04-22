import { Component, OnInit } from '@angular/core';
import { Reservation } from '../model/reservation.model';
import { Observable } from 'rxjs';
import { ActivatedRoute, Router } from '@angular/router';
import { ReservationService } from '../reservation.service';
import { PhotoService } from 'src/app/shared/photo/photo.service';
import { AccommodationService } from 'src/app/accommodation/accommodation.service';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { UserService } from 'src/app/user/user.service';
import Swal from 'sweetalert2';
import { ReservationStatus } from '../model/reservationStatus.enum';
import { Notification } from 'src/app/shared/notifications/model/notification.model';
import { NotificationsService } from 'src/app/shared/notifications/service/notifications.service';
import { WebSocketService } from 'src/app/shared/notifications/service/web-socket.service';
import { NotificationType } from 'src/app/shared/notifications/model/enum/notificationType.enum';
import { Role } from 'src/app/user/model/role.enum';

@Component({
  selector: 'app-reservation-details',
  templateUrl: './reservation-details.component.html',
  styleUrls: ['./reservation-details.component.css']
})
export class ReservationDetailsComponent implements OnInit{
  
  reser!: Reservation;
  reservation: Observable<Reservation> = new Observable<Reservation>();

  pictureUrl: string = '';
  reservationId: number = 1;

  cancellation: boolean = true;

  constructor( private router: Router, 
    private route: ActivatedRoute, 
    private reservationService: ReservationService, 
    private photoService: PhotoService, 
    private accommodationService: AccommodationService, 
    private authService: AuthService, 
    private userService: UserService,
    private notificationService: NotificationsService,
    private webSocketService: WebSocketService
    ) {}

  ngOnInit(): void {

    this.route.params.subscribe((params) => {
      if ('id' in params) {
        this.reservationId = params['id'];
      }

      this.reservation =  this.reservationService.getReservationById(this.reservationId);
      this.reservationService.getReservationById(this.reservationId).subscribe((result) =>{
        this.reser = result;
        this.canCancel();
      })

    });
  }

  cancelReservation(): void {
    if(this.reser.id != undefined){
      this.reservationService.cancelReservation(this.reser.id).subscribe(
        (cancelledReservation) => {
          

          this.reservationService.getReservationsByStatusAndGuestId(this.authService.getUserID(), ReservationStatus.Cancelled).subscribe(
          (cancelledReservations) => {
            const numberOfCancelledReservations = cancelledReservations.length;
            Swal.fire({
              icon: 'success',
              title: 'Reservation cancelled successfully!',
              text: `Number of cancelled reservations: ${numberOfCancelledReservations}!`,
            })
            .then(() => {
              this.router.navigate(['/my-reservations']);
            });
          },
          (error) => {
            console.error('Error creating review:', error);
          });
          

          const notification: Notification = {
            fromUserDTO: this.reser.guest,
            toUserDTO: this.reser.accommodation.host,
            title: 'Reservation Update: Cancelled!',
            message: 'Unfortunately, a reservation for your accommodation has been cancelled. Feel free to review the details and reach out to our support if you need any assistance',
            timestamp: new Date(),
            type: NotificationType.reservationCanceled,
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
          Swal.fire('Error!', 'An error occurred while canceling the reservation.', 'error');
        }
      );
    }
  }

  canCancel(): void{
    if(this.authService.getRole() !== "ROLE_HOST"){
      if( this.reser.status === ReservationStatus.Accept){
        const reservationStartDate = new Date(this.reser.startDate);

        const currentDate = new Date();
        const timeDifference =  reservationStartDate.getTime() - currentDate.getTime();
        const daysDifference = Math.floor(timeDifference / (1000 * 60 * 60 * 24));
        
        console.log("Ovo je days diff ", daysDifference);
        
        if(this.reser.accommodation.cancellationDeadline >= daysDifference){
          this.cancellation = false;
        }
      }else if( this.reser.status == ReservationStatus.Reject || this.reser.status == ReservationStatus.Cancelled || this.reser.status == ReservationStatus.Completed){
        this.cancellation = false;
      }
    }else{
      this.cancellation = false;
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

  roundHalf(value: number): number {
    return Math.round(value * 2) / 2;
  }

  getStatusColor(status: string): string {
    switch (status) {
      case 'CREATED':
        return 'var(--color-orange)';
      case 'REJECTED':
        return 'var(--color-firebrick)';
      case 'ACCEPTED':
        return 'var(--color-seagreen-100)';
      case 'CANCELLED':
        return 'var(--color-firebrick)';
      case 'COMPLETED':
        return 'var(--blue-1)';
      default:
        return 'inherit'; // default color or 'inherit' if no match
    }
  }
}

