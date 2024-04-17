import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, Subject, map } from 'rxjs';
import { Notification } from '../model/notification.model';

@Injectable({
  providedIn: 'root'
})
export class NotificationsService {

  private apiUrl = 'http://localhost:8080/api/notifications';
  
  private notifySource = new Subject<void>();
  notify$ = this.notifySource.asObservable();

  notifyNavBar() {
    this.notifySource.next();
  }

  constructor(private http: HttpClient) { }

  getNotifications(): Observable<Notification[]> {
    return this.http.get<Notification[]>(this.apiUrl);
  }

  getNotification(id: number): Observable<Notification> {
    return this.http.get<Notification>(`${this.apiUrl}/${id}`);
  }

  getNotificationsByUserId(id: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/user/${id}`);
  }

  getEnabledNotificationsByUserId(id: number): Observable<Notification[]> {
    return this.http.get<Notification[]>(`${this.apiUrl}/user/enabled/${id}`);
  }

  createNotification(notification: Notification): Observable<Notification> {
    return this.http.post<Notification>(this.apiUrl, notification);
  }

  updateNotification(id: number, notification: Notification): Observable<Notification> {
    return this.http.put<Notification>(`${this.apiUrl}/${id}`, notification);
  }

  deleteNotification(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

}