// src/app/calendar/calendar.component.ts

import { Component, Input, OnChanges, OnInit, SimpleChanges } from '@angular/core';
import { AccommodationService } from 'src/app/accommodation/accommodation.service';
import { DateRange } from 'src/app/accommodation/model/dateRange.model';
import { PriceChange } from 'src/app/accommodation/model/priceChange.model';
import { AuthService } from 'src/app/infrastructure/auth/auth.service';

@Component({
  selector: 'app-calendar',
  templateUrl: './calendar.component.html',
  styleUrls: ['./calendar.component.css', '../../../styles.css'],
})

export class CalendarComponent implements OnChanges {
  @Input() defaultPrice: number = 0;
  private isFirstChange = true;
  @Input() customPricesInput: PriceChange[] | null = [];
  customPrices: { [date: string]: number } = { };
  @Input() alreadyPickedInput: DateRange[] | null = [];
  alreadyPicked: { [date: string]: string } = { };
  @Input() startDate: string | null = null;
  @Input() endDate: string | null = null;
  selectedRange: { start: number | null; startMonth: number | null; startYear: number | null; end: number | null ; endMonth: number | null; endYear: number | null; } = { start: null, startMonth: null, startYear: null, end: null, endMonth: null, endYear: null };

  displayedMonth: number;
  displayedYear: number;
  userRole : string = '';

  // Initialize calendarDates with the days of the selected month
  calendarDates: { day: number; month: number; year:number; price: number; selected: boolean }[] = [];

  constructor(private accommodationService: AccommodationService, private authService: AuthService) {
    const currentDate = new Date();
    this.displayedMonth = currentDate.getMonth() + 1; // Months are zero-based
    this.displayedYear = currentDate.getFullYear();
    this.generateCalendar();
  }

  ngOnInit(){
    this.userRole = this.authService.getRole();
    
    if(this.startDate !== null && this.endDate !== null){
      const parsedDateStart = new Date(this.startDate);
      const parsedDateEnd = new Date(this.endDate);

      const yearStart = parsedDateStart.getFullYear();
      const monthStart = parsedDateStart.getMonth() + 1; // Months are zero-indexed, so add 1
      const dayStart = parsedDateStart.getDate();

      const yearEnd = parsedDateEnd.getFullYear();
      const monthEnd = parsedDateEnd.getMonth() + 1;
      const dayEnd = parsedDateEnd.getDate();

      this.displayedYear = yearStart;
      this.displayedMonth = monthStart;

      this.selectedRange.start = dayStart;
      this.selectedRange.startMonth = monthStart;
      this.selectedRange.startYear = yearStart;
      this.selectedRange.end = dayEnd;
      this.selectedRange.endMonth = monthEnd;
      this.selectedRange.endYear = yearEnd;
    }
    if(this.alreadyPickedInput !== null){
      this.alreadyPicked = this.getAlreadyPicked(this.alreadyPickedInput);
    }
    if(this.customPricesInput !== null){
      this.customPrices = this.getCustomDefaulted(this.customPricesInput);
    }

    this.generateCalendar();
  }

  getCustomDefaulted(priceList: PriceChange[]): { [date: string]: number } {
    var customPrices: { [date: string]: number } = {};
  
    for (var i = 0; i < priceList.length; i++) {
      var priceChange = priceList[i];
      var dateString: string = priceChange.changeDate.toString().split('T')[0];
      var nextDateString: string | undefined;
  
      if (i < priceList.length - 1) {
        nextDateString = priceList[i + 1].changeDate.toString().split('T')[0];
      }
  
      customPrices[dateString] = priceChange.newPrice;
  
      // Fill in intermediate dates within the range
      if (nextDateString) {
        var currentDate = new Date(dateString);
        var nextDate = new Date(nextDateString);
  
        while (currentDate < nextDate) {
          currentDate.setDate(currentDate.getDate() + 1);
          customPrices[currentDate.toISOString().split('T')[0]] = priceChange.newPrice;
        }
      }
  
      // If it's the last date, extend for an additional 30 days with the last known price
      if (i === priceList.length - 1) {
        var lastDate = new Date(dateString);
        for (var j = 0; j < 30; j++) {
          lastDate.setDate(lastDate.getDate() + 1);
          customPrices[lastDate.toISOString().split('T')[0]] = priceChange.newPrice;
        }
      }
    }
  
    return customPrices;
  }

  getCustom(priceList: PriceChange[]): { [date: string]: number } {
    var customPrices: { [date: string]: number } = {};

    for (var i = 0; i < priceList.length; i++) {
      var priceChange = priceList[i];
      var dateString: string = priceChange.changeDate.toISOString().split('T')[0];
      var nextDateString: string | undefined;

      if (i < priceList.length - 1) {
        nextDateString = priceList[i + 1].changeDate.toISOString().split('T')[0];
      }

      customPrices[dateString] = priceChange.newPrice;

      // Fill in intermediate dates within the range
      if (nextDateString) {
        var currentDate = new Date(dateString);
        var nextDate = new Date(nextDateString);

        while (currentDate < nextDate) {
          currentDate.setDate(currentDate.getDate() + 1);
          customPrices[currentDate.toISOString().split('T')[0]] = priceChange.newPrice;
        }
      }

      if (i === priceList.length - 1) {
        var lastDate = new Date(dateString);
        customPrices[lastDate.toISOString().split('T')[0]] = priceChange.newPrice;
      }
    }

    return customPrices;
  }


  getAlreadyPicked(dateRanges: DateRange[]): { [date: string]: string } {
    var alreadyPicked: { [date: string]: string } = {};

    dateRanges.forEach((range) => {
      var fromDate: string = range.startDate.toString().split('T')[0];
      var toDate: string = range.endDate.toString().split('T')[0];
      alreadyPicked[fromDate] = toDate;
    });

    return alreadyPicked;
  }

  ngOnChanges(changes: SimpleChanges): void {
    if (changes['defaultPrice'] && !changes['defaultPrice'].firstChange) {
      if (this.isFirstChange) {
        this.generateCalendar();
        
      }
    }
    if (changes['customPricesInput'] && !changes['customPricesInput'].firstChange){
      if(this.customPricesInput){
        this.isFirstChange = false;
        this.customPrices = this.getCustom(this.customPricesInput);
        this.generateCalendar();
      }
    }
  }

  handleDateClick(day: number): void {
    const currentDate = new Date();
    const today = {
      day: currentDate.getDate(),
      month: currentDate.getMonth() + 1,
      year: currentDate.getFullYear(),
    };
  
    if (this.isDateInPast(day, today)) {
      // Don't allow selection of past dates
      return;
    }
  
    if (this.selectedRange.start === null || this.selectedRange.end !== null) {
      // Uneven click (1st, 3rd, 5th click, etc.) or resetting the range
      this.selectedRange.start = day;
      this.selectedRange.startMonth = this.displayedMonth;
      this.selectedRange.startYear = this.displayedYear;
      this.selectedRange.end = null;
    } else {
      // Even click (2nd, 4th, 6th click, etc.)
      this.selectedRange.end = day;
      this.selectedRange.endMonth = this.displayedMonth;
      this.selectedRange.endYear = this.displayedYear;
    }
  
    this.updateSelectedStyles();
  }

  changeMonth(delta: number): void {
    const previousMonth = this.displayedMonth;
    this.displayedMonth += delta;
  
    // Ako je mesec manji od 1, postavite ga na 12 (decembar)
    if (this.displayedMonth < 1) {
      this.displayedMonth = 12;
      this.displayedYear--;
    }
  
    // Ako je mesec veći od 12, postavite ga na 1 (januar)
    if (this.displayedMonth > 12) {
      this.displayedMonth = 1;
      this.displayedYear++;
    }
  
    // Check if the displayed month is part of the current selection range
    const isSameSelectionRange =
      this.selectedRange &&
      ((this.displayedYear === this.selectedRange.startYear &&
       previousMonth >= (this.selectedRange.startMonth ?? 0)) ||
      (this.displayedYear === this.selectedRange.endYear &&
       previousMonth <= (this.selectedRange.endMonth ?? 0)));
  
    if (isSameSelectionRange) {
      // Retain the start month if it's part of the same selection range
      this.selectedRange.startMonth = previousMonth;
    } else {
      // Reset the selected range if the displayed month is not part of the same selection range
     // Postavite na odgovarajuću vrednost, možda null ili default vrednost
    }
  
    this.generateCalendar();
  }
  
  
  
 // Change the selected year and update the calendar
  changeYear(delta: number): void {
    this.displayedYear += delta;

    // Check if the displayed year is part of the current selection range
    const isSameSelectionRange =
      this.selectedRange &&
      ((this.displayedYear === this.selectedRange.startYear && this.displayedMonth >= (this.selectedRange.startMonth ?? 0)) ||
      (this.displayedYear === this.selectedRange.endYear && this.displayedMonth <= (this.selectedRange.endMonth ?? 0)));

    if (isSameSelectionRange) {
      // Retain the start and end years if they are part of the same selection range
      this.selectedRange.startYear = this.displayedYear;
      this.selectedRange.endYear = this.displayedYear;
    } 

    this.generateCalendar();
  }
  
  isDateInPast(day: number, today: { day: number, month: number, year: number }): boolean {
    return (
      this.displayedYear < today.year ||
      (this.displayedYear === today.year && this.displayedMonth < today.month) ||
      (this.displayedYear === today.year && this.displayedMonth === today.month && day < today.day)
    );
  }
  
  // Generate the calendar for the selected month and year
  generateCalendar(): void {
    console.log("ovo je range ", this.selectedRange);
    const firstDay = new Date(this.displayedYear, this.displayedMonth - 1, 1).getDay(); // 0-indexed
    const daysInMonth = new Date(this.displayedYear, this.displayedMonth, 0).getDate();

    this.calendarDates = [];

    // Fill in the days before the 1st day of the month with a placeholder
    for (let i = 0; i < firstDay; i++) {
      this.calendarDates.push({ day: 0, month: 0, year: 0, price: 0, selected: false });
    }

    // Fill in the actual days of the month
    for (let day = 1; day <= daysInMonth; day++) {
      const dateKey = `${this.displayedYear}-${this.displayedMonth.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
      this.calendarDates.push({
        day,
        month: this.displayedMonth,
        year: this.displayedYear,
        price: this.customPrices[dateKey] !== undefined ? this.customPrices[dateKey] : this.defaultPrice,
        selected: false,
      });
    }

    this.updateSelectedStyles();
  }
  
  updateSelectedStyles(): void {
    // Reset all selected styles
    this.calendarDates.forEach((date) => {
      date.selected = false;
    });
  
    // Mark and select dates in the range between start and end dates
    if (this.selectedRange.start !== null) {
      // Mark only the start date
      const year = this.selectedRange.startYear ?? this.displayedYear;
      const month = this.selectedRange.startMonth ?? this.displayedMonth;
      const day = this.selectedRange.start;
      const startKey = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
      const startDate = new Date(this.selectedRange.startYear!, this.selectedRange.startMonth! - 1, this.selectedRange.start);
  
      this.calendarDates.forEach((date) => {
        const currentDate = new Date(date.year, date.month - 1, date.day);
  
        if (currentDate.getTime() === startDate.getTime()) {
          date.selected = true;
        }
      });
    }
  
    if (this.selectedRange.start !== null && this.selectedRange.end !== null) {
      const startDate = new Date(this.selectedRange.startYear!, this.selectedRange.startMonth! - 1, this.selectedRange.start);
      const endDate = new Date(this.selectedRange.endYear!, this.selectedRange.endMonth! - 1, this.selectedRange.end);
  
      const startMonth = this.selectedRange.startMonth!;
      const endMonth = this.selectedRange.endMonth!;
      const startYear = this.selectedRange.startYear!;
      const endYear = this.selectedRange.endYear!;
  
      this.calendarDates.forEach((date) => {
        const currentDate = new Date(date.year, date.month - 1, date.day);
  
        if (
          (currentDate >= startDate && currentDate <= endDate) ||
          ((startMonth !== endMonth) &&
            ((date.year === startYear && date.month === startMonth && date.day >= this.selectedRange.start!) ||
              (date.year === endYear && date.month === endMonth && date.day <= this.selectedRange.end!)))
        ) {
          date.selected = true;
        }
      });
    }
  }
  

  updateDefaultPrice(newDefaultPrice: number, changeDate: string): void {

    // Update the default price for the specific date change
    this.customPrices[changeDate] = newDefaultPrice;

    // Update default price for all subsequent dates
    let foundChangeDate = false;
    for (const dateKey in this.customPrices) {
      if (foundChangeDate) {
        this.customPrices[dateKey] = newDefaultPrice;
      }

      if (dateKey === changeDate) {
        foundChangeDate = true;
      }
    }
  }


  // Get the name of the month based on its number (1-indexed)
  getMonthName(monthNumber: number): string {
    const months = [
      'January', 'February', 'March', 'April', 'May', 'June',
      'July', 'August', 'September', 'October', 'November', 'December'
    ];
    return months[monthNumber - 1];
  }

  // Get the abbreviated day name based on the day index (0-based)
  getDayAbbreviation(dayIndex: number): string {
    const days = ['Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat'];
    return days[dayIndex];
  }

  isDateAlreadyPicked(day: { day: number }): boolean {
    if(day.day != 0 && Object.keys(this.alreadyPicked).length != 0){
      
      const dateKey = this.getDateKey(day);

      // Check if the date falls within any already picked range
      return Object.entries(this.alreadyPicked).some(([start, end]) => {
        const startDate = new Date(start);
        const endDate = new Date(end);
        const currentDate = new Date(dateKey);

        return currentDate >= startDate && currentDate <= endDate;
      });
    }else{
      return true;
    }

  }
  
  getDateKey(dateInfo: { day: number, month?: number, year?: number }): string {
    const year = dateInfo.year ?? this.displayedYear;
    const month = dateInfo.month ?? this.displayedMonth;
    const day = dateInfo.day;
  
    return `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
  }
  

  getSelectedRange(): { start: string | null; end: string | null; hasAlreadyPicked: boolean } {
    if (this.selectedRange.start !== null && this.selectedRange.end !== null) {

      const year = this.selectedRange.startYear ?? this.displayedYear;
      const month = this.selectedRange.startMonth ?? this.displayedMonth;
      const day = this.selectedRange.start;
      const startKey = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
      
      const yearEnd = this.selectedRange.endYear ?? this.displayedYear;
      const monthEnd = this.selectedRange.endMonth ?? this.displayedMonth;
      const dayEnd = this.selectedRange.end;
      const endKey = `${yearEnd}-${monthEnd.toString().padStart(2, '0')}-${dayEnd.toString().padStart(2, '0')}`;
  
      // Check if there are already picked dates within the selected range
      const hasAlreadyPicked = Object.entries(this.alreadyPicked).some(([start, end]) => {
        const startDate = new Date(start);
        const endDate = new Date(end);
        const selectedStartDate = new Date(startKey);
        const selectedEndDate = new Date(endKey);
  
        // Check if the selected range overlaps with the already picked range
        return (
          (selectedStartDate <= endDate && selectedEndDate >= startDate)
        );
      });
  
      return { start: startKey, end: endKey, hasAlreadyPicked: !hasAlreadyPicked };
    } else if (this.selectedRange.start !== null) {
      const year = this.selectedRange.startYear ?? this.displayedYear;
      const month = this.selectedRange.startMonth ?? this.displayedMonth;
      const day = this.selectedRange.start;
      const startKey = `${year}-${month.toString().padStart(2, '0')}-${day.toString().padStart(2, '0')}`;
  
      // Check if the start date is already picked
      const hasAlreadyPicked = Object.entries(this.alreadyPicked).some(([start, end]) => {
        const startDate = new Date(start);
        const endDate = new Date(end);
        const selectedStartDate = new Date(startKey);
  
        return selectedStartDate >= startDate && selectedStartDate <= endDate;
      });
  
      return { start: startKey, end: null, hasAlreadyPicked: !hasAlreadyPicked };
    } else {
      return { start: null, end: null, hasAlreadyPicked: true };
    }
  }
  



}
