import { Component, OnInit, ElementRef } from '@angular/core';
import { AccommodationService } from '../../accommodation/accommodation.service';
import { Accommodation } from '../../accommodation/model/accommodation.model';
import { Router, ActivatedRoute } from '@angular/router';
import { AccommodationType } from 'src/app/accommodation/model/enum/accommodationType.enum';
import { Amenity } from 'src/app/accommodation/model/enum/amenity.enum';
import {PhotoService} from "../../shared/photo/photo.service";

@Component({
  selector: 'app-search',
  templateUrl: './search.component.html',
  styleUrls: ['./search.component.css', '../../../styles.css']
})
export class SearchComponent implements OnInit {
  isChecked: boolean = false;
  location: string = "";
  fromDate: Date = new Date();
  outDate: Date = new Date();
  guests: number = 0;
  selectedType: string = 'all';
  customBudget: number = 0;
  searchResults: Accommodation[] = [];
  budgetCheckboxIds: string[] = [];
  popularCheckboxIds: string[] = [];
  updateTimeout: any;
  budgetSliderDisabled: boolean = false;
  photoDict: {accId: number, url: string}[] =[];
  name: string = "";
  minFromDate: string;
  nofilterChecked: boolean = false;

  priceTypeGuest: string = 'per guest';
  priceTypeNight: string = 'per night';


  checkboxChanged(event: any, checkboxId: string) {
    console.log("helou");
      const isBudgetFilter = event.target.closest('#budget-filters') !== null;
      const isPopularFilter = event.target.closest('#popular-filters') !== null;

      if (isBudgetFilter) {

        this.budgetCheckboxIds = [];

        const idParts = checkboxId.split('-');
        const minPrice: number = parseFloat(idParts[0]);
        const maxPrice: number = parseFloat(idParts[1]);

        if (event.target.checked) {
          this.budgetCheckboxIds.push(`Min: ${minPrice}, Max: ${maxPrice}`);
        }
      } else if (isPopularFilter) {
        if (event.target.checked) {
            if (!this.popularCheckboxIds.includes(checkboxId)) {
              this.popularCheckboxIds.push(checkboxId);
            }

        } else {
          this.popularCheckboxIds = this.popularCheckboxIds.filter(
            checkbox => checkbox !== checkboxId
          );
        }
      }

      if (this.budgetCheckboxIds.length > 0 || this.popularCheckboxIds.length > 0) {
        this.searchAndFilterAccommodations();
      } else if (this.budgetCheckboxIds.length > 0 || this.popularCheckboxIds.length == 0) {
        this.searchAndFilterAccommodations();
      } else if (this.popularCheckboxIds.length > 0 || this.budgetCheckboxIds.length == 0){
        this.searchAndFilterAccommodations();
      }

  }

  onNameChange(newValue: string): void {
    this.name = newValue;
    this.searchAndFilterAccommodations();
  }


  constructor(private router: Router, private route: ActivatedRoute, private accommodationService: AccommodationService, private el: ElementRef, private photoService: PhotoService) {
    const tomorrow = new Date();
    tomorrow.setDate(tomorrow.getDate() + 1);
    this.minFromDate = this.formatDate(tomorrow);
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
  }

  ngOnInit() {

    this.location = this.route.snapshot.queryParams['location'] || "";
    this.fromDate = this.route.snapshot.queryParams['selectedFromDate'] || new Date();
    this.outDate = this.route.snapshot.queryParams['selectedToDate'] || new Date();
    this.guests = this.route.snapshot.queryParams['guestNumber'] || 0;
    this.searchResults = JSON.parse(this.route.snapshot.queryParams['searchResults']);
    console.log("this is searchResults", this.searchResults);

    this.loadPhotos();

    const parsedFromDate = new Date(this.fromDate);
    const parsedToDate = new Date(this.outDate);

    if (!isNaN(parsedFromDate.getTime()) && !isNaN(parsedToDate.getTime())) {
      this.fromDate = parsedFromDate;
      this.outDate = parsedToDate;
    } else {
      console.error('Invalid date format detected. Check your query parameters.');
      return;
    }

    const noFilterCheckbox = this.el.nativeElement.querySelector('#NofilterCheckbox');
    if (noFilterCheckbox) {
      noFilterCheckbox.addEventListener('change', () => {
        this.nofilterChecked = false;
        this.uncheckAllRadioButtons('budget');
        this.uncheckAllCheckBoxes('amenities');
        this.guests = 0;
        this.location = "";
        this.fromDate = new Date();
        this.outDate = new Date();
        this.setDateHours();
        this.budgetCheckboxIds = [];
        this.popularCheckboxIds = [];
        this.selectedType = "all";
        this.customBudget = 0;
        this.name = "";
        this.searchAndFilterAccommodations();
      });
    }
    }


  onSearchClick(): void {
    this.location = (document.getElementById("locationTxt") as HTMLInputElement).value || "";
    this.guests = parseInt((document.getElementById("guestNumberTxt") as HTMLInputElement).value, 10) || 0;

    const fromDateInput = (document.getElementById("fromDate") as HTMLInputElement);
    const selectedFromDateInputValue = fromDateInput.value;
    this.fromDate = selectedFromDateInputValue ? new Date(selectedFromDateInputValue) : new Date();

    const toDateInput = document.getElementById("toDate") as HTMLInputElement;
    const selectedToDateInputValue = toDateInput.value;
    this.outDate = selectedToDateInputValue ? new Date(selectedToDateInputValue) : new Date();

    this.searchAndFilterAccommodations();
  }

  uncheckAllRadioButtons(groupName: string) {
    const radioButtons = document.querySelectorAll(`input[name=${groupName}]`) as NodeListOf<HTMLInputElement>;

    radioButtons.forEach((radioButton) => {
      radioButton.checked = false;
    });
  }

  uncheckAllCheckBoxes(groupName: string) {
    const checkBoxes = document.querySelectorAll(`input[type="checkbox"][name="${groupName}"]`) as NodeListOf<HTMLInputElement>;

    checkBoxes.forEach((checkBox) => {
      checkBox.checked = false;
    });
  }

  updateBudget(): void {

    if (this.updateTimeout) {
      clearTimeout(this.updateTimeout);
    }
    this.updateTimeout = setTimeout(() => {


      this.searchAndFilterAccommodations();
    }, 500);
  }

  toggleCheckbox() {
    this.isChecked = !this.isChecked;
  }

  changeStyle(className: string): void {
    console.log(className)
    this.selectedType = className;
    this.searchAndFilterAccommodations();
  }

  calculateDayDifference(): number{
    const timeDifference = this.outDate.getTime() - this.fromDate.getTime();
    const dayDifference = timeDifference / (1000 * 3600 * 24);
    return dayDifference;
  }

  searchAndFilterAccommodations() {
    const today = new Date();
    today.setHours(13, 0, 0, 0);
    
    if (
      (this.fromDate.getTime() === today.getTime() && this.outDate.getTime() !== today.getTime()) ||
      (this.fromDate.getTime() !== today.getTime() && this.outDate.getTime() === today.getTime())
    ) {
      alert('Please enter both check-in and check-out dates.');
      return;
    }

    if (this.customBudget > 50) {
      this.budgetCheckboxIds = [];
    }
    const selectedTypeEnum: AccommodationType | null = this.parseAccommodationType(this.selectedType);
    const popular = this.parseAmenities(this.popularCheckboxIds);
    this.setDateHours();
    let minPrice: number = 0.0;
    let maxPrice: number = 0.0;
    if (this.budgetCheckboxIds.length > 0) {
      const idParts = this.budgetCheckboxIds[0].split(',');
      minPrice = parseFloat(idParts[0].replace('Min:', '').trim());
      maxPrice = parseFloat(idParts[1].replace('Max:', '').trim());
      if (Number.isNaN(maxPrice)){
        maxPrice = 100000.0;
      }
    }

    this.accommodationService
      .searchAccommodationsFilters(this.location, this.guests , this.fromDate, this.outDate, popular, minPrice, maxPrice, this.customBudget, selectedTypeEnum, this.name)
      .subscribe(
        (filterResults: Accommodation[]) => {
          console.log('Accommodations:', filterResults);
          this.searchResults = filterResults;
          this.loadPhotos();
        },
        (error) => {
          console.error('Error:', error);
        }
      );
  }
  private parseAccommodationType(typeString: string): AccommodationType | null {
    const enumValues = Object.values(AccommodationType);

    if (enumValues.includes(typeString as AccommodationType)) {
      return typeString as AccommodationType;
    } else {
      console.error(`Nije moguće konvertovati ${typeString} u AccommodationType.`);
      return null;
    }
  }

  private parseAmenities(amenitiesStrings: string[]): Amenity[] | null {
    const amenities: Amenity[] = [];

    for (const str of amenitiesStrings) {
      const amenity = this.parseAmenity(str);

      if (amenity !== null) {
        amenities.push(amenity);
      } else {
        console.error(`Nije moguće konvertovati ${str} u Amenity.`);
        return null;
      }
    }

    return amenities;
  }

  private parseAmenity(amenityString: string): Amenity | null {
    const enumValues = Object.values(Amenity);

    if (enumValues.includes(amenityString as Amenity)) {
      return amenityString as Amenity;
    } else {
      console.error(`Nije moguće konvertovati ${amenityString} u Amenity.`);
      return null;
    }
  }

  generateStars(rating: number | undefined): string[] {
    const stars: string[] = [];
    if (typeof rating === 'number') {
      for (let i = 1; i <= 5; i++) {
        if (i <= rating) {
          stars.push('★');
        } else if (i - 0.5 === rating) {
          stars.push('✯');
        } else {
          stars.push('☆');
        }
      }
    }
    return stars;
  }


  roundHalf(value: number| undefined): number| undefined {
    if(value){
      return Math.round(value * 2) / 2;
    }
    return 0;
  }

  setDateHours(){
    this.fromDate.setHours(13,0,0,0);
    this.outDate.setHours(13,0,0,0);
  }

  navigateToAccommodationDetails(id:number, totalPrice:number): void {
    this.setDateHours();
    const startDateString = this.fromDate.toISOString().split('T')[0];
    const endDateString = this.outDate.toISOString().split('T')[0];
    const days = this.calculateDayDifference();
    this.router.navigate(['/accommodation-details', id], {

      queryParams: { startDate: startDateString, endDate: endDateString, totalPrice: totalPrice, numberGuests: this.guests, days: days},
    });
  }

  loadPhotos() {
    console.log("this is result in load ", this.searchResults);
    this.searchResults.forEach((acc) => {
      this.photoService.loadPhoto(acc.photos[0]).subscribe(
          (data) => {
            this.createImageFromBlob(data).then((url: string) => {
              if(acc.id){
                console.log("this is adding photo: ", acc.name);
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
}




