// accommodation.service.ts

import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Accommodation } from './model/accommodation.model';
import { PriceChange } from './model/priceChange.model';
import { Amenity } from './model/enum/amenity.enum';
import { AccommodationType } from './model/enum/accommodationType.enum';
import {Reservation} from "../reservation/model/reservation.model";

@Injectable({
  providedIn: 'root',
})
export class AccommodationService {
  private apiUrl = 'http://localhost:8080/api/accommodations';

  constructor(private http: HttpClient) {}

  getAllAccommodations(): Observable<Accommodation[]> {
    return this.http.get<Accommodation[]>(this.apiUrl);
  }

  getAllActiveAccommodationsByHostId(hostId: number): Observable<Accommodation[]> {
    const url = `${this.apiUrl}/host/${hostId}/active`;
    return this.http.get<Accommodation[]>(url);
  }

  getAllRequestsByHostId(hostId: number): Observable<Accommodation[]> {
    const url = `${this.apiUrl}/host/${hostId}/requests`;
    return this.http.get<Accommodation[]>(url);
  }

  getAllRejectedByHostId(hostId: number): Observable<Accommodation[]> {
    const url = `${this.apiUrl}/host/${hostId}/rejected`;
    return this.http.get<Accommodation[]>(url);
  }

  getAccommodationById(id: number): Observable<Accommodation> {
    return this.http.get<Accommodation>(`${this.apiUrl}/${id}`);
  }

  createAccommodation(accommodation: Accommodation): Observable<Accommodation> {
    return this.http.post<Accommodation>(this.apiUrl, accommodation);
  }

  updateAccommodation(id: number, accommodation: Accommodation): Observable<Accommodation> {
    return this.http.put<Accommodation>(`${this.apiUrl}/${id}`, accommodation);
  }

  deleteAccommodation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  approveAccommodation(id: number): Observable<Accommodation> {
    return this.http.put<Accommodation>(`${this.apiUrl}/${id}/confirmation`, {});
  }

  rejectAccommodation(id: number): Observable<Accommodation> {
    return this.http.put<Accommodation>(`${this.apiUrl}/${id}/rejection`, {});
  }

  getAllModifiedAccommodations(): Observable<Accommodation[]> {
    return this.http.get<Accommodation[]>(`${this.apiUrl}/modified`);
  }

  getAllCreatedAccommodations(): Observable<Accommodation[]> {
    return this.http.get<Accommodation[]>(`${this.apiUrl}/created`);
  }

  getAllChangedAccommodations(): Observable<Accommodation[]> {
    return this.http.get<Accommodation[]>(`${this.apiUrl}/changed`);
  }

  getMostPopularAccommodations(): Observable<Accommodation[]> {
    return this.http.get<Accommodation[]>(`${this.apiUrl}/mostPopular`);
  }

  searchAccommodationsFilters(
    location?: string,
    guestsNumber?: number,
    startDate?: Date,
    endDate?: Date,
    amenities?: Amenity[] | null,
    minPrice?: number,
    maxPrice?: number,
    customMaxBudget?:number,
    selectedType?:AccommodationType | null,
    name?:string
  ): Observable<Accommodation[]> {
    const params: any = {
      location,
      guestsNumber,
      startDate,
      endDate,
      amenities: amenities || [],
      minPrice: minPrice || 0.0,
      maxPrice: maxPrice || 0.0,
      customMaxBudget: customMaxBudget || 0,
      selectedType: selectedType || null,
      name:name || ""
    };
    return this.http.get<Accommodation[]>(`${this.apiUrl}/search-filters`, { params });
  }
}
