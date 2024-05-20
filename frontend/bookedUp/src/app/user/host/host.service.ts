import {Host, Injectable} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {Guest} from "../model/guest.model";
import {environment} from "../../../env/env";
 // Zamijenite sa stvarnom putanjom ka modelu

@Injectable({
  providedIn: 'root'
})
export class HostService {
  private apiUrl = environment.apiBackend+'/hosts'; // Prilagodite URL-u vašem backend API-u

  constructor(private http: HttpClient) { }


  getHosts(): Observable<Host[]> {
    return this.http.get<Host[]>(this.apiUrl);
  }

  getHost(id: number): Observable<Host> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<Host>(url);
  }

  createHost(host: Host): Observable<Host> {
    return this.http.post<Host>(this.apiUrl, host);
  }

  updateHost(id: number, host: Host): Observable<Host> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.put<Host>(url, host);
  }

  deleteHost(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url);
  }
  getHostGuests(hostId: number): Observable<Guest[]> {
    const url = `${this.apiUrl}/${hostId}/guests`;
    return this.http.get<Guest[]>(url);
  }

  // Dodajte ostale metode prema potrebama
}
