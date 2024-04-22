import { Component, Input, OnInit } from '@angular/core';
import { AccommodationService } from '../accommodation.service';
import { Router, ActivatedRoute} from '@angular/router';
import { Accommodation } from '../model/accommodation.model';
import { Observable } from 'rxjs';
import Swal from "sweetalert2";
import {PhotoService} from "../../shared/photo/photo.service";

@Component({
  selector: 'app-accommodation-requests',
  templateUrl: './accommodation-requests.component.html',
  styleUrls: ['./accommodation-requests.component.css', '../../../styles.css']
})
export class AccommodationRequestsComponent implements OnInit {
  accommodations: Observable<Accommodation[]> = new Observable<Accommodation[]>();
  selectedClass: string = 'all-accommodations';
  filter: string = 'all';

  priceTypeGuest: string = 'per guest';
  priceTypeNight: string = 'per night';

  photoDict: { accId: number, url: string }[] = [];
  acc: Accommodation[] = [];

  constructor(private accommodationService: AccommodationService, private router: Router, private route: ActivatedRoute, private photoService: PhotoService) {
  }


  ngOnInit(): void {
    this.route.queryParams.subscribe(params => {
      this.filter = params['filter'] || 'all';
      this.loadAccommodations();
    });
  }

  changeStyle(className: string): void {
    this.selectedClass = className;
    if (className === 'changed-accommodations') {
      this.router.navigate(['/accommodation-requests'], {queryParams: {filter: 'changed'}});
    } else if (className === 'new-accommodations') {
      this.router.navigate(['/accommodation-requests'], {queryParams: {filter: 'new'}});
    } else {
      this.router.navigate(['/accommodation-requests'], {queryParams: {filter: 'all'}});
    }
  }

  private loadAccommodations(): void {
    if (this.filter === 'all') {
      this.accommodations = this.accommodationService.getAllModifiedAccommodations();
      this.accommodationService.getAllModifiedAccommodations().subscribe((results) => {
        this.acc = results;
        this.loadPhotos();
      });
    } else if (this.filter === 'new') {
      this.accommodations = this.accommodationService.getAllCreatedAccommodations();
      this.accommodationService.getAllCreatedAccommodations().subscribe((results) => {
        this.acc = results;
        this.loadPhotos();
      });
    } else {
      this.accommodations = this.accommodationService.getAllChangedAccommodations();
      this.accommodationService.getAllChangedAccommodations().subscribe((results) => {
        this.acc = results;
        this.loadPhotos();
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



  approveAccommodation(id: number): void {
    this.accommodationService.approveAccommodation(id)
        .subscribe(
            (approvedReservation) => {
              Swal.fire({
                icon: 'success',
                title: 'Accommodation Approved!',
                text: 'The accommodation has been successfully approved.',
              }).then(() => {
                this.loadAccommodations();
              });
            },
            (error) => {
              // Handle error
              Swal.fire({
                icon: 'error',
                title: 'Error Approving Accommodation',
                text: `An error occurred: ${error.message}`,
              });
            }
        );
  }

  rejectAccommodation(id: number): void {
    this.accommodationService.rejectAccommodation(id)
        .subscribe(
            (rejectedReservation) => {
              Swal.fire({
                icon: 'success',
                title: 'Accommodation Rejected!',
                text: 'The accommodation has been successfully rejected.',
              }).then(() => {
                this.loadAccommodations();
              });
            },
            (error) => {
              Swal.fire({
                icon: 'error',
                title: 'Error Rejecting Accommodation',
                text: `An error occurred: ${error.message}`,
              });
            }
        );
  }


  loadPhotos() {
    this.acc.forEach((acc) => {
      this.photoService.loadPhoto(acc.photos[0]).subscribe(
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

  roundHalf(value: number | undefined): number | undefined {
    if (value) {
      return Math.round(value * 2) / 2;
    }
    return 0;
  }
}
