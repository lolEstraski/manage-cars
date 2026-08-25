import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AutoService } from '../../core/services/auto.service';
import { AuthService } from '../../core/services/auth.service';
import { CatalogoService, Marca, ModeloAuto } from '../../core/services/catalogo.service';
import { Auto } from '../../core/models/auto.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  autos: Auto[] = [];
  autosFiltrados: Auto[] = [];
  
  marcas: Marca[] = [];
  modelos: ModeloAuto[] = [];
  
  autoForm: FormGroup;
  showModal = false;
  showCatalogModal = false;
  editingId: number | null = null;
  loading = false;
  selectedFile: File | null = null;
  
  isAdmin = false;
  
  filtroPlaca = '';
  filtroModelo = '';
  filtroMarca = '';
  filtroAnio = '';

  nuevaMarcaForm: FormGroup;
  nuevoModeloForm: FormGroup;

  constructor(
    private autoService: AutoService,
    public authService: AuthService,
    private catalogoService: CatalogoService,
    private fb: FormBuilder,
    private router: Router
  ) {
    this.autoForm = this.fb.group({
      marca: ['', Validators.required],
      modelo: ['', Validators.required],
      anio: ['', [Validators.required, Validators.pattern('^[0-9]{4}$')]],
      placa: ['', Validators.required],
      color: ['', Validators.required]
    });

    this.nuevaMarcaForm = this.fb.group({
      nombre: ['', Validators.required]
    });

    this.nuevoModeloForm = this.fb.group({
      nombre: ['', Validators.required],
      marca: [null, Validators.required]
    });
  }

  ngOnInit(): void {
    this.isAdmin = this.authService.isAdmin();
    this.cargarCatlogos();
    this.cargarAutos();

    this.autoForm.get('marca')?.valueChanges.subscribe(marcaNombre => {
      const marca = this.marcas.find(m => m.nombre === marcaNombre);
      if (marca) {
        this.catalogoService.obtenerModelosPorMarca(marca.id).subscribe(mods => {
          this.modelos = mods;
        });
      } else {
        this.modelos = [];
      }
    });
  }
  
  cargarCatlogos(): void {
    this.catalogoService.obtenerMarcas().subscribe(m => this.marcas = m);
  }

  abrirCatalogModal(): void {
    this.showCatalogModal = true;
    this.nuevaMarcaForm.reset();
    this.nuevoModeloForm.reset();
  }

  cerrarCatalogModal(): void {
    this.showCatalogModal = false;
  }

  guardarNuevaMarca(): void {
    if (this.nuevaMarcaForm.valid) {
      this.catalogoService.crearMarca(this.nuevaMarcaForm.value).subscribe({
        next: () => {
          this.cargarCatlogos();
          this.nuevaMarcaForm.reset();
          alert('Marca agregada correctamente');
        },
        error: (err) => alert('Error al agregar marca. Asegúrate de que no exista.')
      });
    }
  }

  guardarNuevoModelo(): void {
    if (this.nuevoModeloForm.valid) {
      const { nombre, marca } = this.nuevoModeloForm.value;
      const modeloPayload = { nombre, marca: { id: marca } };
      this.catalogoService.crearModelo(modeloPayload).subscribe({
        next: () => {
          this.nuevoModeloForm.reset();
          alert('Modelo agregado correctamente');
        },
        error: (err) => alert('Error al agregar modelo.')
      });
    }
  }

  cargarAutos(): void {
    if (this.isAdmin) {
      this.autoService.obtenerTodos(0, 100).subscribe({
        next: (page) => {
          this.autos = page.content;
          this.aplicarFiltros();
        }
      });
    } else {
      this.autoService.obtenerMisAutos(0, 100).subscribe({
        next: (page) => {
          this.autos = page.content;
          this.aplicarFiltros();
        }
      });
    }
  }

  aplicarFiltros(): void {
    this.autosFiltrados = this.autos.filter(a => {
      const matchPlaca = a.placa.toLowerCase().includes(this.filtroPlaca.toLowerCase());
      const matchModelo = a.modelo.toLowerCase().includes(this.filtroModelo.toLowerCase());
      const matchMarca = this.filtroMarca ? a.marca === this.filtroMarca : true;
      const matchAnio = this.filtroAnio ? a.anio === this.filtroAnio : true;
      return matchPlaca && matchModelo && matchMarca && matchAnio;
    });
  }

  actualizarFiltros(event: any, tipo: string): void {
    const val = event.target.value;
    if (tipo === 'placa') this.filtroPlaca = val;
    if (tipo === 'modelo') this.filtroModelo = val;
    if (tipo === 'marca') this.filtroMarca = val;
    if (tipo === 'anio') this.filtroAnio = val;
    this.aplicarFiltros();
  }

  precargar10Autos(): void {
    this.autoService.precargarAutos().subscribe(() => {
      this.cargarAutos();
    });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  abrirModal(auto?: Auto): void {
    this.selectedFile = null;
    if (auto && auto.id) {
      this.editingId = auto.id;
      this.autoForm.patchValue(auto);
      // Trigger marca change to load modelos
      this.autoForm.get('marca')?.updateValueAndValidity();
    } else {
      this.editingId = null;
      this.autoForm.reset();
    }
    this.showModal = true;
  }

  cerrarModal(): void {
    this.showModal = false;
  }

  guardarAuto(): void {
    if (this.autoForm.valid) {
      this.loading = true;
      const autoData: Auto = this.autoForm.value;

      const handleImageUpload = (autoId: number) => {
        if (this.selectedFile) {
          this.autoService.subirImagen(autoId, this.selectedFile).subscribe(() => {
            this.finalizarGuardado();
          });
        } else {
          this.finalizarGuardado();
        }
      };

      if (this.editingId) {
        this.autoService.actualizarAuto(this.editingId, autoData).subscribe({
          next: () => handleImageUpload(this.editingId!)
        });
      } else {
        this.autoService.guardarAuto(autoData).subscribe({
          next: (saved) => handleImageUpload(saved.id!)
        });
      }
    }
  }
  
  finalizarGuardado(): void {
    this.cargarAutos();
    this.cerrarModal();
    this.loading = false;
  }

  eliminarAuto(id?: number): void {
    if (id && confirm('¿Estás seguro de eliminar este auto?')) {
      this.autoService.eliminarAuto(id).subscribe({
        next: () => this.cargarAutos()
      });
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
