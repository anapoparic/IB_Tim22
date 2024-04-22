import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Review } from './model/review.model';

@Injectable({
  providedIn: 'root'
})
export class ReviewService {
  private apiUrl = 'http://localhost:8080/api/reviews';

  constructor(private http: HttpClient) { }

  getReviews(): Observable<Review[]> {
    return this.http.get<Review[]>(this.apiUrl);
  }

  getReview(id: number): Observable<Review> {
    return this.http.get<Review>(`${this.apiUrl}/${id}`);
  }

  getAccommodationReviews(id: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/accommodation/${id}`);
  }

  getGuestReviews(guestId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/guest/${guestId}`);
  }

  getGuestAccommodationReviews(guestId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/guest/${guestId}/accommodation`);
  }

  getGuestHostReviews(guestId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/guest/${guestId}/host`);
  }

  createReview(review: Review): Observable<Review> {
    return this.http.post<Review>(this.apiUrl, review);
  }

  updateReview(id: number, review: Review): Observable<Review> {
    return this.http.put<Review>(`${this.apiUrl}/${id}`, review);
  }

  deleteReview(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getAccommodationReviewsByHostId(hostId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/host/${hostId}/accommodation`);
  }

  getHostReviewsByHostId(hostId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/host/${hostId}/host`);
  }

  getReviewsByHostId(hostId: number): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/host/${hostId}`);
  }

  getUnapprovedReviews(): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/unapproved`);
  }

  approveReview(id: number): Observable<Review> {
    return this.http.put<Review>(`${this.apiUrl}/${id}/confirmation`, null);
  }


}
