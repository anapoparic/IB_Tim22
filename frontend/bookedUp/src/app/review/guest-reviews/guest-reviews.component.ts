import { Component, Input, OnInit } from '@angular/core';
import { AccommodationService } from 'src/app/accommodation/accommodation.service';
import { Router, ActivatedRoute} from '@angular/router';
import { Accommodation } from 'src/app/accommodation/model/accommodation.model';
import { ReviewService } from '../review.service';
import { Review } from '../model/review.model';
import { Observable, map, of } from 'rxjs';
import Swal from "sweetalert2";
import {PhotoService} from "../../shared/photo/photo.service";
import {AuthService} from "../../infrastructure/auth/auth.service";

@Component({
  selector: 'app-guest-reviews',
  templateUrl: './guest-reviews.component.html',
  styleUrls: ['./guest-reviews.component.css']
})
export class GuestReviewsComponent implements OnInit {
  reviews: Observable<Review[]> = new Observable<Review[]>();
  selectedClass: string = 'all-accommodations';
  filter: string = 'all';

  photoDict: { accId: number, url: string }[] = [];
  photoDictUsers: { hostId: number, url:string}[] = [];
  review: Review[] = [];

  constructor(private reviewService: ReviewService, private accommodationService: AccommodationService, private authService: AuthService,private router: Router, private route: ActivatedRoute, private photoService: PhotoService) {
  }


  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.filter = params['filter'] || 'all';
      this.loadReviews();
    });
  }

  changeStyle(className: string): void {
    this.selectedClass = className;
    if (className === 'changed-accommodations') {
      this.router.navigate(['/guest-reviews'], {queryParams: {filter: 'waiting'}});
    } else if (className === 'new-accommodations') {
      this.router.navigate(['/guest-reviews'], {queryParams: {filter: 'posted'}});
    } else {
      this.router.navigate(['/guest-reviews'], {queryParams: {filter: 'all'}});
    }
  }

  private loadReviews(): void {
    if (this.filter === 'all') {
      this.reviewService.getGuestReviews(this.authService.getUserID())
      .pipe(
        map(reviews => reviews.sort((a, b) => {
          const timestampA = a.date ? new Date(a.date).getTime() : 0;
          const timestampB = b.date ? new Date(b.date).getTime() : 0;
          return timestampB - timestampA;
        }))
      )
      .subscribe(sortedReviews => {
        this.reviews = of(sortedReviews);
      });

      this.reviewService.getGuestReviews(this.authService.getUserID()).subscribe((results) => {
        this.review = results;
        this.loadPhotos();
        this.loadPhotosUsers();

      });
    } else if (this.filter === 'posted') {
      this.reviewService.getGuestAccommodationReviews(this.authService.getUserID()).pipe(
        map(reviews => reviews.sort((a, b) => {
          const timestampA = a.date ? new Date(a.date).getTime() : 0;
          const timestampB = b.date ? new Date(b.date).getTime() : 0;
          return timestampB - timestampA;
        }))
      )
      .subscribe(sortedReviews => {
        this.reviews = of(sortedReviews);
      });
      this.reviewService.getGuestAccommodationReviews(this.authService.getUserID()).subscribe((results) => {
        this.review = results;
        this.loadPhotos();
      });
    } else {

      this.reviewService.getGuestHostReviews(this.authService.getUserID()).pipe(
        map(reviews => reviews.sort((a, b) => {
          const timestampA = a.date ? new Date(a.date).getTime() : 0;
          const timestampB = b.date ? new Date(b.date).getTime() : 0;
          return timestampB - timestampA;
        }))
      )
      .subscribe(sortedReviews => {
        this.reviews = of(sortedReviews);
      });
      this.reviewService.getGuestHostReviews(this.authService.getUserID()).subscribe((results) => {
        this.review = results;
        this.loadPhotosUsers();

      });

    }
  }

  generateStars(rating: number | undefined): string[] {
    const stars: string[] = [];
    if(rating != undefined){
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
    return stars;
  }


  deleteReview(id: number | undefined): void {
    if (id !== undefined) {
      this.reviewService.deleteReview(id).subscribe(
          () => {
            Swal.fire({
              icon: 'success',
              title: 'Review deleted successfully!',
              showConfirmButton: false,
              timer: 1500
            });

            this.loadReviews();
          },
          (error) => {
            Swal.fire({
              icon: 'error',
              title: 'Error deleting review',
              text: 'An error occurred while deleting the review.',
            });

            console.error('Error deleting review:', error);
          }
      );
    } else {
      console.error('Invalid review id:', id);
    }
  }





  loadPhotos() {
    this.review.forEach((acc) => {
      // Provera postojanja acc i njegovog accommodation svojstva
      if (acc && acc.accommodation && acc.accommodation.photos && acc.accommodation.photos.length > 0) {
        this.photoService.loadPhoto(acc.accommodation.photos[0]).subscribe(
            (data) => {
              this.createImageFromBlob(data).then((url: string) => {
                if (acc.id) {
                  this.photoDict.push({ accId: acc.id, url: url });
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

  loadPhotosUsers() {
    this.review.forEach((acc) => {
      // Provera postojanja host objekta i profilePicture svojstva
      if (acc && acc.host && acc.host.profilePicture) {
        this.photoService.loadPhoto(acc.host.profilePicture).subscribe(
            (data) => {
              this.createImageFromBlob(data).then((url: string) => {
                if (acc.id) {
                  this.photoDictUsers.push({ hostId: acc.id, url: url });
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

  getPhotoUrlUser(hostId: number | undefined): string | undefined {
    const photo = this.photoDictUsers.find((item) => item.hostId === hostId);
    return photo ? photo.url : '';
  }


  roundHalf(value: number | undefined): number | undefined {
    if (value) {
      return Math.round(value * 2) / 2;
    }
    return 0;
  }
}
