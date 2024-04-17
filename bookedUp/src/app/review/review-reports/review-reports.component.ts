import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { Observable, concatMap, map, of } from 'rxjs';
import { PhotoService } from 'src/app/shared/photo/photo.service';
import Swal from 'sweetalert2';
import { Review } from '../model/review.model';
import { ReviewService } from '../review.service';
import {ReviewReportService} from "../review-report/review-report.service";
import { Notification } from 'src/app/shared/notifications/model/notification.model';
import { NotificationsService } from 'src/app/shared/notifications/service/notifications.service';
import { NotificationType } from 'src/app/shared/notifications/model/enum/notificationType.enum';
import { WebSocketService } from 'src/app/shared/notifications/service/web-socket.service';

@Component({
  selector: 'app-review-reports',
  templateUrl: './review-reports.component.html',
  styleUrls: ['./review-reports.component.css']
})
export class ReviewReportsComponent implements OnInit {
  reviews: Observable<Review[]> = new Observable();
  selectedClass: string = 'all-accommodations';
  filter: string = 'all';

  photoDict: { accId: number, url: string }[] = [];
  photoDictUser: { accId: number, url: string }[] = [];

  review: Review[] = [];
  reportedReasons: string[] = [];

  constructor(
    private reviewService: ReviewService,
    private router: Router, private route: ActivatedRoute,
    private photoService: PhotoService,
    private reviewReportService: ReviewReportService,
    private notificationService: NotificationsService,
    private webSocketService: WebSocketService) {
  }


  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.filter = params['filter'] || 'all';
      this.loadReviews();
    });
  }

  changeStyle(className: string): void {
    this.selectedClass = className;
    if (className === 'new-accommodations') {
      this.router.navigate(['/review-reports'], {queryParams: {filter: 'reported'}});
    } else {
      this.router.navigate(['/review-reports'], {queryParams: {filter: 'all'}});
    }
  }

  private loadReviews(): void {
    if (this.filter === 'all') {
      this.reviewService.getUnapprovedReviews().pipe(
        map(reviews => reviews.sort((a, b) => {
          const timestampA = new Date(a.date??0).getTime() ;
          const timestampB = new Date(b.date??0).getTime() ;
          return timestampB - timestampA;
        }))
      )
      .subscribe(sortedReviews => {
        this.reviews = of(sortedReviews);
      });
      this.reviewService.getUnapprovedReviews().subscribe((results) => {
        this.review = results;
        this.loadPhotos();
        this.loadPhotosUser()
      });
    } else if (this.filter === 'reported') {
      this.reviewReportService.getReportedReviews().pipe(
        map(reviews => reviews.sort((a, b) => {
          const timestampA = a.date ? new Date(a.date).getTime() : 0;
          const timestampB = b.date ? new Date(b.date).getTime() : 0;
          return timestampB - timestampA;
        }))
      )
      .subscribe(sortedReviews => {
        this.reviews = of(sortedReviews);
      });
      this.reviewReportService.getReportedReviews().subscribe((results) => {
        this.review = results;
        console.log(results);
        this.loadPhotos();
        this.loadPhotosUser()

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

  calculateDaysAgo(reviewDate: Date): number {
    const currentDate = new Date();
    const timeDifference = currentDate.getTime() - reviewDate.getTime();
    const daysDifference = Math.floor(timeDifference / (1000 * 60 * 60 * 24)); // convert milliseconds to days
    return daysDifference;
  }

  loadPhotosUser() {
    this.review.forEach((acc) => {
      // Provera postojanja acc i njegovog accommodation svojstva
      if (acc && acc.host && acc.host.profilePicture) {
        this.photoService.loadPhoto(acc.host.profilePicture).subscribe(
            (data) => {
              this.createImageFromBlob(data).then((url: string) => {
                if (acc.id) {
                  this.photoDictUser.push({ accId: acc.id, url: url });
                  console.log(this.photoDictUser)
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

  getPhotoUrlUser(accId: number | undefined): string | undefined {
    const photo = this.photoDictUser.find((item) => item.accId === accId);
    return photo ? photo.url : '';
  }


  roundHalf(value: number | undefined): number | undefined {
    if (value) {
      return Math.round(value * 2) / 2;
    }
    return 0;
  }

  onApproveClick(reviewId: number): void {
    this.reviewService.approveReview(reviewId).subscribe(
      (approvedReview) => {
        Swal.fire({
          icon: 'success',
          title: 'Review Approved!',
          text: 'The review has been successfully approved.',
          confirmButtonText: 'OK'
        });
        this.loadReviews();

        if(approvedReview.accommodation != undefined && approvedReview.guest != undefined){
          const notification: Notification = {
            fromUserDTO: approvedReview.guest,
            toUserDTO: approvedReview.accommodation.host,
            title: 'New Review for Your Accommodation!',
            message: 'Heads up, '+ approvedReview.accommodation.host.firstName + '! A new review awaits your attention. Check it out and keep the positive vibes going!',
            timestamp: new Date(),
            type: NotificationType.accommodationRated,
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

        }else if(approvedReview.host!= undefined && approvedReview.guest!= undefined){
          const notification: Notification = {
            fromUserDTO: approvedReview.guest,
            toUserDTO: approvedReview.host,
            title: 'New Review for You!',
            message: 'Curious about the latest buzz? A new review is in-time to hear what your guests are saying about their experience!',
            timestamp: new Date(),
            type: NotificationType.hostRated,
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
          //this.webSocketService.sendMessageUsingSocket(notification);
        }
      },
      (error) => {
        console.error('Error approving review:', error);
      }
    );
  }

  showReportReasons(id: number): void {
    this.reviewReportService.getReportReasonsForReview(id)
        .subscribe((reasons: string[]) => {
          this.reportedReasons = reasons;
          this.showSwalWithReportedReasons(id);
        });
  }


  showSwalWithReportedReasons(reviewId: number): void {
    Swal.fire({
      title: 'Reported Reasons',
      html: this.generateReportedReasonsHtml(),
      confirmButtonText: 'OK',
      showCancelButton: true,
      cancelButtonText: 'Delete Review',
    }).then((result) => {
      if (result.dismiss === Swal.DismissReason.cancel) {
        this.askForDeleteConfirmation(reviewId);
      }
    });
  }

  askForDeleteConfirmation(reviewId: number): void {
    this.reviewService.getReview(reviewId).subscribe(
        (review: Review) => {
          const reviewData = {
            comment: review.comment,
            date: review.date,
            guest: review.guest,
          };

          Swal.fire({
            title: 'Delete Review Confirmation',
            html: `
          <p>Review:</p>
          <p>Comment: ${reviewData.comment}</p>
          <p>Guest: ${reviewData.guest?.firstName} ${reviewData.guest?.lastName}</p>
        `,
            icon: 'warning',
            showCancelButton: true,
            confirmButtonText: 'Yes, delete review!',
            cancelButtonText: 'Cancel',
          }).then((result) => {
            if (result.isConfirmed) {
              this.deleteReview(reviewId);
            }
          });
        },
        (error) => {
          console.error('Error getting review:', error);
        }
    );
  }

  deleteReview(reviewId: number): void {
    this.reviewService.deleteReview(reviewId).subscribe(
        () => {
          this.loadReviews();
        },
        (error) => {
          console.error('Error deleting review:', error);
        }
    );
  }


  generateReportedReasonsHtml(): string {
    return this.reportedReasons.map((reason, index) => `
    <div style="margin-bottom: 8px;">
      <div style="border: 1px solid #ccc; padding: 8px; border-radius: 8px;">
        ${index + 1}. ${reason}
      </div>
    </div>`).join('');
  }


}
