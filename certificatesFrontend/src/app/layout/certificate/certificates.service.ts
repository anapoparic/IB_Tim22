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
}
