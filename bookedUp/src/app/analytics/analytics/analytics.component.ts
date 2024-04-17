import { Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { AccommodationService } from 'src/app/accommodation/accommodation.service';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';
import { YearlyAnalyticsComponent } from '../yearly-analytics/yearly-analytics.component';
import jsPDF from 'jspdf';
import { UserService } from 'src/app/user/user.service';
import { SingleAccommodationAnalyticsComponent } from '../single-accommodation-analytics/single-accommodation-analytics.component';

@Component({
  selector: 'app-analytics',
  templateUrl: './analytics.component.html',
  styleUrls: ['./analytics.component.css']
})
export class AnalyticsComponent implements OnInit{
  @ViewChild(YearlyAnalyticsComponent) yearlyAnalyticsComponent: YearlyAnalyticsComponent | undefined;
  @ViewChild(SingleAccommodationAnalyticsComponent) singleAccommodationAnalyticsComponent: SingleAccommodationAnalyticsComponent | undefined;

  accommodations: any[] = [];
  selectedAccommodation: string = '';
  selectedAccommodationId: number = 0;
  isDropdownVisible = false;

  types: string[] = ['All Accommodations', 'Single Accommodation'];
  selectedType: string = 'All Accommodations';
  isDropdownTypeVisible = false;

  hostName: string = '';

  startDate: string = '';
  startDateInput: Date | null = null;
  endDateInput: Date | null = null;
  endDate: string = '';
  
  constructor( private accommodationService: AccommodationService, private authService: AuthService, private userService: UserService){}

  ngOnInit(): void {
    
    this.accommodationService.getAllActiveAccommodationsByHostId(this.authService.getUserID()).subscribe(
      (result) => {
        this.accommodations = result.map(acc => ({ 'name': acc.name, 'id': acc.id }));
      },
      (error) => {
        console.error('Error fetching accommodations:', error);
      }
    );      

    this.userService.getUser(this.authService.getUserID()).subscribe(
      (result) => {
        this.hostName = `${result.firstName} ${result.lastName}`;
      },
      (error) => {
        console.error('Error fetching host name:', error);
      }
    );

    const endDate = new Date();
    const oneYearAgo = new Date();
    oneYearAgo.setFullYear(oneYearAgo.getFullYear() - 1);

    this.endDate = this.formatDate(endDate);
    this.startDate = this.formatDate(oneYearAgo);
  }

  reloadChart(){
    if (this.selectedType === 'All Accommodations') {
      if (this.startDateInput != null && this.endDateInput != null && this.startDateInput < this.endDateInput && this.yearlyAnalyticsComponent) {
        if (this.yearlyAnalyticsComponent) {
          this.yearlyAnalyticsComponent.startDate = this.startDate;
          this.yearlyAnalyticsComponent.endDate = this.endDate;
          this.yearlyAnalyticsComponent.getAnalytics();
        }
      }
    } else {
      if (this.selectedAccommodationId != 0 && this.singleAccommodationAnalyticsComponent) {
        if (this.singleAccommodationAnalyticsComponent) {
          if(this.startDateInput != null){
            const year = this.startDate.split('-')[0];
            this.singleAccommodationAnalyticsComponent.startDate = `${year}-01-01`;
            this.singleAccommodationAnalyticsComponent.endDate = `${year}-12-31`;
          }else if(this.endDateInput != null){
            const year = this.endDate.split('-')[0];
            this.singleAccommodationAnalyticsComponent.startDate = `${year}-01-01`;
            this.singleAccommodationAnalyticsComponent.endDate = `${year}-12-31`;
          }
          this.singleAccommodationAnalyticsComponent.accommodationId = this.selectedAccommodationId;
          this.singleAccommodationAnalyticsComponent.getAnalytics();
        }
      }
    }
  }

  startDateChanged() {
    if(this.startDateInput != null){
      const fromDateInput = document.getElementById("startDate") as HTMLInputElement;
      const selectedFromDateInputValue = fromDateInput.value;
      const selectedFromDate = selectedFromDateInputValue ? new Date(selectedFromDateInputValue) : new Date();

      this.startDate = this.formatDate(selectedFromDate);
    }    
  }

  endDateChanged() {
    if (this.endDateInput != null) {
      const toDateInput = document.getElementById("endDate") as HTMLInputElement;
      const selectedToDateInputValue = toDateInput.value;
      const selectedToDate = selectedToDateInputValue ? new Date(selectedToDateInputValue) : new Date();

      this.endDate = this.formatDate(selectedToDate);
    }
  }
  

  toggleDropdown() {
    this.isDropdownVisible = true;
  }

  selectAccommodation(selectedAccommodation: { 'name': string, 'id': number }) {
    this.isDropdownVisible = false;
    this.selectedAccommodation = selectedAccommodation.name;
    this.selectedAccommodationId = selectedAccommodation.id;
  }
  
  toggleTypeDropdown() {
    this.isDropdownTypeVisible = !this.isDropdownTypeVisible; // Toggle the visibility
  }  

  selectType(name: string) {
    this.isDropdownTypeVisible = false;
    this.selectedType = name;
  }

  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
  }

  printingPDF() {
    if (!this.selectedType) {
      console.error('Selected type is required.');
      return;
    }else if(this.selectedType === 'All Accommodations'){
      this.yearlyAnalyticsComponent?.exportToPDF();
    }else{
      this.singleAccommodationAnalyticsComponent?.exportToPDF();
    }
  }


  private formatDatePDF(dateString: string): string {
      const options: Intl.DateTimeFormatOptions = { year: 'numeric', month: 'long', day: 'numeric' };
      return new Date(dateString).toLocaleDateString('en-US', options);
  }
  
}
