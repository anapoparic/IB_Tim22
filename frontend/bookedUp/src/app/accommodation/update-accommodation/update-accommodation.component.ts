import {ChangeDetectorRef, Component, Host, OnInit, ViewChild} from '@angular/core';
import {CalendarComponent} from "../../shared/calendar/calendar.component";
import {PriceType} from "../model/enum/priceType.enum";
import {AccommodationType} from "../model/enum/accommodationType.enum";
import {Amenity} from "../model/enum/amenity.enum";
import {PriceChange} from "../model/priceChange.model";
import {DateRange} from "../model/dateRange.model";
import {ActivatedRoute, Router} from "@angular/router";
import {HostService} from "../../user/host/host.service";
import {AuthService} from "../../infrastructure/auth/auth.service";
import {AccommodationService} from "../accommodation.service";
import {Accommodation} from "../model/accommodation.model";
import {AccommodationStatus} from "../model/enum/accommodationStatus.enum";
import Swal from "sweetalert2";
import {FormBuilder, FormGroup, Validators} from "@angular/forms";
import { Observable, map } from 'rxjs';
import { Photo } from 'src/app/shared/model/photo.model';
import {PhotoService} from "../../shared/photo/photo.service";
import { Address } from 'src/app/shared/model/address.model';

@Component({
  selector: 'app-update-accommodation',
  templateUrl: './update-accommodation.component.html',
  styleUrls: ['./update-accommodation.component.css']
})
export class UpdateAccommodationComponent implements OnInit {
  @ViewChild('calendarRef') calendarComponent: CalendarComponent | undefined;
  amenitiesList : string[] = [];
  accTypeList : string[] = [];
  accTypeChecked: { [key: string]: boolean } = {};
  selectedAccType: string = '';
  accAmenities: { [key: string]: boolean } = {};
  perNightChecked: boolean = true;
  perGuestChecked: boolean = false;
  defaultPrice: number = 0;
  minimumPrice: number | undefined;
  maximumPrice: number | undefined;
  description: string | undefined;
  name: string | undefined;
  addressStreet: string | undefined;
  city: string | undefined;
  postalCode: string | undefined;
  country: string | undefined;
  acceptReservations: boolean = false;
  isInputReadOnly: boolean = false;
  pictureUrls: string[] = [];
  orgPictureUrls: string[] = [];
  convertedUrls: string[] = [];
  currentIndex: number = 0;
  editedAcc!: Accommodation;
  address!: Address;


  customPricesInput: { [date: string]: number } = { };
  customPrice: number = 0;

  priceType: PriceType = PriceType.PerNight;
  accType: AccommodationType = AccommodationType.Apartment;
  copyPriceChange: PriceChange[] = [];
  accPriceChange: PriceChange[] = [];

  loggedUser!: Host;

  addedDates: { start: string, end: string }[] = [];
  availability: DateRange[] = [];

  //
  accommodation: Observable<Accommodation> = new Observable<Accommodation>();
  selectedAccommodation!: Accommodation;
  selectedAccommodationId: number = 0;

  updateForm: FormGroup | undefined;

  constructor(private router: Router,  private route: ActivatedRoute,private hostService: HostService, private authService: AuthService, private accommodationService: AccommodationService, private fb: FormBuilder, private photoService:PhotoService) {
    this.updateForm = this.fb.group({
      name: ['', Validators.required],
      streetAndNumber: ['', Validators.required],
      city: ['', Validators.required],
      postalCode: ['', Validators.required],
      country: ['', Validators.required],
      defaultPrice: [0, Validators.required],
      perNightChecked: [true],
      perGuestChecked: [false],
      description: ['', Validators.required],
      minimumGuest: [0, Validators.required],
      maximumGuest: [0, Validators.required],
      acceptReservations: [false],
    });
  }

  ngOnInit() {
    
    const amenityValues = Object.values(Amenity) as string[];
    for (const amenity of amenityValues) {
      this.amenitiesList.push(this.transformEnumToDisplayFormat(amenity));
    }
    const accTypeValues = Object.values(AccommodationType) as string[];
    for (const type of accTypeValues) {
      this.accTypeList.push(this.transformEnumToDisplayFormat(type));
    }

    this.route.params.subscribe((params) => {
      this.selectedAccommodationId = params['id'];

      this.accommodation = this.accommodationService.getAccommodationById(this.selectedAccommodationId);
      this.accommodationService.getAccommodationById(this.selectedAccommodationId).subscribe(
          (acc: Accommodation) => {
            this.selectedAccommodation = acc;
            this.loadPhotos();

            if(acc.priceType == PriceType.PerGuest){
              this.perGuestChecked = true;
              this.perNightChecked = false;
            }else{
              this.perNightChecked = true;
              this.perGuestChecked = false;
            }

            this.availability = acc.availability;
            this.accPriceChange = acc.priceChanges;
            this.copyPriceChange = acc.priceChanges;
            this.defaultPrice = acc.price;
            
            this.amenitiesList.forEach((amenity: string) => {
              const amenityEnumValue: Amenity = Amenity[amenity as keyof typeof Amenity];
              this.accAmenities[amenity] = acc.amenities.includes(amenityEnumValue);
            });
            
            this.accTypeList.forEach((accType: string) => {
              const accTypeEnumValue: AccommodationType = AccommodationType[accType as keyof typeof AccommodationType];

              this.accTypeChecked[accType] = this.selectedAccommodation?.type === accTypeEnumValue;
            });

            this.updateForm!.setValue({
              name: acc.name,
              streetAndNumber: acc.address.streetAndNumber,
              city: acc.address.city,
              postalCode: acc.address.postalCode,
              country: acc.address.country,
              defaultPrice: acc.price,
              perNightChecked: this.perNightChecked,
              perGuestChecked: this.perGuestChecked,
              description: acc.description,
              minimumGuest: acc.minGuests,
              maximumGuest: acc.maxGuests,
              acceptReservations: acc.automaticReservationAcceptance,
            });

            this.addedDates = this.availability.map((dateRange: DateRange) => {
              return {
                start: dateRange.startDate.toString().split('T')[0].trim(),
                end: dateRange.endDate.toString().split('T')[0].trim()
              };
            });

          },
          (error) => {console.error('Error loading user:', error);
          });

      this.hostService.getHost(this.authService.getUserID()).subscribe(
          (host: Host) => {
            this.loggedUser = host;
          },
          (error) => {
            console.error('Error loading user:', error);
          }
      );
      console.log("sndfjsnfjdsfjdsjdjfnd");
    });
    
    console.log("sta je selected date ", this.defaultPrice);
    //console.log("sta je default ", this.selectedAccommodation.price);

    this.getUrls().subscribe((urls) => {
      this.orgPictureUrls = urls;
    });
    
    this.updateForm?.get('defaultPrice')!.valueChanges.subscribe((value) => {
      this.defaultPrice = value;
    });
  }

  //ovo je za dodavanje images u folder
  // this.convertBlobToFiles(this.pictureUrls)
  //   .then(files => {
  //     for (const file of files) {
  //       this.photoService.uploadImage(file).subscribe(
  //         response => {
  //           console.log('Image uploaded successfully:', response);
  //           // Handle success as needed
  //         },
  //         error => {
  //           console.error('Error uploading image:', error);
  //           // Handle error as needed
  //         }
  //       );
  //       this.convertedUrls.push('images/'+file.name);
  //     }
  //   })
  //   .catch(error => {
  //     console.error('Error converting blob to files:', error);
  //   });

updateAccAmenities(amenity: string, isChecked: boolean): void {
  this.accAmenities[amenity] = isChecked;
  console.log(this.accAmenities);
}  

handlePerNightChange() {
  console.log("PER night changed to true" );
  if (this.updateForm?.get('perNightChecked')?.value) {
    this.updateForm.get('perGuestChecked')?.setValue(false);
    this.selectedAccommodation.priceType = PriceType.PerNight;
  }
}

handlePerGuestChange() {
  console.log("PER guest changed to true");
  if (this.updateForm?.get('perGuestChecked')?.value) {
    this.updateForm.get('perNightChecked')?.setValue(false);
    this.selectedAccommodation.priceType = PriceType.PerGuest;
  }
}

convertAmenities():Amenity[] {
  var amenities: Amenity[] = []
  if(this.accAmenities['Restaurant'] == true){
    amenities.push(Amenity.Restaurant);
  }
  if(this.accAmenities['Free Wifi'] == true){
    amenities.push(Amenity.FreeWifi);
  }
  if(this.accAmenities['Non Smoking Rooms'] == true){
    amenities.push(Amenity.NonSmokingRooms);
  }
  if(this.accAmenities['Parking'] == true){
    amenities.push(Amenity.Parking);
  }
  if(this.accAmenities['Swimming Pool'] == true){
    amenities.push(Amenity.SwimmingPool);
  }
  if(this.accAmenities['Fitness Centre'] == true){
    amenities.push(Amenity.FitnessCentre);
  }

  return amenities;
}

converAccType(): AccommodationType{
  var accType: AccommodationType = AccommodationType.Hostel;

  if(this.accTypeChecked['Hostel'] == true){
    accType = AccommodationType.Hostel;
  }
  if(this.accTypeChecked['Hotel'] == true){
    accType = AccommodationType.Hotel;
  }
  if(this.accTypeChecked['Villa'] == true){
    accType = AccommodationType.Villa;
  }
  if(this.accTypeChecked['Apartment'] == true){
    accType = AccommodationType.Apartment;
  }
  if(this.accTypeChecked['Resort'] == true){
    accType = AccommodationType.Resort;
  }

  return accType;
}

edit(){
  var amenities: Amenity[] = this.convertAmenities();

  var selectedAccommodationType: AccommodationType = this.converAccType();

  let pr : PriceType;
  if (this.perGuestChecked){
     pr = PriceType.PerGuest;
  } else {
    pr = PriceType.PerNight;
  }

  this.editedAcc = {
    name: this.updateForm?.get(['name'])?.value || '',
    description: this.updateForm?.get(['description'])?.value || '',
    address: {
      country: this.updateForm?.get(['country'])?.value || '',
      city: this.updateForm?.get(['city'])?.value|| '',
      postalCode: this.updateForm?.get(['postalCode'])?.value|| '',
      streetAndNumber: this.updateForm?.get(['streetAndNumber'])?.value|| '',
      latitude: this.selectedAccommodation.address.latitude,
      longitude: this.selectedAccommodation.address.longitude
    },
    amenities: amenities,
    // photos:  this.photos = this.pictureUrls.map(url => ({
    //   url: url,
    //   caption:'',
    //   active: true
    // })),//??
    photos: this.selectedAccommodation.photos,
    minGuests: this.selectedAccommodation.minGuests|| 0,
    maxGuests:this.selectedAccommodation.maxGuests|| 0,
    type: selectedAccommodationType,
    availability: this.selectedAccommodation.availability,
    priceType: pr,
    priceChanges: this.selectedAccommodation.priceChanges,//
    automaticReservationAcceptance: this.updateForm?.get(['acceptReservations'])?.value,
    status: AccommodationStatus.Changed,
    host: this.selectedAccommodation.host,
    price: this.updateForm?.get(['defaultPrice'])?.value,
    cancellationDeadline: this.selectedAccommodation.cancellationDeadline || 0,
  };


  
  console.log('Changed acc:', this.editedAcc);

  this.accommodationService.updateAccommodation(this.selectedAccommodation.id ?? 0, this.editedAcc)
  .subscribe(updatedAccommodation => {
    console.log('Update successful:', updatedAccommodation); // Log the updated data
    Swal.fire({ icon: 'success', title: 'Accommodation edited successfully!', text: 'You will be directed to a page featuring your accommodations.' });    
    this.router.navigate(['my-accommodations']);
  }, error => {
    console.error('Error updating accommodation:', error);
    if (error.status === 403) {
      Swal.fire({
        icon: 'error',
        title: 'Change Denied!',
        text: 'Whoops! It looks like there are existing reservations for this accommodation under your hosting profile, making it currently unmodifiable.',
      });
      this.router.navigate(['my-accommodations']);
    } else {
      Swal.fire({
        icon: 'error',
        title: 'Error',
        text: 'Uh-oh! It seems there is a hiccup. We are encountering an error that prevents modifications at the moment. Please try again later.',
      });
    }
  });

}


handleAccTypeChange(selectedAccType: string): void {
  this.accType = AccommodationType[selectedAccType as keyof typeof AccommodationType];
  Object.keys(this.accTypeChecked).forEach(accType => {
    if (accType !== selectedAccType) {
      this.accTypeChecked[accType] = false;
    }
  });
  console.log(this.accTypeChecked);
}

transformEnumToDisplayFormat(enumValue: string): string {
  const words = enumValue.split('_');
  const formattedString = words.map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()).join(' ');
  return formattedString;
}


applyCustomPrice(): void {
    if(this.isInputReadOnly == false){
      Swal.fire({
        title: 'Default Price Changed!',
        text: 'You can no longer change it in this window.',
        icon: 'warning',
        showCancelButton: true,
        confirmButtonText: 'OK',
        cancelButtonText: 'Cancel',
      }).then((result) => {
        if (result.isConfirmed) {
          if(this.defaultPrice != 0){
            this.isInputReadOnly = true;
            const selectedRange = this.calendarComponent?.getSelectedRange();
            if (selectedRange != null && selectedRange.start != null && selectedRange.start !== undefined && selectedRange.end != null && selectedRange.end !== undefined) {

              const startDate = new Date(selectedRange.start);
              const endDate = new Date(selectedRange.end);

              const newPriceChangeStart: PriceChange = {
                changeDate: startDate,
                newPrice: this.customPrice,
              };

              this.accPriceChange = [...this.accPriceChange, newPriceChangeStart];
              this.updateCopyPriceChange();



              var lastDate = new Date(selectedRange.end);
              lastDate.setDate(lastDate.getDate() + 1);
              const newPriceChange: PriceChange = {
                changeDate: lastDate,
                newPrice: this.defaultPrice,
              };

              this.accPriceChange = [...this.accPriceChange, newPriceChange];
              this.updateCopyPriceChange();

            }
          }else{
            alert("You didn't input default price. Please provide a default price before entering a custom one.");
            return;
          }
        } else if (result.dismiss === Swal.DismissReason.cancel) {
          return;
        }
      });
    }else{
      if(this.defaultPrice != 0){
        this.isInputReadOnly = true;
        const selectedRange = this.calendarComponent?.getSelectedRange();
        if (selectedRange != null && selectedRange.start != null && selectedRange.start !== undefined && selectedRange.end != null && selectedRange.end !== undefined) {

          const startDate = new Date(selectedRange.start);
          const endDate = new Date(selectedRange.end);

          const newPriceChangeStart: PriceChange = {
            changeDate: startDate,
            newPrice: this.customPrice,
          };

          this.accPriceChange = [...this.accPriceChange, newPriceChangeStart];
          this.updateCopyPriceChange();



          var lastDate = new Date(selectedRange.end);
          lastDate.setDate(lastDate.getDate() + 1);
          const newPriceChange: PriceChange = {
            changeDate: lastDate,
            newPrice: this.defaultPrice,
          };

          this.accPriceChange = [...this.accPriceChange, newPriceChange];
          this.updateCopyPriceChange();

        }
      }else{
        alert("You didn't input default price. Please provide a default price before entering a custom one.");
        return;
      }
    }



  }

addDateRange() {
  const selectedDates = this.calendarComponent?.getSelectedRange();

  if (selectedDates?.start !== null && selectedDates?.end !== null && selectedDates?.hasAlreadyPicked) {
    this.addedDates.push({ start: selectedDates.start, end: selectedDates.end });
    this.addedDates = this.mergeOverlappingDateRanges(this.addedDates);

    if (this.calendarComponent?.selectedRange) {
      this.calendarComponent.selectedRange.start = null;
      this.calendarComponent.selectedRange.end = null;
    }

    this.calendarComponent?.generateCalendar();
  } else {
    alert('Please select valid date range!');
  }
}

private mergeOverlappingDateRanges(dateRanges: { start: string, end: string }[]): { start: string, end: string }[] {
  dateRanges.sort((a, b) => new Date(a.start).getTime() - new Date(b.start).getTime());
  const mergedRanges: { start: string, end: string }[] = [];
  let currentRange = dateRanges[0];
  for (let i = 1; i < dateRanges.length; i++) {
    const nextRange = dateRanges[i];

    const currentStartDate = new Date(currentRange.start);
    const currentEndDate = new Date(currentRange.end);
    const nextStartDate = new Date(nextRange.start);
    const nextEndDate = new Date(nextRange.end);


    if (currentEndDate >= nextStartDate) {
      if(currentEndDate >= nextEndDate){
        continue;
      }else{
        currentRange.end = nextRange.end;
      }
    } else {
      mergedRanges.push(currentRange);
      currentRange = nextRange;
    }
  }

  mergedRanges.push(currentRange);

  return mergedRanges;
}

deleteDateRange(): void {
  const selectedDates = this.calendarComponent?.getSelectedRange();

  if (selectedDates?.start && selectedDates?.end) {
    const startSelectedDate = new Date(selectedDates.start);
    const endSelectedDate = new Date(selectedDates.end);

    this.addedDates.forEach((dateRange, index) => {
      const startDate = new Date(dateRange.start);
      const endDate = new Date(dateRange.end);

      if (endSelectedDate < startDate || startSelectedDate > endDate) {
        // No overlap, do nothing
      } else if (startSelectedDate <= startDate && endSelectedDate >= endDate) {
        // Selected range completely covers the current range, remove it
        this.addedDates.splice(index, 1);
      } else if (startSelectedDate <= startDate && endSelectedDate < endDate) {
        // Overlapping on the left side, adjust start date
        dateRange.start = endSelectedDate.toISOString().split('T')[0];
      } else if (startSelectedDate > startDate && endSelectedDate >= endDate) {
        // Overlapping on the right side, adjust end date
        dateRange.end = startSelectedDate.toISOString().split('T')[0];
      } else if (startSelectedDate > startDate && endSelectedDate < endDate) {
        // Selected range is in the middle, split the current range
        const newEndDate = endSelectedDate.toISOString().split('T')[0];
        dateRange.end = startSelectedDate.toISOString().split('T')[0];

        // Insert a new range for the right side
        this.addedDates.splice(index + 1, 0, {
          start: newEndDate,
          end: endDate.toISOString().split('T')[0]
        });
      }
    });

    this.addedDates = this.mergeOverlappingDateRanges(this.addedDates);
  }
}

nextImage() {
  if (this.currentIndex < this.pictureUrls.length - 1) {
    this.currentIndex++;
  } else {
    this.currentIndex = 0;
  }
}

getUrls(): Observable<string[]> {
  return this.accommodation.pipe(
    map((accommodation: { photos: Photo[]; }) => accommodation?.photos?.map((photo) => photo.url) || [])
  );
}

uploadNewImage(): void {
  const fileInput = document.getElementById('fileInput') as HTMLInputElement;
  fileInput.click();
}

handleFileInputChange(event: Event): void {
  const inputElement = event.target as HTMLInputElement;

  if (inputElement.files && inputElement.files.length > 0) {
    const file = inputElement.files[0];

    const imageUrl = URL.createObjectURL(file);

    this.pictureUrls.push(imageUrl);

    inputElement.value = '';
  }
  console.log("ovo je posle dodavanja ", this.pictureUrls);
}

deleteImage():void{
  if (this.currentIndex >= 0 && this.currentIndex < this.pictureUrls.length) {
    this.pictureUrls.splice(this.currentIndex, 1);
    if (this.currentIndex >= this.pictureUrls.length) {
      this.currentIndex = this.pictureUrls.length - 1;
    }
  } else {
    console.error('Invalid currentIndex value');
  }
  console.log("ovo je posle brisanja ", this.pictureUrls);
}

loadPhotos() {
  this.selectedAccommodation.photos.forEach((imageName) => {
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

convertBlobToFiles(blobUrls: string[]): Promise<File[]> {
  const files: File[] = [];

  // Map each blob URL to a Promise that resolves to a File
  const promises = blobUrls.map(blobUrl =>
    fetch(blobUrl)
      .then(response => response.blob())
      .then(blob => new File([blob], `image_${Date.now()}.png`, { type: blob.type }))
  );

  // Use Promise.all to wait for all promises to resolve
  return Promise.all(promises);
}

private updateCopyPriceChange(): void {
  if (this.accPriceChange.length % 2==0) {
    const lastTwoChanges = this.accPriceChange.slice(-2); // Poslednja dva elementa

    if (this.copyPriceChange.length === 0) {
      // Ako je copyPriceChange prazan, kopiraj poslednja dva elementa iz accPriceChange
      this.copyPriceChange = [...lastTwoChanges];
    } else {
      // Ako nije prazan, ažuriraj ga na osnovu poslednja dva elementa iz accPriceChange
      const lastTwoCopyChanges = this.copyPriceChange.slice(-2);

      const startDate =lastTwoChanges[0].changeDate;
      const endDate = lastTwoChanges[1].changeDate;

      // Iteriraj kroz copyPriceChange i ažuriraj ga
      this.copyPriceChange.forEach((copyChange, index) => {
        const copyDate = copyChange.changeDate;

        if (copyDate >= startDate && copyDate <= endDate) {
          // Datum u copyPriceChange je između startDate i endDate, izbaci ga
          this.copyPriceChange.splice(index, 1);
        }
      });

      const uniqueDates = this.copyPriceChange.filter((change, index, self) => {
        const currentChangeDate = new Date(change.changeDate).toISOString();
        const isUnique = index === self.findIndex(c => currentChangeDate === new Date(c.changeDate).toISOString());

        const isSameAsStartDate = currentChangeDate === new Date(startDate).toISOString();

        return isUnique && !isSameAsStartDate;
      });

      this.copyPriceChange = [
        ...uniqueDates,
        { changeDate: startDate, newPrice: lastTwoChanges[0].newPrice },
        { changeDate: endDate, newPrice: lastTwoChanges[1].newPrice }
      ];



    }
  }
}

}

