# Sistema de Farmacia — FarmaSystem

Aplicación web para la gestión integral de una farmacia, desarrollada con Spring Boot y Oracle Database. Soporta dos roles: **Administrador** y **Cliente**, cada uno con su propio panel de control.

---

## Tecnologías

| Capa | Tecnología |
|---|---|
| Backend | Java 26 · Spring Boot 4.0.5 (Web MVC, Data JPA, Validation, Actuator) |
| Base de datos | Oracle Database XE 21c (JDBC ojdbc11) |
| Vistas | Thymeleaf 3 |
| IA | Google Gemini 2.5 Flash (API REST) |
| Utilidades | Lombok · Jackson |
| Build | Maven Wrapper |

---

## Funcionalidades

### Panel de Administrador
- **Dashboard** — estadísticas de ventas, stock bajo y productos próximos a vencer
- **Productos** — CRUD completo con estado activo/inactivo
- **Categorías** — gestión de categorías de productos
- **Lotes** — registro de lotes por producto con costo, precio de venta y fecha de vencimiento
- **Ventas** — creación de ventas con descuento, cálculo automático de IVA y total
- **Clientes** — listado y gestión de clientes registrados
- **Reportes** — reportes de ventas por periodo
- **Perfil** — edición de datos del administrador

### Panel de Cliente
- **Tienda** — catálogo de productos disponibles con carrito de compras
- **Mis Compras** — historial de compras realizadas con detalle
- **Consulta IA** — asistente de salud basado en Gemini que sugiere medicamentos según síntomas, con historial de consultas
- **Mi Perfil** — visualización y edición de datos personales

---

## Estructura del proyecto

```
src/
└── main/
    ├── java/com/farmaciaproyecto/
    │   ├── controller/        # Controladores MVC y REST
    │   ├── dto/
    │   │   ├── request/       # DTOs de entrada
    │   │   └── response/      # DTOs de salida
    │   ├── model/             # Entidades JPA
    │   ├── repository/        # Interfaces Spring Data JPA
    │   ├── service/           # Lógica de negocio
    │   ├── util/              # Utilidades (sesión)
    │   └── DataInitializer    # Datos iniciales (admin por defecto)
    └── resources/
        ├── templates/
        │   ├── admin/         # Vistas del panel administrador
        │   ├── cliente/       # Vistas del panel cliente
        │   └── fragments/     # Layouts compartidos (shells)
        ├── static/
        │   ├── css/
        │   ├── js/
        │   └── img/
        └── application.properties
docs/
└── diagrama-clases.md         # Diagrama de clases (Mermaid)
```

---

## Modelo de datos

Ver el diagrama completo en [`docs/diagrama-clases.md`](docs/diagrama-clases.md).

Las entidades principales son:

- **User** — usuario del sistema con rol `ADMIN` o `CLIENT`
- **Cliente** — perfil de cliente vinculado 1:1 a un `User`
- **Producto** — artículo del inventario, clasificado por `Categoria`
- **Lote** — lote de compra de un producto con su precio y vencimiento
- **Venta / DetalleVenta** — cabecera y líneas de una transacción de venta
- **EstadoVenta** — catálogo de estados de venta (COMPLETADA, ANULADA, etc.)
- **ConsultaIA** — historial de consultas al asistente de inteligencia artificial

---

## Configuración y ejecución

### Requisitos
- Java 26+
- Oracle Database XE (instancia `XEPDB1` en `localhost:1521`)
- Maven (o usar el wrapper incluido `./mvnw`)

### Variables de configuración

Editar `src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:oracle:thin:@localhost:1521/XEPDB1
spring.datasource.username=FARMACIA
spring.datasource.password=FARMACIA_PASSWORD

# Credenciales del administrador inicial
admin.username=admin
admin.password=Admin123
admin.email=admin@farmasystem.com

# API de Gemini (requerida para la funcionalidad de IA)
gemini.api.key=TU_API_KEY
```

### Ejecutar

```bash
./mvnw spring-boot:run
```

La aplicación queda disponible en `http://localhost:8080`.

El administrador por defecto se crea automáticamente al arrancar (`DataInitializer`).

---

## Roles y acceso

| Rol | Acceso |
|---|---|
| `ADMIN` | `/admin/**` — panel de gestión completo |
| `CLIENT` | `/cliente/**` — tienda, historial de compras, perfil e IA |

La sesión se gestiona mediante `HttpSession`. El control de acceso por rol se verifica en los controladores y a través de `SessionUtils`.

---

## Autor

Jose Castro Matos — Universidad Popular del Cesar
Andry Camacho Martinez - Universidad Popular del Cesar
