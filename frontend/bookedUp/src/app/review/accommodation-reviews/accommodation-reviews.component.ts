import { Component, OnInit  } from '@angular/core';
import { AccommodationService } from 'src/app/accommodation/accommodation.service';
import { Router, ActivatedRoute} from '@angular/router';
import { Accommodation } from 'src/app/accommodation/model/accommodation.model';
import {Observable, map, last, forkJoin, of, merge} from 'rxjs';
import { Photo } from 'src/app/shared/model/photo.model';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { UserService } from 'src/app/user/user.service';
import {PhotoService} from "../../shared/photo/photo.service";
import { Review } from '../model/review.model';
import { ReviewService } from '../review.service';

@Component({
  selector: 'app-accommodation-reviews',
  templateUrl: './accommodation-reviews.component.html',
  styleUrls: ['./accommodation-reviews.component.css']
})
export class AccommodationReviewsComponent implements OnInit {
  pictureUrls: string[] = [];
  orgPictureUrls: string[] = [];

  accommodationId: number = 1;
  accommodation: Observable<Accommodation> = new Observable<Accommodation>();
  currentIndex: number = 0;
  startDate: string | null = null;
  endDate: string | null = null;
  totalPrice: number = 0;
  numberGuests: number = 0;
  days: number = 0;
  accommodations: Accommodation[] = [];
  foundAccommodation!: Accommodation;
  acc!:Accommodation;

  photoDictUser: { reviewId: number, url: string }[] = [];
  photoDict: { accId: number, url: string }[] = [];

  reviews: Observable<Review[]> = new Observable<Review[]>;
  accResult: Review[] = [];
  hostResult: Review[] = [];
  totalResults: Review[] = [];
  review: Review[] = [];


  constructor( private router: Router, private route: ActivatedRoute, private photoService:PhotoService, private accommodationService: AccommodationService, private authService: AuthService, private userService: UserService, private reviewService: ReviewService) {}

  ngOnInit(): void {
    this.route.params.subscribe((params) => {
      if ('id' in params) {
        this.accommodationId = params['id'];

        this.route.queryParams.subscribe(queryParams => {
          if ('startDate' in queryParams) {
            this.startDate = queryParams['startDate'];
          }
          if ('endDate' in queryParams) {
            this.endDate = queryParams['endDate'];
          }
          if ('totalPrice' in queryParams) {
            this.totalPrice = queryParams['totalPrice'];
          }
          if ('numberGuests' in queryParams) {
            this.numberGuests = queryParams['numberGuests'];
          }
          if ('days' in queryParams) {
            this.days = queryParams['days'];
          }
        });
      }

      this.accommodationService.getAccommodationById(this.accommodationId).subscribe((result) =>{
        this.acc=result;
        this.loadPhotos();
      })
      this.loadReviews();
    });

    this.accommodation = this.accommodationService.getAccommodationById(this.accommodationId);

    this.getUrls().subscribe((urls) => {
      this.orgPictureUrls = urls;
    });

  }

  private loadReviews() {
    this.reviews = this.reviewService.getAccommodationReviews(this.accommodationId);

    this.reviewService.getAccommodationReviews(this.accommodationId).subscribe((results) => {
      this.review = results;
      this.loadPhotosUser();  
    }); 
  }

  findAccommodationById(accommodations: Accommodation[], targetId: number): Accommodation | undefined {
    for (const accommodation of accommodations) {
      if (accommodation.id == targetId) {
        return accommodation;
      }
    }
    return undefined;
  }

  navigateTo(route: string): void {
    this.router.navigate([route, this.accommodationId], { queryParams: { startDate: this.startDate, endDate: this.endDate, totalPrice: this.totalPrice, numberGuests: this.numberGuests, days: this.days} });
  }

  getUrls(): Observable<string[]> {
    return this.accommodation.pipe(
      map((accommodation: { photos: Photo[]; }) => accommodation?.photos?.map((photo) => photo.url) || [])
    );
  }

  nextImage() {
    if (this.currentIndex < this.pictureUrls.length - 1) {
      this.currentIndex++;
    } else {
      this.currentIndex = 0;
    }
  }

  loadPhotosUser() {
    this.review.forEach((review) => {
      if (review && review.guest && review.guest.profilePicture) {
        this.photoService.loadPhoto(review.guest.profilePicture).subscribe(
          (data) => {
            this.createImageFromBlob(data).then((url: string) => {
              if (review.id) {
                this.photoDictUser.push({ reviewId: review.id, url: url });
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

  getPhotoUrl(reviewId: number | undefined): string | undefined {
    const photo = this.photoDict.find((item) => item.accId === reviewId);
    return photo ? photo.url : '';
  }

  getPhotoUrlUser(accId: number | undefined): string | undefined {
    const photo = this.photoDictUser.find((item) => item.reviewId === accId);
    return photo ? photo.url : '';
  }

  calculateTimeAgo(date: Date | string | undefined): string {
    try {
      if (!date) {
        throw new Error('Date is undefined or null');
      }
  
      let parsedDate: Date;
  
      if (date instanceof Date) {
        parsedDate = date;
      } else {
        parsedDate = new Date(date);
        if (isNaN(parsedDate.getTime())) {
          throw new Error('Invalid date string');
        }
      }
  
      const currentDate = new Date();
      const timeDifference = currentDate.getTime() - parsedDate.getTime();
      const secondsAgo = Math.floor(timeDifference / 1000);
      const minutesAgo = Math.floor(timeDifference / (1000 * 60));
      const hoursAgo = Math.floor(timeDifference / (1000 * 60 * 60));
      const daysAgo = Math.floor(timeDifference / (1000 * 60 * 60 * 24));
      const weeksAgo = Math.floor(timeDifference / (1000 * 60 * 60 * 24 * 7));
      const monthsAgo = Math.floor(timeDifference / (1000 * 60 * 60 * 24 * 30)); // Assuming a month has approximately 30 days
  
      if (secondsAgo < 60) {
        return 'Just now';
      } else if (minutesAgo < 60) {
        return `${minutesAgo} minute${minutesAgo > 1 ? 's' : ''} ago`;
      } else if (hoursAgo < 24) {
        return `${hoursAgo} hour${hoursAgo > 1 ? 's' : ''} ago`;
      } else if (daysAgo < 7) {
        if (daysAgo === 1) {
          return 'Yesterday';
        }
        return `${daysAgo} day${daysAgo > 1 ? 's' : ''} ago`;
      } else if (weeksAgo <= 6) {
        return `${weeksAgo} week${weeksAgo > 1 ? 's' : ''} ago`;
      } else if (monthsAgo > 0) {
        return `${monthsAgo} month${monthsAgo > 1 ? 's' : ''} ago`;
      } else {
        return 'N/A';
      }
    } catch (error) {
      console.error('Error calculating time ago:', error);
      return 'N/A';
    }
  }
  

  get reviewsChunks(): any[] {
    const chunkSize = 3;
    const chunks = [];

    for (let i = 0; i < this.review.length; i += chunkSize) {
      chunks.push(this.review.slice(i, i + chunkSize));
    }
    return chunks;
  }

  roundHalf(value: number | undefined): number | undefined {
    if (value) {
      return Math.round(value * 2) / 2;
    }
    return 0;
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

  protected readonly last = last;
}
