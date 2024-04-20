import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CertificateRequest } from './model/certificateRequest.model';
import { Observable, catchError, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CertificationService {
  private apiUrl = 'http://localhost:8081/api/requests'; // Prilagodite URL prema portu 8081 i putanji na serveru

  constructor(private http: HttpClient) { }

  getAllRequests(): Observable<CertificateRequest[]> {
    return this.http.get<CertificateRequest[]>(`${this.apiUrl}`);
  }

  getRequestById(id: number): Observable<CertificateRequest> {
    return this.http.get<CertificateRequest>(`${this.apiUrl}/${id}`);
  }

  createRequest(request: CertificateRequest): Observable<CertificateRequest> {
    return this.http.post<CertificateRequest>(`${this.apiUrl}`, request)
      .pipe(
        catchError(this.handleError)
      );
  }

  deleteRequest(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  private handleError(error: any) {
    console.error('An error occurred:', error);
    return throwError('Something went wrong; please try again later.');
  }
}