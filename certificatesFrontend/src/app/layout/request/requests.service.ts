import { Injectable } from '@angular/core';
import { Request } from './request.model';
import { Observable, of } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class RequestsService {
  requests: Request[] = [];

  constructor() {
    // Inicijalizujemo requests niz sa testnim podacima
    this.requests = [
      {
        number: 12345,
        commonName: "John",
        surname: "Doe",
        givenName: "John",
        organization: "Example Inc.",
        email: "johndoe@example.com",
        uid: "johndoe123"
      },
      {
        number: 67890,
        commonName: "Jane",
        surname: "Smith",
        givenName: "Jane",
        organization: "ABC Company",
        email: "janesmith@example.com",
        uid: "janesmith456"
      },
      {
        number: 54321,
        commonName: "Michael",
        surname: "Johnson",
        givenName: "Michael",
        organization: "XYZ Corporation",
        email: "michaeljohnson@example.com",
        uid: "michaeljohnson789"
      }
    ];
    
  }

  getRequests(): Observable<Request[]> {
    return of(this.requests);
  }
}