import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { CertificateRequest } from './model/certificateRequest.model';
import { Observable, catchError, throwError } from 'rxjs';
import { Certificate } from './model/certificate.model';
import {environment} from "../../env/env";

@Injectable({
  providedIn: 'root'
})
export class CertificationService {
  private apiUrl = environment.apiCertificateBackend+'/requests'; // Prilagodite URL prema portu 8081 i putanji na serveru

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

  private apiUrlCer = environment.apiCertificateBackend+'/certificates';
  private apiUrlBackend = environment.apiBackend+'/certificates';

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

  downloadCertificate(alias?: string): Observable<Certificate> {
    return this.http.get<Certificate>(`${this.apiUrlBackend}/download/${alias}`)
      .pipe(
        catchError(this.handleError)
      );
  }

  // private handleError(error: HttpErrorResponse) {
  //   let errorMessage = 'An unknown error occurred!';
    
  //   if (error.error instanceof ErrorEvent) {
  //     // A client-side or network error occurred.
  //     errorMessage = `A client-side error occurred: ${error.error.message}`;
  //   } else {
  //     // The backend returned an unsuccessful response code.
  //     errorMessage = `Backend returned code ${error.status}, body was: ${error.error}`;
  //   }

  //   // Optionally log to the console for debugging
  //   console.error(errorMessage);
    
  //   return throwError(() => new Error(errorMessage));
  // }

  private handleError(error: any) {
    console.error('An error occurred:', error);
    return throwError('Something went wrong; please try again later.');
  }

  generateUniqueUID(): number {
    const currentDate = new Date();

    const day = this.padZero(currentDate.getDate());
    const month = this.padZero(currentDate.getMonth() + 1);
    const year = currentDate.getFullYear().toString().slice(-4);
    const hours = this.padZero(currentDate.getHours());
    const minutes = this.padZero(currentDate.getMinutes());
    const seconds = this.padZero(currentDate.getSeconds());
    const milliseconds = currentDate.getMilliseconds();

    const uniqueUIDString = `${day}${month}${year}${hours}${minutes}${seconds}${milliseconds.toString().padStart(3, '0')}`;

    const uniqueUID = Number(uniqueUIDString);

    return uniqueUID;
  }

  private padZero(num: number): string {
    return num.toString().padStart(2, '0');
  }

  generateAlias(prefix: string): string {
    const uniqueNumber = Date.now().toString();
    const alias = prefix + uniqueNumber;
    return alias;
  }

  getAliasByOwnerEmail(ownerEmail?: string):  Observable<string> {
    return this.http.get(`${this.apiUrlCer}/aliasByOwnerEmail/${ownerEmail}`, { responseType: 'text' });
  }
  
}
