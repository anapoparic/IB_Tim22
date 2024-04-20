import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { User } from './model/user.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private apiUrl = 'http://localhost:8080/api/users';

  constructor(private http: HttpClient) {}

  getUsers(): Observable<User[]> {
    return this.http.get<User[]>(this.apiUrl);
  }

  getUser(id: number): Observable<User> {
    return this.http.get<User>(`${this.apiUrl}/${id}`);
  }

  getActiveUsers():Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/active-users`);
  }

  getBlockedUsers():Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/blocked-users`);
  }

  getReportedUsers():Observable<User[]> {
    return this.http.get<User[]>(`${this.apiUrl}/reported-users`);
  }

  createUser(User: User): Observable<User> {
    return this.http.post<User>(this.apiUrl, User);
  }

  updateUser(id: number, user: User): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}`, user);
  }

  deleteUser(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  loginUser(logInDto: any): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/login`, logInDto);
  }

  registerGuest(User: User): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/register-guest`, User);
  }

  registerHost(User: User): Observable<string> {
    return this.http.post<string>(`${this.apiUrl}/register-host`, User);
  }

  blockUser(id: number): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}/block`, {});
  }

  unblockUser(id: number): Observable<User> {
    return this.http.put<User>(`${this.apiUrl}/${id}/unblock`, {});
  }
}
