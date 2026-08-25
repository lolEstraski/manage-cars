import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

export interface Marca {
  id: number;
  nombre: string;
}

export interface ModeloAuto {
  id: number;
  nombre: string;
  marcaId: number;
}

@Injectable({
  providedIn: 'root'
})
export class CatalogoService {
  private apiUrl = 'http://localhost:8081/api/v1/catalogo';

  constructor(private http: HttpClient) {}

  obtenerMarcas(): Observable<Marca[]> {
    return this.http.get<Marca[]>(`${this.apiUrl}/marcas`);
  }

  obtenerModelosPorMarca(marcaId: number): Observable<ModeloAuto[]> {
    return this.http.get<ModeloAuto[]>(`${this.apiUrl}/marcas/${marcaId}/modelos`);
  }

  crearMarca(marca: {nombre: string}): Observable<Marca> {
    return this.http.post<Marca>(`${this.apiUrl}/marcas`, marca);
  }

  crearModelo(modelo: {nombre: string, marca: {id: number}}): Observable<ModeloAuto> {
    return this.http.post<ModeloAuto>(`${this.apiUrl}/modelos`, modelo);
  }
}
