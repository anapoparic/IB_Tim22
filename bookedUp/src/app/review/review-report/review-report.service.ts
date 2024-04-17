import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {ReviewReport} from "../model/review-report.model";
import {Review} from "../model/review.model";

@Injectable({
  providedIn: 'root'
})
export class ReviewReportService {
  private apiUrl = 'http://localhost:8080/api/review-reports';  // Prilagodite URL-u prema vašem API-ju

  constructor(private http: HttpClient) { }

  getReviewReports(): Observable<ReviewReport[]> {
    return this.http.get<ReviewReport[]>(this.apiUrl);
  }

  getReviewReport(id: number): Observable<ReviewReport> {
    return this.http.get<ReviewReport>(`${this.apiUrl}/${id}`);
  }

  createReviewReport(report: ReviewReport): Observable<ReviewReport> {
    return this.http.post<ReviewReport>(this.apiUrl, report);
  }

  updateReviewReport(id: number, report: ReviewReport): Observable<ReviewReport> {
    return this.http.put<ReviewReport>(`${this.apiUrl}/${id}`, report);
  }

  deleteReviewReport(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  getReportedReviews(): Observable<Review[]> {
    return this.http.get<Review[]>(`${this.apiUrl}/reported-reviews`);
  }

  getReportReasonsForReview(reportReviewId: number): Observable<string[]> {
    return this.http.get<string[]>(`${this.apiUrl}/reasons/${reportReviewId}`);
  }
}
