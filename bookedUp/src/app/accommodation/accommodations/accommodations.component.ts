import { Component, Input, OnInit } from '@angular/core';
import { AccommodationService } from '../accommodation.service';
import { Router, ActivatedRoute} from '@angular/router';
import { Accommodation } from '../model/accommodation.model';
import { Observable } from 'rxjs';
import {AuthService} from "../../infrastructure/auth/auth.service";
import {AccommodationStatus} from "../model/enum/accommodationStatus.enum";
import {PhotoService} from "../../shared/photo/photo.service";
@Component({
    selector: 'app-accommodations',
    templateUrl: './accommodations.component.html',
    styleUrls: ['./accommodations.component.css', '../../../styles.css']
})
export class AccommodationsComponent implements OnInit {
    protected readonly AccommodationStatus = AccommodationStatus;

    accommodations: Observable<Accommodation[]> = new Observable<Accommodation[]>();
    selectedClass: string = 'active-accommodations';
    filter: string = 'active';

    priceTypeGuest: string = 'per guest';
    priceTypeNight: string = 'per night';

    photoDict: {accId: number, url: string}[] =[];
    acc: Accommodation[]=[];


    constructor(private accommodationService: AccommodationService, private router: Router, private route: ActivatedRoute, private authService: AuthService, private photoService: PhotoService) { }

    ngOnInit() {
        this.route.queryParams.subscribe(params => {
            this.filter = params['filter'] || 'active';
            this.loadAccommodations();
        });
        //this.accommodations = this.accommodationService.getAllActiveAccommodationsByHostId(this.authService.getUserID());

    }

    private loadAccommodations(): void {
        if (this.filter === 'active') {
            this.accommodations = this.accommodationService.getAllActiveAccommodationsByHostId(this.authService.getUserID());
            this.accommodationService.getAllActiveAccommodationsByHostId(this.authService.getUserID()).subscribe((results) => {
                this.acc = results;
                this.loadPhotos();
            });
        } else if (this.filter === 'requests') {
            this.accommodations = this.accommodationService.getAllRequestsByHostId(this.authService.getUserID());
            this.accommodationService.getAllRequestsByHostId(this.authService.getUserID()).subscribe((results) => {
                this.acc = results;
                this.loadPhotos();
            });
        } else {
            this.accommodations = this.accommodationService.getAllRejectedByHostId(this.authService.getUserID());
            this.accommodationService.getAllRejectedByHostId(this.authService.getUserID()).subscribe((results) => {
                this.acc = results;
                this.loadPhotos();
            });
        }
    }

    changeStyle(className: string): void {
        this.selectedClass = className;
        if (className === 'active-accommodations') {
            this.router.navigate(['/my-accommodations'], { queryParams: { filter: 'active' } });
        } else if (className === 'requests-accommodations') {
            this.router.navigate(['/my-accommodations'], { queryParams: { filter: 'requests' } });
        } else {
            this.router.navigate(['/my-accommodations'], { queryParams: { filter: 'rejected' } });
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

    loadPhotos() {
        this.acc.forEach((acc) => {
            this.photoService.loadPhoto(acc.photos[0]).subscribe(
                (data) => {
                    this.createImageFromBlob(data).then((url: string) => {
                        if(acc.id){
                            this.photoDict.push({accId: acc.id, url: url});
                        }
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
}
