import { Injectable } from '@angular/core';
import { Certificate } from './certificate.model';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class CertificatesService {
  certificates: Certificate[] = [];

  constructor() {
    this.certificates = [
      {
        number: 1,
        commonName: "John Doe",
        validFrom: new Date("2024-04-01"),
        validUntil: new Date("2025-04-01"),
        alias: "JD123",
        issuerAlias: "AdminCA",
        template: "User Certificate",
        revoked: false
    },
    {
        number: 2,
        commonName: "Jane Smith",
        validFrom: new Date("2024-03-15"),
        validUntil: new Date("2025-03-15"),
        alias: "JS456",
        issuerAlias: "AdminCA",
        template: "User Certificate",
        revoked: false
    },
    {
        number: 3,
        commonName: "Acme Corp Web Server",
        validFrom: new Date("2024-02-20"),
        validUntil: new Date("2025-02-20"),
        alias: "acme-server",
        issuerAlias: "WebServerCA",
        template: "Server Certificate",
        revoked: false
    }
    ];
    
  }

  getCertifications(): Observable<Certificate[]> {
    return of(this.certificates);
  }


  // private apiUrl = 'http://localhost:8081/api/certificates'; // Prilagodite URL prema portu 8081 i putanji na serveru

  // constructor(private http: HttpClient) { }

  // getAllCertificates(): Observable<CertificateDTO[]> {
  //   return this.http.get<CertificateDTO[]>(this.apiUrl);
  // }

  // getCertificateById(id: number): Observable<CertificateDTO> {
  //   return this.http.get<CertificateDTO>(`${this.apiUrl}/${id}`);
  // }

  // createCertificate(requestDTO: CertificateRequestDTO, alias: string, issuerAlias: string, template: string): Observable<CertificateDTO> {
  //   return this.http.post<CertificateDTO>(this.apiUrl, requestDTO, {
  //     params: {
  //       alias: alias,
  //       issuerAlias: issuerAlias,
  //       template: template
  //     }
  //   }).pipe(
  //     catchError(this.handleError)
  //   );
  // }

  // deleteCertificate(id: number): Observable<void> {
  //   return this.http.delete<void>(`${this.apiUrl}/${id}`);
  // }

  // private handleError(error: any) {
  //   console.error('An error occurred:', error);
  //   return throwError('Something went wrong; please try again later.');
  // }
}
