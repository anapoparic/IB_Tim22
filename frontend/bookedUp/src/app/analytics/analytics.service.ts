import { Injectable } from '@angular/core';
import { Observable, map, of, switchMap } from 'rxjs';
import { AccommodationService } from '../accommodation/accommodation.service';
import { ReservationService } from '../reservation/reservation.service';
import { ReservationStatus } from '../reservation/model/reservationStatus.enum';

interface AnalyticsData {
  name: string;
  totalEarnings: number;
  totalReservations: number;
}

interface SingleAnalyticsData {
  month: string;
  totalEarnings: number;
  totalReservations: number;
}

@Injectable({
  providedIn: 'root'
})
export class AnalyticsService {

  

  constructor( private accommodationService: AccommodationService, private reservationService: ReservationService ){}

  getAllAnalytics(startDate: string, endDate: string, hostId: number): Observable<AnalyticsData[]> {
    return this.accommodationService.getAllActiveAccommodationsByHostId(hostId).pipe(
      switchMap((accommodations) => {
        return this.reservationService.getReservationsByStatusAndHostId(hostId, ReservationStatus.Completed).pipe(
          map((completedReservations) => {
            const analyticsData: AnalyticsData[] = accommodations.map(accommodation => {
              const matchingReservations = completedReservations.filter(reservation => 
                reservation.accommodation.name === accommodation.name &&
                this.isDateInRange(this.formatDate(new Date(reservation.startDate)), startDate, endDate) &&
                this.isDateInRange(this.formatDate(new Date(reservation.endDate)), startDate, endDate)
              );
  
              console.log("This is acc ", accommodation, " and this profit, total: ", matchingReservations);
              const totalEarnings = matchingReservations.reduce((sum, reservation) => sum + reservation.totalPrice, 0);
              const totalReservations = matchingReservations.length;
  
              return {
                name: accommodation.name,
                totalEarnings,
                totalReservations
              };
            });
  
            return analyticsData;
          })
        );
      })
    );
  }
  
  getSingleAnalytics(startDate: string, endDate: string, hostId: number, accId: number): Observable<SingleAnalyticsData[]> {
    return this.reservationService.getReservationsByStatusAndHostId(hostId, ReservationStatus.Completed).pipe(
      map((completedReservations) => {
        const analyticsData: SingleAnalyticsData[] = [];
  
        // Iterate over each month in the year from startDate to endDate
        const currentDate = new Date(startDate);
        const end = new Date(endDate);
        while (currentDate < end) {
          const monthStartDate = new Date(currentDate.getFullYear(), currentDate.getMonth(), 1);
          const monthEndDate = new Date(currentDate.getFullYear(), currentDate.getMonth() + 1, 0);

          const matchingReservations = completedReservations.filter(reservation =>
            reservation.accommodation.id === accId &&
            this.isDateInRange(this.formatDate(new Date(reservation.startDate)), monthStartDate.toISOString(), monthEndDate.toISOString())
          );

          const totalEarnings = matchingReservations.reduce((sum, reservation) => sum + reservation.totalPrice, 0);
          const totalReservations = matchingReservations.length;

          analyticsData.push({
            month: currentDate.toLocaleString('en-US', { month: 'long', year: 'numeric' }),
            totalEarnings,
            totalReservations
          });

          currentDate.setMonth(currentDate.getMonth() + 1);
        }
        
        return analyticsData;
      })
    );
  }
  

  private isDateInRange(date: string, startDate: string, endDate: string): boolean {
    const parsedDate = new Date(date);
    const parsedStartDate = new Date(startDate);
    const parsedEndDate = new Date(endDate);

    console.log("this is range ", date, "result: ", parsedDate >= parsedStartDate && parsedDate <= parsedEndDate);
    return parsedDate >= parsedStartDate && parsedDate <= parsedEndDate;
  }


  private formatDate(date: Date): string {
    const year = date.getFullYear();
    const month = ('0' + (date.getMonth() + 1)).slice(-2);
    const day = ('0' + date.getDate()).slice(-2);
    return `${year}-${month}-${day}`;
  }
}
