import { Injectable } from '@angular/core';
import {HttpClient, HttpHeaders} from "@angular/common/http";
import {Observable} from "rxjs";
import {Photo} from "../model/photo.model";

@Injectable({
  providedIn: 'root'
})
export class PhotoService {
  private baseUrl = 'http://localhost:8080/api/photo';

  constructor(private http: HttpClient) { }

  getAllPhotos(): Observable<Photo[]> {
    return this.http.get<Photo[]>(`${this.baseUrl}`);
  }

  getPhoto(id: number): Observable<Photo> {
    return this.http.get<Photo>(`${this.baseUrl}/${id}`);
  }

  uploadImage(file: File): Observable<any> {
    const formData = new FormData();
    formData.append('image', file);

    const headers = new HttpHeaders();
    return this.http.post(`${this.baseUrl}/upload`, formData);
  }

  loadPhoto(photo: Photo): Observable<Blob> {
    return this.http.get(`${this.baseUrl}/${photo.id}/load`, { responseType: 'blob' });
  }

  createPhoto(photo: Photo): Observable<Photo> {
    return this.http.post<Photo>(`${this.baseUrl}`, photo);
  }

  updatePhoto(id: number, photo: Photo): Observable<Photo> {
    return this.http.put<Photo>(`${this.baseUrl}/${id}`, photo);
  }

  deletePhoto(id: number): Observable<any> {
    return this.http.delete(`${this.baseUrl}/${id}`);
  }
}
