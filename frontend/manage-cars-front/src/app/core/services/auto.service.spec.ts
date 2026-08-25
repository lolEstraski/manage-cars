import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AutoService } from './auto.service';
import { Auto, Page } from '../models/auto.model';

describe('AutoService', () => {
  let service: AutoService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AutoService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AutoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debe crearse correctamente', () => {
    expect(service).toBeTruthy();
  });

  it('debe registrar un auto nuevo', () => {
    const mockAuto: Auto = { marca: 'Toyota', modelo: 'Corolla', anio: '2024', placa: 'ABC-123', color: 'Rojo', userId: 2 };

    service.guardarAuto(mockAuto).subscribe(auto => {
      expect(auto.marca).toBe('Toyota');
      expect(auto.userId).toBe(2);
    });

    const req = httpMock.expectOne('http://localhost:8081/api/v1/autos');
    expect(req.request.method).toBe('POST');
    expect(req.request.body).toEqual(mockAuto);
    req.flush(mockAuto);
  });

  it('debe llamar a precargar sin targetUserId', () => {
    service.precargarAutos().subscribe(res => {
      expect(res).toBe('10 autos generados');
    });

    const req = httpMock.expectOne('http://localhost:8081/api/v1/autos/precargar');
    expect(req.request.method).toBe('POST');
    req.flush('10 autos generados');
  });

  it('debe llamar a precargar con targetUserId para admin', () => {
    service.precargarAutos(10004).subscribe(res => {
      expect(res).toBe('10 autos generados para 10004');
    });

    const req = httpMock.expectOne('http://localhost:8081/api/v1/autos/precargar?targetUserId=10004');
    expect(req.request.method).toBe('POST');
    req.flush('10 autos generados para 10004');
  });

  it('debe obtener todos los autos paginados', () => {
    const mockPage: Page<Auto> = {
      content: [{ id: 1, marca: 'Audi', modelo: 'A3', anio: '2024', placa: 'XYZ-999', color: 'Negro' }],
      totalElements: 1,
      totalPages: 1,
      size: 10,
      number: 0,
      first: true,
      last: true,
      empty: false
    };

    service.obtenerTodos(0, 10).subscribe(page => {
      expect(page.content.length).toBe(1);
      expect(page.content[0].marca).toBe('Audi');
    });

    const req = httpMock.expectOne('http://localhost:8081/api/v1/autos?page=0&size=10');
    expect(req.request.method).toBe('GET');
    req.flush(mockPage);
  });

  it('debe eliminar un auto por ID', () => {
    service.eliminarAuto(1).subscribe();

    const req = httpMock.expectOne('http://localhost:8081/api/v1/autos/1');
    expect(req.request.method).toBe('DELETE');
    req.flush(null);
  });
});
