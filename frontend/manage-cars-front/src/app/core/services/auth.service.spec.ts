import { TestBed } from '@angular/core/testing';
import { provideHttpClient } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { AuthService } from './auth.service';
import { Usuario } from '../models/usuario.model';

describe('AuthService', () => {
  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    TestBed.configureTestingModule({
      providers: [
        AuthService,
        provideHttpClient(),
        provideHttpClientTesting()
      ]
    });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
    localStorage.clear();
  });

  afterEach(() => {
    httpMock.verify();
    localStorage.clear();
  });

  it('debe crearse correctamente', () => {
    expect(service).toBeTruthy();
  });

  it('debe registrar un usuario correctamente', () => {
    const mockUser: Usuario = { nombre: 'Carlos', apellido: 'Gomez', correo: 'carlos@test.com', contrasena: '123456' };

    service.registro(mockUser).subscribe(user => {
      expect(user.correo).toBe('carlos@test.com');
    });

    const req = httpMock.expectOne('http://localhost:8085/api/v1/auth/registro');
    expect(req.request.method).toBe('POST');
    req.flush(mockUser);
  });

  it('debe guardar el token en localStorage al iniciar sesión', () => {
    const mockResponse = { token: 'mock.jwt.token' };

    service.login('carlos@test.com', '123456').subscribe(res => {
      expect(res.token).toBe('mock.jwt.token');
      expect(service.getToken()).toBe('mock.jwt.token');
    });

    const req = httpMock.expectOne('http://localhost:8085/api/v1/auth/login');
    expect(req.request.method).toBe('POST');
    req.flush(mockResponse);
  });

  it('debe obtener la lista de usuarios', () => {
    const mockUsers: Usuario[] = [
      { id: 1, nombre: 'Admin', apellido: 'User', correo: 'admin@admin.com' },
      { id: 2, nombre: 'Carlos', apellido: 'Gomez', correo: 'carlos@test.com' }
    ];

    service.obtenerUsuarios().subscribe(users => {
      expect(users.length).toBe(2);
      expect(users[0].nombre).toBe('Admin');
    });

    const req = httpMock.expectOne('http://localhost:8085/api/v1/auth/usuarios');
    expect(req.request.method).toBe('GET');
    req.flush(mockUsers);
  });

  it('debe limpiar el token en logout', () => {
    localStorage.setItem('token', 'sample.token');
    service.logout();
    expect(service.getToken()).toBeNull();
    expect(service.isAuthenticated()).toBeFalse();
  });
});
