export interface Role {
  id?: number;
  nombre: string;
}

export interface Usuario {
  id?: number;
  nombre: string;
  apellido: string;
  correo: string;
  contrasena?: string; // Solo para envío
  rol?: Role;
  fechaCreacion?: string;
  ultimoAcceso?: string;
}

export interface LoginResponse {
  token: string;
}
