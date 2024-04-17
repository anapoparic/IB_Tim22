import { Component, OnInit } from '@angular/core';
import { ReviewService } from '../review.service';
import { PhotoService } from 'src/app/shared/photo/photo.service';
import { Router, ActivatedRoute } from '@angular/router';
import { Observable, map } from 'rxjs';
import { AccommodationService } from 'src/app/accommodation/accommodation.service';
import { Accommodation } from 'src/app/accommodation/model/accommodation.model';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { Photo } from 'src/app/shared/model/photo.model';
import { UserService } from 'src/app/user/user.service';
import { Review } from '../model/review.model';
import Swal from 'sweetalert2';
import {Reservation} from "../../reservation/model/reservation.model";
import {ReservationService} from "../../reservation/reservation.service";
import {ReviewType} from "../model/enum/reviewType.enum";
import {GuestService} from "../../user/guest/guest.service";
import {User} from "../../user/model/user.model";
import {Guest} from "../../user/model/guest.model";

@Component({
  selector: 'app-add-review',
  templateUrl: './add-review.component.html',
  styleUrls: ['./add-review.component.css']
})
export class AddReviewComponent implements OnInit{
  
  acc!:Accommodation;
  accommodation: Observable<Accommodation> = new Observable<Accommodation>();

  pictureUrl: string = '';

  reservationId: number=1;
  res!: Reservation;
  reservation: Observable<Reservation> = new Observable<Reservation>();

  selectedAccommodationStars: number = 0;
  accommodationComment: string = '';
  selectedHostStars: number = 0;
  hostComment: string = '';

  loggedUser!: Guest;

  constructor( private router: Router, private route: ActivatedRoute, private reviewService: ReviewService, private photoService:PhotoService, private accommodationService: AccommodationService, private authService: AuthService, private reservationService: ReservationService, private guestService: GuestService) {}

    ngOnInit(): void {
        this.route.params.subscribe((params) => {
            if ('id' in params) {
                this.reservationId = params['id'];

                this.reservation =  this.reservationService.getReservationById(this.reservationId);
                this.reservationService.getReservationById(this.reservationId).subscribe((result) =>{
                    this.res = result;

                    // Provjerite da li postoji rezervacija i pridružite accommodation samo ako postoji
                    if (this.res && this.res.accommodation && this.res.accommodation.id) {
                        this.accommodation = this.accommodationService.getAccommodationById(this.res.accommodation.id);
                        this.accommodation.subscribe((accommodationResult) =>{
                            this.acc = accommodationResult;
                            this.loadPhotos();
                        });
                    } else {
                        console.error('Reservation data or accommodation information is missing.');
                    }
                });
            }

            this.guestService.getGuestById(this.authService.getUserID()).subscribe(
                (guest: Guest) => {
                    this.loggedUser = guest;
                },
                (error) => {
                    console.error('Error loading user:', error);
                }
            );
        });
    }


  onRatingClickedAccommodation(stars: number): void {
    this.selectedAccommodationStars = stars;
  }

  onRatingClickedHost(stars: number): void {
    this.selectedHostStars = stars;
  }


  saveReview(): void {
      if (this.accommodationComment.trim() !== '' && this.selectedAccommodationStars !== 0) {
          const reviewData: Review = {
              accommodation: this.acc,
              type: ReviewType.Accommodation,
              comment: this.accommodationComment,
              review: this.selectedAccommodationStars,
              date: new Date(),
              guest: this.loggedUser,
              host: undefined
          };

          this.reviewService.createReview(reviewData).subscribe(
              (createdReview) => {

                console.log(createdReview);
                  Swal.fire({
                      icon: 'success',
                      title: 'Review submitted successfully!',
                      text: 'You will be redirected to the review page.'
                  }).then(() => {
                      this.router.navigate(['/guest-reviews']);
                  });
              },
              (error) => {
                  // Greška pri kreiranju review-a
                  console.error('Error creating review:', error);
                  Swal.fire({
                      icon: 'error',
                      title: 'Error',
                      text: 'An error occurred while submitting the review.'
                  });
              }
          );
      }

      if (this.hostComment.trim() !== '' && this.selectedHostStars !== 0) {
          const reviewDataHost: Review = {
              host: this.acc.host,
              type: ReviewType.Host,
              comment: this.hostComment,
              review: this.selectedHostStars,
              date: new Date(),
              guest: this.loggedUser,
              accommodation: undefined

          };

          console.log(reviewDataHost);

          // Poziv servisa za kreiranje review-a
          this.reviewService.createReview(reviewDataHost).subscribe(
              (createdReview) => {
                  console.log(createdReview);

                  Swal.fire({
                      icon: 'success',
                      title: 'Review submitted successfully!',
                      text: 'You will be redirected to the review page.'
                  }).then(() => {
                      this.router.navigate(['/guest-reviews']);
                  });
              },
              (error) => {
                  // Greška pri kreiranju review-a
                  console.error('Error creating review:', error);
                  Swal.fire({
                      icon: 'error',
                      title: 'Error',
                      text: 'An error occurred while submitting the review.'
                  });
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

  loadPhotos() {
    const imageName = this.acc.photos[0];
    this.photoService.loadPhoto(imageName).subscribe(
      (data) => {
        this.createImageFromBlob(data).then((url: string) => {
          this.pictureUrl = url;
        }).catch(error => {
          console.error("Greška prilikom konverzije slike ${imageName}:" , error);
        });
      },
      (error) => {
        console.log("Doslo je do greske pri ucitavanju slike ${imageName}:" , error);
      }
    );
  }

}
