import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import {UserReport} from "../model/user-report";
import {User} from "../model/user.model";

@Injectable({
  providedIn: 'root'
})
export class UserReportService {
  private apiUrl = 'http://localhost:8080/api/user-reports';

  constructor(private http: HttpClient) { }

  getUserReports(): Observable<UserReport[]> {
    return this.http.get<UserReport[]>(this.apiUrl);
  }

  getUserReport(id: number): Observable<UserReport> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.get<UserReport>(url);
  }

  createUserReport(userReport: UserReport): Observable<UserReport> {
    return this.http.post<UserReport>(this.apiUrl, userReport);
  }

  updateUserReport(id: number, userReport: UserReport): Observable<UserReport> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.put<UserReport>(url, userReport);
  }

  deleteUserReport(id: number): Observable<void> {
    const url = `${this.apiUrl}/${id}`;
    return this.http.delete<void>(url);
  }

  getAllReportedUsers(): Observable<User[]> {
    const url = `${this.apiUrl}/reported-users`;
    return this.http.get<User[]>(url);
  }

  getReportReasonsForUser(reportUserId: number): Observable<string[]> {
    const url = `${this.apiUrl}/reasons/${reportUserId}`;
    return this.http.get<string[]>(url);
  }

}
