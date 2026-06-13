# progrmacion_parcial_final
# 📦 Sistema de Inventario SAMADIGITAL

## 👥 Integrantes

| Nombre | Rol |
|--------|-----|
| _(tu nombre)_ | _(tu rol)_ |

---

## 📝 Descripción del Problema

El Sistema de Inventario SAMADIGITAL surge ante la necesidad de modernizar y automatizar la gestión de productos y ventas de un negocio que manejaba su inventario de forma manual, lo que generaba errores en los precios, pérdida de información y dificultad para obtener reportes financieros.

**Problemas identificados:**
- Control manual propenso a errores humanos
- Pérdida de información de productos y ventas
- Dificultad para calcular márgenes de ganancia y tarifas con IVA
- Falta de reportes financieros organizados
- Sin diferenciación por tipo de producto (físico, digital, servicio)
- Ausencia de control de stock disponible

## ✨ Solución Propuesta

Se desarrolló un sistema integral con **dos interfaces**: consola interactiva y ventana gráfica con JavaFX, que permite:

- Registrar, editar y eliminar productos (físicos, digitales y servicios)
- Registrar ventas con descuento automático de stock
- Calcular márgenes de ganancia y precios con IVA
- Controlar el stock disponible (máximo 1000 productos)
- Generar reportes financieros detallados
- Buscar productos por código o nombre
- Evitar duplicados en el sistema

---

## 🚀 Explicación de Clases y Métodos Principales

| Clase | Rol |
|-------|-----|
| `Producto` | Clase abstracta padre que representa un producto general |
| `ProductoFisico` | Clase hija que representa un producto físico |
| `ProductoDigital` | Clase hija que representa un producto digital |
| `ProductoServicio` | Clase hija que representa un servicio |
| `Tarifas` | Gestiona precios, IVA y cálculos de márgenes |
| `Venta` | Almacena información de cada transacción realizada |
| `GestorInventario` | Clase principal que gestiona todas las operaciones |
| `inventario` | Contiene el método `main` de la interfaz consola |
| `InventarioApp` | Contiene el método `main` de la interfaz gráfica JavaFX |

---

### 📦 Clase Producto (Abstracta)

**Propósito:** Clase padre que define la estructura básica de cualquier producto.

**Atributos:**
- `codigo`: código único del producto (String)
- `nombre`: nombre del producto (String)
- `precioCompra`: precio de compra (double)
- `precioVenta`: precio de venta (double)
- `cantidad`: stock disponible (int)

**Métodos:**
- `getTipo()`: método abstracto que cada subclase implementa
- `calcularGanancia()`: retorna la ganancia total del producto en stock
- `toString()`: representación en cadena del producto

---

### 🗂️ Clases Hijas: ProductoFisico, ProductoDigital, ProductoServicio

**Propósito:** Representan tipos específicos de productos que extienden de `Producto`.

**Características:**
- **Herencia:** Todas extienden de la clase `Producto`
- **Constructores:** Cada una inicializa su tipo específico usando `super()`
- Ejemplos:
  - `ProductoFisico(...)` → `getTipo()` retorna `"Físico"`
  - `ProductoDigital(...)` → `getTipo()` retorna `"Digital"`
  - `ProductoServicio(...)` → `getTipo()` retorna `"Servicio"`

---

### 💰 Clase Tarifas

**Propósito:** Maneja todo lo relacionado con precios y cálculos financieros.

**Constantes:**
- `IVA = 0.19`: IVA aplicado en Colombia (19%)
- `MARGEN_MINIMO = 0.10`: margen mínimo recomendado (10%)

**Métodos:**
- `calcularPrecioSugerido(precioCompra, margen)`: calcula precio de venta recomendado
- `calcularPrecioConIva(precio)`: calcula precio con IVA incluido
- `calcularMargen(precioCompra, precioVenta)`: calcula el margen porcentual
- `mostrarTarifas()`: muestra tabla de tarifas en consola
- `obtenerResumenTarifas()`: retorna resumen de tarifas como texto para la UI

---

### 🧾 Clase Venta

**Propósito:** Almacena información completa de cada transacción realizada.

**Atributos:**
- `codigoProducto`, `nombreProducto`: identificación del producto vendido
- `cantidadVendida`: unidades vendidas
- `precioVenta`, `precioCompra`: precios al momento de la venta
- `fechaHora`: fecha y hora de la transacción (LocalDateTime)

**Métodos:**
- `getTotalVenta()`: retorna el ingreso total de la venta
- `getCostoVenta()`: retorna el costo de la venta
- `getGananciaVenta()`: retorna la ganancia neta de la venta

---

### 🏢 Clase GestorInventario

**Propósito:** Clase principal que gestiona todas las operaciones del inventario.

**Atributos:**
- `productos`: ArrayList de productos activos
- `ventas`: ArrayList de ventas registradas
- `CAPACIDAD_MAXIMA = 1000`: capacidad máxima del inventario

**Métodos principales:**

| Método | Función |
|--------|---------|
| `agregarProducto(p)` | Valida duplicados y agrega producto al inventario |
| `buscarPorCodigo(codigo)` | Localiza un producto por su código |
| `eliminarProducto(codigo)` | Elimina un producto del inventario |
| `actualizarProducto(...)` | Actualiza los datos de un producto existente |
| `registrarVenta(codigo, cantidad)` | Valida stock, descuenta y registra la venta |
| `calcularCapitalInvertido()` | Suma precio compra × cantidad de todos los productos |
| `calcularValorInventario()` | Suma precio venta × cantidad de todos los productos |
| `calcularTotalVentas()` | Suma el total de todas las ventas realizadas |
| `calcularGananciaNeta()` | Suma la ganancia neta de todas las ventas |

---

## 🎯 Conceptos de Programación Implementados

### 🔄 Herencia
- Clase abstracta `Producto` como padre
- Clases `ProductoFisico`, `ProductoDigital`, `ProductoServicio` como hijas
- Uso de `super()` en constructores

### 🔒 Encapsulamiento
- Atributos `protected` y `private`
- Métodos getter/setter para acceso controlado
- Validaciones en métodos públicos

### 🔄 Polimorfismo
- Manejo de diferentes tipos de productos como `Producto`
- Método `getTipo()` sobrescrito en cada subclase
- Método `toString()` sobrescrito

### 📊 Estructuras de Datos
- `ArrayList<Producto>` para productos activos
- `ArrayList<Venta>` para historial de ventas
- Uso de `LocalDateTime` para fechas de transacciones
- `ObservableList` de JavaFX para la tabla de la interfaz gráfica

### ⚠️ Manejo de Excepciones
- `try-catch` para entrada de datos en consola
- `NumberFormatException` para validar números
- Validaciones de campos vacíos y stock insuficiente

---

## 💻 Instrucciones para Ejecutar el Código

### ✅ Requisitos Previos

1. **JDK 21** — [Descargar Eclipse Temurin JDK 21](https://adoptium.net/)
2. **Visual Studio Code** con la extensión **Extension Pack for Java**
3. **JavaFX 21 SDK** — solo necesario para la interfaz gráfica

---

### 🖥️ Opción 1 — Solo Consola (sin JavaFX)

No requiere instalar JavaFX. Simplemente:

1. Abre el proyecto en VS Code
2. Ve al panel **Run and Debug** (`Ctrl+Shift+D`)
3. Selecciona `Consola - inventario` en el desplegable
4. Presiona ▶️

---

### 🎨 Opción 2 — Interfaz Gráfica JavaFX

#### Paso 1 — Descargar JavaFX

1. Ve a: [https://gluonhq.com/products/javafx/](https://gluonhq.com/products/javafx/)
2. Descarga **JavaFX 21 LTS** → **Windows x64** → tipo **SDK**
3. Extrae el zip en: `C:\javafx-sdk-21.0.11`

La estructura debe quedar así:
```
C:\javafx-sdk-21.0.11\
├── bin\
├── lib\
│   ├── javafx.controls.jar
│   ├── javafx.fxml.jar
│   └── ...
└── ...
```

#### Paso 2 — Ejecutar en VS Code

1. Abre el proyecto en VS Code
2. Ve al panel **Run and Debug** (`Ctrl+Shift+D`)
3. Selecciona `Interfaz - InventarioApp` en el desplegable
4. Presiona ▶️

> ⚠️ Si JavaFX está instalado en una ruta diferente, edita el archivo `.vscode/launch.json` y cambia `C:\\javafx-sdk-21.0.11\\lib` por tu ruta.

---

## 🎮 Manual de Usuario

### Consola — Menú Principal

```
1. Agregar producto
2. Ver productos
3. Editar producto
4. Eliminar producto
5. Registrar venta
6. Ver historial de ventas
7. Estado del inventario
8. Ver tarifas
9. Buscar producto
0. Salir
```

### Interfaz Gráfica — Botones

| Botón | Función |
|-------|---------|
| ➕ Agregar | Registra un nuevo producto |
| ✏️ Editar | Modifica un producto seleccionado |
| 🗑 Eliminar | Elimina el producto seleccionado |
| 💰 Vender | Registra una venta y descuenta stock |
| 🔍 Buscar | Localiza producto por código o nombre |
| 📋 Tarifas | Muestra tabla de IVA y márgenes |

### Características del Sistema

✅ Control de stock (máximo 1000 productos)  
✅ Prevención de códigos duplicados  
✅ Advertencia de margen bajo (menos del 10%)  
✅ Cálculo automático de IVA (19%)  
✅ Reportes financieros en tiempo real  
✅ Dos interfaces: consola y ventana gráfica  
✅ Historial completo de ventas con fecha y hora  

---

## 📂 Estructura del Proyecto

```
progrmacion_parcial_final\
├── .vscode\
│   └── launch.json        ← configuración de ejecución
├── src\
│   └── inventario\
│       ├── inventario.java      ← clases base + interfaz consola
│       └── InventarioApp.java   ← interfaz gráfica JavaFX
└── README.md
```