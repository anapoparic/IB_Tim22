// reservation.service.ts

import { Injectable } from '@angular/core';
import {HttpClient, HttpParams} from '@angular/common/http';
import { Observable } from 'rxjs';
import { Reservation } from './model/reservation.model';
import {ReservationStatus} from "./model/reservationStatus.enum";

@Injectable({
  providedIn: 'root',
})
export class ReservationService {
  private apiUrl = 'http://localhost:8080/api/reservations';

  constructor(private http: HttpClient) {}

  getAllReservations(): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(this.apiUrl);
  }

  getReservationById(id: number): Observable<Reservation> {
    return this.http.get<Reservation>(`${this.apiUrl}/${id}`);
  }


  getCreatedReservationsByHostId(hostId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/host/${hostId}/created`);
  }

  getAcceptedReservationsByHostId(hostId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/host/${hostId}/accepted`);
  }

  getRejectedReservationsByHostId(hostId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/host/${hostId}/rejected`);
  }

  getCompletedReservationsByHostId(hostId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/host/${hostId}/completed`);
  }

  getCancelledReservationsByHostId(hostId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/host/${hostId}/cancelled`);
  }

  approveReservation(id: number): Observable<Reservation> {
    return this.http.put<Reservation>(`${this.apiUrl}/${id}/confirmation`, {});
  }

  rejectReservation(id: number): Observable<Reservation> {
    return this.http.put<Reservation>(`${this.apiUrl}/${id}/rejection`, {});
  }

  createReservation(reservation: Reservation): Observable<Reservation> {
    console.log(reservation.accommodation);
    return this.http.post<Reservation>(this.apiUrl, reservation);
  }

  updateReservation(id: number, reservation: Reservation): Observable<Reservation> {
    return this.http.put<Reservation>(`${this.apiUrl}/${id}`, reservation);
  }

  deleteReservation(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getReservationsByStatusAndGuestId(guestId: number, reservationStatus: ReservationStatus): Observable<Reservation[]> {
    const params = new HttpParams()
        .set('reservationStatus', reservationStatus.toString());

    return this.http.get<Reservation[]>(`${this.apiUrl}/guest/${guestId}/filter`, { params });
  }

  getReservationsByGuestId(guestId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/guest/${guestId}`);
  }

  getReservationsByStatusAndHostId(hostId: number, reservationStatus: ReservationStatus): Observable<Reservation[]> {
    const params = new HttpParams()
        .set('reservationStatus', reservationStatus.toString());

    return this.http.get<Reservation[]>(`${this.apiUrl}/host/${hostId}/filter`, { params });
  }

  getReservationsByHostId(hostId: number): Observable<Reservation[]> {
    return this.http.get<Reservation[]>(`${this.apiUrl}/host/${hostId}`);
  }

  cancelReservation(id: number): Observable<Reservation> {
    return this.http.put<Reservation>(`${this.apiUrl}/${id}/cancellation`, {});
  }
}
