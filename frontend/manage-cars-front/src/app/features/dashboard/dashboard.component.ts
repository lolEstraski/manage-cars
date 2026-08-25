import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ReactiveFormsModule, FormsModule, FormBuilder, FormGroup, Validators } from '@angular/forms';
import { Router } from '@angular/router';
import { AutoService } from '../../core/services/auto.service';
import { AuthService } from '../../core/services/auth.service';
import { CatalogoService, Marca, ModeloAuto } from '../../core/services/catalogo.service';
import { Auto } from '../../core/models/auto.model';
import { Usuario } from '../../core/models/usuario.model';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, ReactiveFormsModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit {
  autos: Auto[] = [];
  autosFiltrados: Auto[] = [];
  
  marcas: Marca[] = [];
  modelos: ModeloAuto[] = [];
  usuarios: Usuario[] = [];
  
  autoForm: FormGroup;
  showModal = false;
  showCatalogModal = false;
  showGenerateModal = false;
  
  editingId: number | null = null;
  loading = false;
  generatingAutos = false;
  selectedFile: File | null = null;
  
  isAdmin = false;
  
  filtroPlaca = '';
  filtroModelo = '';
  filtroMarca = '';
  filtroAnio = '';
  filtroUsuario = '';

  generateTargetUserId: number | null = null;

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
      color: ['', Validators.required],
      userId: [null]
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
    
    if (this.isAdmin) {
      this.cargarUsuarios();
    }
    
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
  
  cargarUsuarios(): void {
    this.authService.obtenerUsuarios().subscribe({
      next: (users) => {
        this.usuarios = users;
        if (this.usuarios.length > 0) {
          if (!this.generateTargetUserId) {
            this.generateTargetUserId = Number(this.usuarios[0].id);
          }
          if (this.isAdmin && !this.autoForm.value.userId) {
            this.autoForm.patchValue({ userId: Number(this.usuarios[0].id) });
          }
        }
      },
      error: (err) => console.error('Error al cargar usuarios:', err)
    });
  }

  getUsuarioLabel(userId?: number): string {
    if (!userId) return 'Sin asignar';
    const user = this.usuarios.find(u => Number(u.id) === Number(userId));
    if (user) {
      return `${user.nombre} ${user.apellido}`;
    }
    return `Usuario #${userId}`;
  }

  getUsuarioEmail(userId?: number): string {
    if (!userId) return '';
    const user = this.usuarios.find(u => Number(u.id) === Number(userId));
    return user ? user.correo : '';
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
      const nombre = this.nuevaMarcaForm.value.nombre.trim();
      this.catalogoService.crearMarca({ nombre }).subscribe({
        next: () => {
          this.cargarCatlogos();
          this.nuevaMarcaForm.reset();
          alert('Marca agregada correctamente al catálogo');
        },
        error: (err) => {
          const msg = typeof err.error === 'string' ? err.error : 'Error al agregar marca.';
          alert(msg);
        }
      });
    }
  }

  guardarNuevoModelo(): void {
    if (this.nuevoModeloForm.valid) {
      const { nombre, marca } = this.nuevoModeloForm.value;
      const modeloPayload = { 
        nombre: nombre.trim(), 
        marcaId: Number(marca) 
      };
      this.catalogoService.crearModelo(modeloPayload).subscribe({
        next: () => {
          this.cargarCatlogos();
          this.nuevoModeloForm.reset();
          alert('Modelo agregado correctamente al catálogo');
        },
        error: (err) => {
          const msg = typeof err.error === 'string' ? err.error : 'Error al agregar modelo.';
          alert(msg);
        }
      });
    }
  }

  cargarAutos(): void {
    if (this.isAdmin) {
      this.autoService.obtenerTodos(0, 1000).subscribe({
        next: (page) => {
          this.autos = page.content;
          this.aplicarFiltros();
        }
      });
    } else {
      this.autoService.obtenerMisAutos(0, 1000).subscribe({
        next: (page) => {
          this.autos = page.content;
          this.aplicarFiltros();
        }
      });
    }
  }

  aplicarFiltros(): void {
    this.autosFiltrados = this.autos.filter(a => {
      const matchPlaca = a.placa ? a.placa.toLowerCase().includes(this.filtroPlaca.toLowerCase().trim()) : true;
      const matchModelo = a.modelo ? a.modelo.toLowerCase().includes(this.filtroModelo.toLowerCase().trim()) : true;
      const matchMarca = this.filtroMarca ? a.marca === this.filtroMarca : true;
      const matchAnio = this.filtroAnio ? a.anio.includes(this.filtroAnio.trim()) : true;
      const matchUsuario = (this.filtroUsuario !== '' && this.filtroUsuario !== null && this.filtroUsuario !== undefined) 
        ? Number(a.userId) === Number(this.filtroUsuario) 
        : true;
      return matchPlaca && matchModelo && matchMarca && matchAnio && matchUsuario;
    });
  }

  actualizarFiltros(event: any, tipo: string): void {
    const val = event.target.value;
    if (tipo === 'placa') this.filtroPlaca = val;
    if (tipo === 'modelo') this.filtroModelo = val;
    if (tipo === 'marca') this.filtroMarca = val;
    if (tipo === 'anio') this.filtroAnio = val;
    if (tipo === 'usuario') this.filtroUsuario = val;
    this.aplicarFiltros();
  }

  limpiarFiltros(): void {
    this.filtroPlaca = '';
    this.filtroModelo = '';
    this.filtroMarca = '';
    this.filtroAnio = '';
    this.filtroUsuario = '';
    this.aplicarFiltros();
  }

  // Generar 10 Autos
  precargar10Autos(): void {
    if (this.isAdmin) {
      if (this.usuarios.length === 0) {
        this.cargarUsuarios();
      } else if (!this.generateTargetUserId) {
        this.generateTargetUserId = Number(this.usuarios[0].id);
      }
      this.showGenerateModal = true;
    } else {
      this.ejecutarPrecarga();
    }
  }

  cerrarGenerateModal(): void {
    this.showGenerateModal = false;
  }

  confirmarGeneracion10Autos(): void {
    const targetId = this.generateTargetUserId ? Number(this.generateTargetUserId) : undefined;
    this.ejecutarPrecarga(targetId);
  }

  private ejecutarPrecarga(targetUserId?: number): void {
    this.generatingAutos = true;
    this.autoService.precargarAutos(targetUserId).subscribe({
      next: (msg) => {
        this.generatingAutos = false;
        this.cerrarGenerateModal();
        if (targetUserId) {
          this.filtroUsuario = String(targetUserId);
        }
        this.cargarAutos();
        const destName = targetUserId ? ` para ${this.getUsuarioLabel(targetUserId)}` : '';
        alert((msg || '10 autos generados correctamente') + destName);
      },
      error: (err) => {
        this.generatingAutos = false;
        alert('Error al generar los autos. Verifique el catálogo y los datos.');
      }
    });
  }

  onFileSelected(event: any): void {
    const file = event.target.files[0];
    if (file) {
      this.selectedFile = file;
    }
  }

  setColor(color: string): void {
    this.autoForm.patchValue({ color });
  }

  abrirModal(auto?: Auto): void {
    this.selectedFile = null;
    const defaultUserId = this.usuarios.length > 0 ? Number(this.usuarios[0].id) : null;
    
    if (auto && auto.id) {
      this.editingId = auto.id;
      this.autoForm.patchValue({
        marca: auto.marca,
        modelo: auto.modelo,
        anio: auto.anio,
        placa: auto.placa,
        color: auto.color,
        userId: auto.userId ? Number(auto.userId) : defaultUserId
      });
      const marca = this.marcas.find(m => m.nombre === auto.marca);
      if (marca) {
        this.catalogoService.obtenerModelosPorMarca(marca.id).subscribe(mods => {
          this.modelos = mods;
        });
      }
    } else {
      this.editingId = null;
      this.autoForm.reset({
        marca: '',
        modelo: '',
        anio: '',
        placa: '',
        color: '',
        userId: defaultUserId
      });
      this.modelos = [];
    }
    this.showModal = true;
  }

  cerrarModal(): void {
    this.showModal = false;
  }

  guardarAuto(): void {
    if (this.autoForm.valid) {
      this.loading = true;
      const formValue = this.autoForm.value;
      const autoData: Auto = {
        marca: formValue.marca,
        modelo: formValue.modelo,
        anio: formValue.anio,
        placa: formValue.placa,
        color: formValue.color
      };
      
      if (this.isAdmin) {
        const rawUserId = formValue.userId;
        if (rawUserId !== null && rawUserId !== undefined && rawUserId !== '') {
          autoData.userId = Number(rawUserId);
        }
      }

      const handleImageUpload = (autoId: number) => {
        if (this.selectedFile) {
          this.autoService.subirImagen(autoId, this.selectedFile).subscribe({
            next: () => this.finalizarGuardado(autoData.userId),
            error: () => this.finalizarGuardado(autoData.userId)
          });
        } else {
          this.finalizarGuardado(autoData.userId);
        }
      };

      if (this.editingId) {
        this.autoService.actualizarAuto(this.editingId, autoData).subscribe({
          next: () => handleImageUpload(this.editingId!),
          error: () => {
            this.loading = false;
            alert('Error al actualizar el auto.');
          }
        });
      } else {
        this.autoService.guardarAuto(autoData).subscribe({
          next: (saved) => handleImageUpload(saved.id!),
          error: () => {
            this.loading = false;
            alert('Error al registrar el auto.');
          }
        });
      }
    }
  }
  
  finalizarGuardado(targetUserId?: number): void {
    if (this.isAdmin && targetUserId) {
      this.filtroUsuario = String(targetUserId);
    }
    this.cargarAutos();
    this.cerrarModal();
    this.loading = false;
  }

  eliminarAuto(id?: number): void {
    if (id && confirm('¿Estás seguro de que deseas eliminar este auto? Esta acción no se puede deshacer.')) {
      this.autoService.eliminarAuto(id).subscribe({
        next: () => this.cargarAutos(),
        error: () => alert('Error al eliminar el auto.')
      });
    }
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/login']);
  }
}
