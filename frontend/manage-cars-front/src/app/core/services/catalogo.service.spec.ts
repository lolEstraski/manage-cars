import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { CatalogoService, Marca, ModeloAuto } from './catalogo.service';

describe('CatalogoService', () => {
  let service: CatalogoService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        CatalogoService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(CatalogoService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
  });

  it('debe crearse correctamente', () => {
    expect(service).toBeTruthy();
  });

  it('debe listar todas las marcas', () => {
    const mockMarcas: Marca[] = [
      { id: 1, nombre: 'Toyota' },
      { id: 2, nombre: 'Porsche' }
    ];

    service.obtenerMarcas().subscribe(marcas => {
      expect(marcas.length).toBe(2);
      expect(marcas[1].nombre).toBe('Porsche');
    });

    const req = httpMock.expectOne('http://localhost:8081/api/v1/catalogo/marcas');
    expect(req.request.method).toBe('GET');
    req.flush(mockMarcas);
  });

  it('debe obtener modelos por marca ID', () => {
    const mockModelos: ModeloAuto[] = [
      { id: 10, nombre: 'Corolla', marcaId: 1 }
    ];

    service.obtenerModelosPorMarca(1).subscribe(modelos => {
      expect(modelos.length).toBe(1);
      expect(modelos[0].nombre).toBe('Corolla');
    });

    const req = httpMock.expectOne('http://localhost:8081/api/v1/catalogo/marcas/1/modelos');
    expect(req.request.method).toBe('GET');
    req.flush(mockModelos);
  });

  it('debe crear una nueva marca', () => {
    const nuevaMarca = { nombre: 'Volvo' };
    const guardada: Marca = { id: 3, nombre: 'Volvo' };

    service.crearMarca(nuevaMarca).subscribe(res => {
      expect(res.id).toBe(3);
      expect(res.nombre).toBe('Volvo');
    });

    const req = httpMock.expectOne('http://localhost:8081/api/v1/catalogo/marcas');
    expect(req.request.method).toBe('POST');
    req.flush(guardada);
  });

  it('debe crear un nuevo modelo', () => {
    const nuevoModelo = { nombre: 'XC90', marcaId: 3 };
    const guardado: ModeloAuto = { id: 25, nombre: 'XC90', marcaId: 3 };

    service.crearModelo(nuevoModelo).subscribe(res => {
      expect(res.id).toBe(25);
      expect(res.nombre).toBe('XC90');
    });

    const req = httpMock.expectOne('http://localhost:8081/api/v1/catalogo/modelos');
    expect(req.request.method).toBe('POST');
    req.flush(guardado);
  });
});
