import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CertificateRequest } from './model/certificateRequest.model';
import { Observable, catchError, throwError } from 'rxjs';
import { Certificate } from './model/certificate.model';

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

  private apiUrlCer = 'http://localhost:8081/api/certificates';

  getAllCertificates(): Observable<Certificate[]> {
    return this.http.get<Certificate[]>(this.apiUrlCer);
  }

  getCertificateById(id: number): Observable<Certificate> {
    return this.http.get<Certificate>(`${this.apiUrlCer}/${id}`);
  }

  createCertificate(requestDTO: CertificateRequest, alias: string, issuerAlias: string, template: string): Observable<Certificate> {
    return this.http.post<Certificate>(this.apiUrlCer, requestDTO, {
      params: {
        alias: alias,
        issuerAlias: issuerAlias,
        template: template
      }
    }).pipe(
      catchError(this.handleError)
    );
  }

  deleteCertificate(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrlCer}/${id}`);
  }

  private handleError(error: any) {
    console.error('An error occurred:', error);
    return throwError('Something went wrong; please try again later.');
  }
}