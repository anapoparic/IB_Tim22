import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {BehaviorSubject, Observable, map} from "rxjs";
import {AuthResponse} from "./model/auth-response";
import {JwtHelperService} from "@auth0/angular-jwt";
import {environment} from "../../../env/env";
import {User} from "../../user/model/user.model";
import sha1 from 'sha1';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private apiUrl = environment.apiBackend;


  private headers = new HttpHeaders({
    'Content-Type': 'application/json',
    skip: 'true',
  });

  user$ = new BehaviorSubject("");
  userState = this.user$.asObservable();

  constructor(private http: HttpClient) {
    this.user$.next(this.getRole());
  }

  login(auth: any): Observable<AuthResponse> {
    return this.http.post<AuthResponse>(`${this.apiUrl}/login`, auth, {
      headers: this.headers,
    });
  }

  logout(): Observable<string> {
    return this.http.get(`${this.apiUrl}/logout`, {
      responseType: 'text',
    });
  }

  getRole(): any {
    if (this.isLoggedIn()) {
      const accessToken: any = localStorage.getItem('user');
      const helper = new JwtHelperService();
      return helper.decodeToken(accessToken).role[0].authority;
    }
    return null;
  }

  getUsername(): any {
    if (this.isLoggedIn()) {
      const accessToken: any = localStorage.getItem('user');
      const helper = new JwtHelperService();
      return helper.decodeToken(accessToken).username;
    }
    return null;
  }

  getUserID(): number {
    if (this.isLoggedIn()) {
      const accessToken: any = localStorage.getItem('user');
      const helper = new JwtHelperService();
      return helper.decodeToken(accessToken).id;
    }
    return 0;
  }

  isLoggedIn(): boolean {
    return localStorage.getItem('user') != null;
  }

  setUser(): void {
    this.user$.next(this.getRole());
  }

  register(user: User): Observable<User> {
    return this.http.post<User>(`${this.apiUrl}/registration`, user, {
      headers: this.headers,
    });
  }

  checkPassword(password: string): Observable<boolean> {
    const sha1Password = sha1(password);
    const prefix = sha1Password.substring(0, 5);
    const suffix = sha1Password.substring(5).toUpperCase();

    return this.http.get(`https://api.pwnedpasswords.com/range/${prefix}`, { responseType: 'text' }).pipe(
      map(response => this.isPasswordPwned(response, suffix))
    );
  }

  private isPasswordPwned(response: string, suffix: string): boolean {
    const lines = response.split('\n');
    for (const line of lines) {
      const [hashSuffix] = line.split(':');
      if (hashSuffix === suffix) {
        return true;
      }
    }
    return false;
  }

}
