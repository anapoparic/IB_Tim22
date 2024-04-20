import { Injectable } from '@angular/core';
import { Certificate } from './certificate.model';
import { Observable, catchError, of, throwError } from 'rxjs';
import { HttpClient } from '@angular/common/http';
import { CertificateRequest } from '../request/certificateRequest.model';

@Injectable({
  providedIn: 'root'
})
export class CertificatesService {
  private apiUrlCer = 'http://localhost:8081/api/certificates';
  
  constructor(private http: HttpClient) { }

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
