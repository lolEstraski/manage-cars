import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Auto, Page } from '../models/auto.model';

@Injectable({
  providedIn: 'root'
})
export class AutoService {
  private apiUrl = 'http://localhost:8081/api/v1/autos';

  constructor(private http: HttpClient) {}

  obtenerMisAutos(page: number = 0, size: number = 10): Observable<Page<Auto>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Auto>>(`${this.apiUrl}/mis-autos`, { params });
  }

  guardarAuto(auto: Auto): Observable<Auto> {
    return this.http.post<Auto>(this.apiUrl, auto);
  }

  actualizarAuto(id: number, auto: Auto): Observable<Auto> {
    return this.http.put<Auto>(`${this.apiUrl}/${id}`, auto);
  }

  eliminarAuto(id: number): Observable<void> {
    return this.http.delete<void>(`${this.apiUrl}/${id}`);
  }

  obtenerTodos(page: number = 0, size: number = 10): Observable<Page<Auto>> {
    const params = new HttpParams().set('page', page).set('size', size);
    return this.http.get<Page<Auto>>(`${this.apiUrl}`, { params });
  }

  obtenerPorPlaca(placa: string): Observable<Auto> {
    return this.http.get<Auto>(`${this.apiUrl}/placa/${placa}`);
  }

  precargarAutos(targetUserId?: number): Observable<string> {
    const url = targetUserId ? `${this.apiUrl}/precargar?targetUserId=${targetUserId}` : `${this.apiUrl}/precargar`;
    return this.http.post(url, {}, { responseType: 'text' });
  }

  subirImagen(autoId: number, file: File): Observable<Auto> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http.post<Auto>(`${this.apiUrl}/${autoId}/imagen`, formData);
  }
}
