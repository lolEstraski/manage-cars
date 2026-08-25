export interface Auto {
  id?: number;
  marca: string;
  modelo: string;
  anio: string;
  placa: string;
  color: string;
  userId?: number;
  imagenUrl?: string;
  fechaRegistro?: string;
}

export interface Page<T> {
  content: T[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
  first: boolean;
  last: boolean;
  empty: boolean;
}
