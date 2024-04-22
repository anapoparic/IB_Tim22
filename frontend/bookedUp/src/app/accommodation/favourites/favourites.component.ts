import {Component, OnInit} from '@angular/core';
import {forkJoin, Observable, of} from "rxjs";
import {Accommodation} from "../model/accommodation.model";
import {AccommodationService} from "../accommodation.service";
import {ActivatedRoute, Router} from "@angular/router";
import {AuthService} from "../../infrastructure/auth/auth.service";
import {PhotoService} from "../../shared/photo/photo.service";
import { AccommodationStatus } from '../model/enum/accommodationStatus.enum';
import {GuestService} from "../../user/guest/guest.service";
import {Guest} from "../../user/model/guest.model";

@Component({
  selector: 'app-favourites',
  templateUrl: './favourites.component.html',
  styleUrls: ['./favourites.component.css']
})
export class FavouritesComponent implements OnInit {
  protected readonly AccommodationStatus = AccommodationStatus;

  accommodations: Observable<Accommodation[]> = new Observable<Accommodation[]>();
  selectedClass: string = 'active-accommodations';

  priceTypeGuest: string = 'per guest';
  priceTypeNight: string = 'per night';

  photoDict: {accId: number, url: string}[] =[];
  acc: Accommodation[]=[];


  constructor(private accommodationService: AccommodationService,private router: Router, private route: ActivatedRoute, private authService: AuthService, private photoService: PhotoService, private guestService: GuestService) { }

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      this.loadAccommodations();
    });
    //this.accommodations = this.accommodationService.getAllActiveAccommodationsByHostId(this.authService.getUserID());

  }

  private loadAccommodations(): void {
    this.guestService.getGuestById(this.authService.getUserID()).subscribe((guest: Guest) => {
      if (guest) {
        const guestFavourites: Accommodation[] = [];

        if (guest.favourites && guest.favourites.length > 0) {
          for (const accommodation of guest.favourites) {
              if (accommodation.status == AccommodationStatus.Active) {
                  guestFavourites.push(accommodation);
              }
          }
        }

        this.accommodations = of(guestFavourites);
        this.acc = guestFavourites;
        this.loadPhotos();
      }
    });
  }

  changeStyle(className: string): void {
    this.selectedClass = className;
    if (className === 'active-accommodations') {
      this.router.navigate(['/favourites'], { queryParams: { filter: 'active' } });
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



  roundHalf(value: number | undefined): number | undefined {
    if (value) {
      return Math.round(value * 2) / 2;
    }
    return 0;
  }
}

