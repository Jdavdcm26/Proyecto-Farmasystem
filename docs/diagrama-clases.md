# Diagrama de Clases — Sistema de Farmacia

```mermaid
classDiagram
    direction TB

    class EntidadCatalogo {
        <<abstract>>
        #String nombre
        #String descripcion
    }

    class User {
        -Long id
        -String name
        -String apellido
        -String username
        -String email
        -String password
        -String rol
        +List~Producto~ productos
        +List~Categoria~ categorias
        +Cliente cliente
        +List~ConsultaIA~ consultasIA
    }

    class Cliente {
        -String cedula
        -String nombre
        -String apellido
        -String email
        -String telefono
        -String direccion
        -Boolean activo
        +List~Venta~ ventas
        +User user
    }

    class Categoria {
        -Long id
        -String nombre
        -String descripcion
        -Boolean activo
        +List~Producto~ productos
        +User user
    }

    class Producto {
        -Long id
        -String nombre
        -String descripcion
        -BigDecimal iva
        -Integer stock
        -Integer stockMinimo
        -Boolean activo
        +User user
        +Categoria categoria
        +List~Lote~ lotes
        +List~DetalleVenta~ detalleVentas
    }

    class Lote {
        -Long id
        -String numeroLote
        -LocalDate fechaIngreso
        -LocalDate fechaVencimiento
        -BigDecimal precioCosto
        -BigDecimal porcentajeUtilidad
        -BigDecimal precioVenta
        -Integer stock
        -Boolean activo
        +Producto producto
    }

    class Venta {
        -Long id
        -String numeroVenta
        -LocalDateTime fechaVenta
        -BigDecimal parcial
        -BigDecimal descuento
        -BigDecimal subtotal
        -BigDecimal ivaMonto
        -BigDecimal total
        +EstadoVenta estado
        +Cliente cliente
        +User user
        +List~DetalleVenta~ detalleVentas
    }

    class DetalleVenta {
        -Long id
        -Integer cantidad
        -BigDecimal precioUnitario
        -BigDecimal subtotal
        +Venta venta
        +Producto producto
        +Lote lote
    }

    class EstadoVenta {
        -Long id
        -String nombre
        -String descripcion
        +List~Venta~ ventas
    }

    class ConsultaIA {
        -Long id
        -String sintomas
        -String respuesta
        -LocalDateTime fecha
        +User user
    }

    %% Herencia
    EntidadCatalogo <|-- Categoria
    EntidadCatalogo <|-- EstadoVenta

    %% Relaciones
    User "1" --> "0..*" Producto       : gestiona
    User "1" --> "0..*" Categoria      : gestiona
    User "1" --> "0..1" Cliente        : tiene perfil
    User "1" --> "0..*" ConsultaIA     : realiza

    Cliente "1" --> "0..*" Venta       : genera

    Categoria "1" --> "0..*" Producto  : clasifica

    Producto "1" --> "0..*" Lote       : tiene
    Producto "1" --> "0..*" DetalleVenta : aparece en

    Venta "1" --> "1..*" DetalleVenta  : contiene
    Venta "*" --> "1" EstadoVenta      : tiene
    Venta "*" --> "1" User             : realizada por

    DetalleVenta "*" --> "1" Lote      : descontado de
```

## Descripción de entidades

| Entidad | Tabla BD | Descripción |
|---|---|---|
| `User` | `USUARIOS` | Usuario del sistema. Rol `ADMIN` o `CLIENT`. |
| `Cliente` | `CLIENTES` | Perfil de cliente asociado 1:1 a un `User` con rol CLIENT. |
| `Categoria` | `CATEGORIAS` | Categoría de productos, creada por un admin. |
| `Producto` | `PRODUCTOS` | Medicamento o artículo del inventario. |
| `Lote` | `LOTES` | Lote de compra con costo, precio de venta y fecha de vencimiento. |
| `Venta` | `VENTAS` | Cabecera de una venta con totales calculados. |
| `DetalleVenta` | `DETALLE_VENTAS` | Línea de venta: producto, lote, cantidad y precio unitario. |
| `EstadoVenta` | `ESTADOS_VENTA` | Catálogo de estados (ej. COMPLETADA, ANULADA). |
| `ConsultaIA` | `CONSULTAS_IA` | Historial de consultas al asistente de IA (Gemini). |
| `EntidadCatalogo` | *(superclase)* | Clase base abstracta con `nombre` y `descripcion` para catálogos. |
