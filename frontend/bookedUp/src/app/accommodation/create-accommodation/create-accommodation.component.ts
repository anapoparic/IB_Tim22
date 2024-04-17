import {Component, Host, NgZone, OnInit, ViewChild} from '@angular/core';
import {Router} from '@angular/router';
import {Amenity} from '../model/enum/amenity.enum';
import {AccommodationType} from '../model/enum/accommodationType.enum';
import {CalendarComponent} from 'src/app/shared/calendar/calendar.component';
import {Accommodation} from "../model/accommodation.model";
import {PriceType} from "../model/enum/priceType.enum";
import {AccommodationStatus} from "../model/enum/accommodationStatus.enum";
import {AuthService} from "../../infrastructure/auth/auth.service";
import {HostService} from "../../user/host/host.service";
import {AccommodationService} from "../accommodation.service";
import Swal from "sweetalert2";
import { ChangeDetectorRef } from '@angular/core';
import {DateRange} from "../model/dateRange.model";
import {PriceChange} from "../model/priceChange.model";
import { Observable } from 'rxjs';
import {Photo} from "../../shared/model/photo.model";
import {PhotoService} from "../../shared/photo/photo.service";


@Component({
  selector: 'app-create-accommodation',
  templateUrl: './create-accommodation.component.html',
  styleUrls: ['./create-accommodation.component.css', '../../../styles.css']
})
export class CreateAccommodationComponent implements OnInit {
    @ViewChild('calendarRef') calendarComponent: CalendarComponent | undefined;
    amenitiesList: string[] = [];
    accTypeList: string[] = [];
    accTypeChecked: { [key: string]: boolean } = {};
    perNightChecked: boolean = true;
    perGuestChecked: boolean = false;
    defaultPrice: number = 0;
    minimumPrice: number | undefined;
    maximumPrice: number | undefined;
    cancellation: number | undefined;
    description: string | undefined;
    name: string | undefined;
    addressStreet: string | undefined;
    city: string | undefined;
    postalCode: string | undefined;
    country: string | undefined;
    acceptReservations: boolean = false;
    isInputReadOnly: boolean = false;

    pictureUrls: string[] = [];
    convertedPictureUrls: string[] = [];

    updateProfilePicture: string  = '';

    currentIndex: number = 0;

    customPricesInput: { [date: string]: number } = {};
    customPrice: number = 0;

    priceType: PriceType = PriceType.PerNight;
    accType: AccommodationType = AccommodationType.Apartment;
    accAmenities: Amenity[] = [];
    accPriceChange: PriceChange[] = [];
    copyPriceChange: PriceChange[] = [];

    loggedUser!: Host;

    addedDates: { start: string, end: string }[] = [];
    availability: DateRange[] = [];
    photos: Photo[] = [];

    constructor(private router: Router, private hostService: HostService, private authService: AuthService, private accommodationService: AccommodationService, private cdr: ChangeDetectorRef, private zone: NgZone, private photoService:PhotoService) {
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

        this.hostService.getHost(this.authService.getUserID()).subscribe(
            (host: Host) => {
                this.loggedUser = host;
            },
            (error) => {
                console.error('Error loading user:', error);
            }
        );
    }

    convertBlobToFile(blobUrl: string): Promise<File> {
        return fetch(blobUrl)
            .then(response => response.blob())
            .then(blob => new File([blob], `acc${Date.now()}.png`, {type: blob.type}));
    }

    handlePerNightChange() {
        if (this.perNightChecked) {
            this.perGuestChecked = false;
            this.priceType = PriceType.PerNight;
        }
    }

    handlePerGuestChange() {
        if (this.perGuestChecked) {
            this.perNightChecked = false;
            this.priceType = PriceType.PerGuest;
        }
    }

    handleAccTypeChange(selectedAccType: string): void {
        this.accType = AccommodationType[selectedAccType as keyof typeof AccommodationType];
        Object.keys(this.accTypeChecked).forEach(accType => {
            if (accType !== selectedAccType) {
                this.accTypeChecked[accType] = false;
            }
        });
    }

    transformEnumToDisplayFormat(enumValue: string): string {
        const words = enumValue.split('_');
        const formattedString = words.map(word => word.charAt(0).toUpperCase() + word.slice(1).toLowerCase()).join(' ');
        return formattedString;
    }


    applyCustomPrice(): void {
        if (this.isInputReadOnly == false) {
            Swal.fire({
                title: 'Default Price Changed!',
                text: 'You can no longer change it in this window.',
                icon: 'warning',
                showCancelButton: true,
                confirmButtonText: 'OK',
                cancelButtonText: 'Cancel',
            }).then((result) => {
                if (result.isConfirmed) {
                    if (this.defaultPrice != 0) {
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
                    } else {
                        alert("You didn't input default price. Please provide a default price before entering a custom one.");
                        return;
                    }
                } else if (result.dismiss === Swal.DismissReason.cancel) {
                    return;
                }
            });
        } else {
            if (this.defaultPrice != 0) {
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
            } else {
                alert("You didn't input default price. Please provide a default price before entering a custom one.");
                return;
            }
        }


    }


    saveAccommodation(): void {

        console.log(this.acceptReservations);
        if (this.validation()) {
            // this.uploadImagesToResources();

            const accommodation: Accommodation = {
                name: this.name || '',
                description: this.description || '',
                address: {
                    country: this.country || '',
                    city: this.city || '',
                    postalCode: this.postalCode || '',
                    streetAndNumber: this.addressStreet || '',
                    latitude: 0, //??
                    longitude: 0 //??
                },
                amenities: this.accAmenities,
                photos: this.photos = this.convertedPictureUrls.map(url => ({
                    url: url,
                    caption: '',
                    active: true
                })),//??
                // photos: [],
                minGuests: this.minimumPrice || 0,
                maxGuests: this.maximumPrice || 0,
                type: this.accType,
                availability: this.availability = this.addedDates.map(addedDate => ({
                    startDate: new Date(addedDate.start),
                    endDate: new Date(addedDate.end),
                })),
                priceType: this.priceType,
                priceChanges: this.copyPriceChange,//
                automaticReservationAcceptance: this.acceptReservations,
                status: AccommodationStatus.Created,
                host: this.loggedUser,
                price: this.defaultPrice,
                cancellationDeadline: this.cancellation || 0,
            };

            this.copyPriceChange.sort((a, b) => new Date(a.changeDate).getTime() - new Date(b.changeDate).getTime());

            // console.log('Posle sortiranja:', this.copyPriceChange);
            // Swal.fire({icon: 'success', title: 'Accommodation created successfully!', text: 'You will be redirected to the home page.',}).then(() => {
            //            //this.router.navigate(['/']);
            //          });

            this.accommodationService.createAccommodation(accommodation).subscribe(
                (createdAccommodation: Accommodation) => {
                    Swal.fire({icon: 'success', title: 'Accommodation created successfully!', text: 'You will be redirected to the home page.',}).then(() => {
                        this.router.navigate(['/']);
                    });
                },
                (error) => {
                    Swal.fire({icon: 'error', title: 'Error creating accommodation', text: 'Please try again.',});
                }
            );

        }

    }

    private validation() {
        if (!this.name || !this.description || !this.addressStreet || !this.city || !this.postalCode || !this.country ||
            this.name.trim() === '' || this.description.trim() === '' || this.addressStreet.trim() === '' || this.city.trim() === '' || this.postalCode.trim() === '' || this.country.trim() === '' ||
            this.defaultPrice <= 0 ||
            (this.minimumPrice !== undefined && (this.minimumPrice <= 0 ||
                (this.maximumPrice !== undefined && this.minimumPrice > this.maximumPrice))) ||
            (this.maximumPrice !== undefined && this.maximumPrice <= 0) ||
            (this.cancellation !== undefined && this.cancellation <= 0) ||
            // (this.pictureUrls.length == 0 ) ||
            (this.addedDates.length == 0) ||
            Object.values(this.accTypeChecked).every(value => value === false)) {
            Swal.fire({
                icon: 'error',
                title: 'Invalid Input',
                text: 'All properties must have a valid value. Please check and enter valid information for all fields.',
            });
            return false;
        }
        return true;
    }

    toggleAmenity(amenity: string): void {
        const newAmenity: string = amenity.replace(/\s+/g, '');
        const amenityEnum = Amenity[newAmenity as keyof typeof Amenity];

        if (this.accAmenities.includes(amenityEnum)) {
            this.accAmenities = this.accAmenities.filter(a => a !== amenityEnum);
        } else {
            this.accAmenities.push(amenityEnum);
        }
    }


    addDateRange() {
        const selectedDates = this.calendarComponent?.getSelectedRange();

        if (selectedDates?.start !== null && selectedDates?.end !== null && selectedDates?.hasAlreadyPicked) {
            this.addedDates.push({start: selectedDates.start, end: selectedDates.end});
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
                if (currentEndDate >= nextEndDate) {
                    continue;
                } else {
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

    uploadNewImage(): void {
        const fileInput = document.getElementById('fileInput') as HTMLInputElement;
        fileInput.click();
    }

    handleFileInputChange(event: Event): void {
        const inputElement = event.target as HTMLInputElement;

        if (inputElement.files && inputElement.files.length > 0) {
            const file = inputElement.files[0];

            const imageUrl = URL.createObjectURL(file);
            // this.convertedPictureUrls.push(imageUrl);

            this.convertBlobToFile(imageUrl??'')
                .then(file => {
                    console.log('Converted file:', file);

                    // Now you can upload the file or use it as needed.
                    this.photoService.uploadImage(file).subscribe(
                        response => {
                            console.log('Image uploaded successfully:', response);
                            this.updateProfilePicture = 'images/'+file.name;
                            console.log("ovo je naziv koji se prosledjuje ", this.updateProfilePicture);
                            // Handle success as needed
                            this.convertedPictureUrls.push(this.updateProfilePicture);

                        },
                        error => {
                            console.error('Error uploading image:', error);
                            // Handle error as needed
                        }
                    );
                })
                .catch(error => {
                    console.error('Error converting blob to file:', error);
                });

            this.pictureUrls.push(imageUrl);

            // Update currentIndex to point to the newly added image
            this.currentIndex = this.pictureUrls.length - 1;

            inputElement.value = '';
        }
    }


    deleteImage(): void {
        if (this.currentIndex >= 0 && this.currentIndex < this.pictureUrls.length) {
            this.pictureUrls.splice(this.currentIndex, 1);
            if (this.currentIndex >= this.pictureUrls.length) {
                this.currentIndex = this.pictureUrls.length - 1;
            }
        } else {
            console.error('Invalid currentIndex value');
        }

        if (this.currentIndex >= 0 && this.currentIndex < this.convertedPictureUrls.length) {
            this.convertedPictureUrls.splice(this.currentIndex, 1);
            if (this.currentIndex >= this.convertedPictureUrls.length) {
                this.currentIndex = this.convertedPictureUrls.length - 1;
            }
        } else {
            console.error('Invalid currentIndex value');
        }
    }

    private updateCopyPriceChange(): void {
        if (this.accPriceChange.length % 2 == 0) {
            const lastTwoChanges = this.accPriceChange.slice(-2); // Poslednja dva elementa

            if (this.copyPriceChange.length === 0) {
                // Ako je copyPriceChange prazan, kopiraj poslednja dva elementa iz accPriceChange
                this.copyPriceChange = [...lastTwoChanges];
            } else {
                // Ako nije prazan, ažuriraj ga na osnovu poslednja dva elementa iz accPriceChange
                const lastTwoCopyChanges = this.copyPriceChange.slice(-2);

                const startDate = lastTwoChanges[0].changeDate;
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
                    {changeDate: startDate, newPrice: lastTwoChanges[0].newPrice},
                    {changeDate: endDate, newPrice: lastTwoChanges[1].newPrice}
                ];


            }
        }
    }

    // private async uploadImagesToResources() {
    //     for (const imageUrl of this.convertedPictureUrls) {
    //         try {
    //             const file = await this.convertBlobToFile(imageUrl);
    //             console.log('Converted file:', file);
    //
    //             // Now you can upload the file or use it as needed.
    //             this.photoService.uploadImage(file).subscribe(
    //                 response => {
    //                     console.log('Image uploaded successfully:', response);
    //                     this.updateProfilePicture = 'images/' + file.name;
    //                     console.log("Ovo je naziv koji se prosleđuje: ", this.updateProfilePicture);
    //                     // Handle success as needed
    //                 },
    //                 error => {
    //                     console.error('Error uploading image:', error);
    //                     // Handle error as needed
    //                 }
    //             );
    //         } catch (error) {
    //             console.error('Error converting blob to file:', error);
    //         }
    //     }
    // }



}
