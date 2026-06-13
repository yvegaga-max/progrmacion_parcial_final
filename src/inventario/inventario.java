package inventario;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class inventario {
    public static void main(String[] args) {

        GestorInventario gestor = new GestorInventario();
        Scanner sc = new Scanner(System.in);
        int opcion;

        do {
            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║     SISTEMA DE INVENTARIO            ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  1. Agregar producto                 ║");
            System.out.println("║  2. Ver productos                    ║");
            System.out.println("║  3. Editar producto                  ║");
            System.out.println("║  4. Eliminar producto                ║");
            System.out.println("║  5. Registrar venta                  ║");
            System.out.println("║  6. Ver historial de ventas          ║");
            System.out.println("║  7. Estado del inventario            ║");
            System.out.println("║  8. Ver tarifas                      ║");
            System.out.println("║  9. Buscar producto                  ║");
            System.out.println("║  0. Salir                            ║");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.print("  Elige una opción: ");

            try { opcion = Integer.parseInt(sc.nextLine().trim()); }
            catch (NumberFormatException e) { opcion = -1; }

            switch (opcion) {
                case 1: agregarProducto(gestor, sc); break;
                case 2: gestor.mostrarProductos(); break;
                case 3: editarProducto(gestor, sc); break;
                case 4: eliminarProducto(gestor, sc); break;
                case 5: registrarVenta(gestor, sc); break;
                case 6: gestor.mostrarVentas(); break;
                case 7: gestor.mostrarEstado(); break;
                case 8: Tarifas.mostrarTarifas(); break;
                case 9: buscarProducto(gestor, sc); break;
                case 0: System.out.println("\n  ¡Hasta luego!\n"); break;
                default: System.out.println("  Opción no válida, intenta de nuevo.");
            }

        } while (opcion != 0);

        sc.close();
    }

    // ── Opción 1: Agregar ─────────────────────────────
    private static void agregarProducto(GestorInventario gestor, Scanner sc) {
        System.out.println("\n── Agregar Producto ──");
        System.out.print("  Código:        "); String cod = sc.nextLine().trim();
        System.out.print("  Nombre:        "); String nom = sc.nextLine().trim();
        System.out.println("  Tipo (1=Físico, 2=Digital, 3=Servicio): ");
        System.out.print("  Opción:        ");
        String tipo = sc.nextLine().trim();
        try {
            System.out.print("  Precio compra: ");
            double pC   = Double.parseDouble(sc.nextLine().trim());
            System.out.print("  Precio venta:  ");
            double pV   = Double.parseDouble(sc.nextLine().trim());
            System.out.print("  Cantidad:      ");
            int cant    = Integer.parseInt(sc.nextLine().trim());

            double margen = Tarifas.calcularMargen(pC, pV);
            if (margen < Tarifas.MARGEN_MINIMO * 100)
                System.out.printf("  ⚠  Margen bajo: %.1f%% (mínimo recomendado: 10%%)%n", margen);

            Producto p;
            if (tipo.equals("2"))      p = new ProductoDigital(cod, nom, pC, pV, cant);
            else if (tipo.equals("3")) p = new ProductoServicio(cod, nom, pC, pV, cant);
            else                       p = new ProductoFisico(cod, nom, pC, pV, cant);

            if (gestor.agregarProducto(p))
                System.out.println("  ✔  Producto agregado correctamente.");
            else
                System.out.println("  ✘  Ya existe un producto con ese código.");

        } catch (NumberFormatException e) {
            System.out.println("  ✘  Valor numérico inválido.");
        }
    }

    // ── Opción 3: Editar ──────────────────────────────
    private static void editarProducto(GestorInventario gestor, Scanner sc) {
        System.out.println("\n── Editar Producto ──");
        System.out.print("  Código del producto a editar: ");
        String cod = sc.nextLine().trim();
        Producto p = gestor.buscarPorCodigo(cod);
        if (p == null) { System.out.println("  ✘  Producto no encontrado."); return; }

        System.out.println("  Producto actual: " + p);
        System.out.print("  Nuevo nombre (Enter para mantener): ");       String nom  = sc.nextLine().trim();
        System.out.print("  Nuevo precio compra (0 para mantener): ");    String sPC  = sc.nextLine().trim();
        System.out.print("  Nuevo precio venta  (0 para mantener): ");    String sPV  = sc.nextLine().trim();
        System.out.print("  Nueva cantidad      (-1 para mantener): ");   String sCant= sc.nextLine().trim();

        try {
            double pC   = sPC.isEmpty()   ? 0  : Double.parseDouble(sPC);
            double pV   = sPV.isEmpty()   ? 0  : Double.parseDouble(sPV);
            int    cant = sCant.isEmpty()  ? -1 : Integer.parseInt(sCant);
            gestor.actualizarProducto(cod, nom, pC, pV, cant);
            System.out.println("  ✔  Producto actualizado.");
        } catch (NumberFormatException e) {
            System.out.println("  ✘  Valor numérico inválido.");
        }
    }

    // ── Opción 4: Eliminar ────────────────────────────
    private static void eliminarProducto(GestorInventario gestor, Scanner sc) {
        System.out.println("\n── Eliminar Producto ──");
        System.out.print("  Código del producto a eliminar: ");
        String cod = sc.nextLine().trim();
        if (gestor.eliminarProducto(cod))
            System.out.println("  ✔  Producto eliminado.");
        else
            System.out.println("  ✘  Producto no encontrado.");
    }

    // ── Opción 5: Vender ──────────────────────────────
    private static void registrarVenta(GestorInventario gestor, Scanner sc) {
        System.out.println("\n── Registrar Venta ──");
        System.out.print("  Código del producto: ");  String cod  = sc.nextLine().trim();
        System.out.print("  Cantidad a vender:   ");
        try {
            int cant = Integer.parseInt(sc.nextLine().trim());
            String res = gestor.registrarVenta(cod, cant);
            if (res.equals("OK")) System.out.println("  ✔  Venta registrada correctamente.");
            else                  System.out.println("  ✘  " + res);
        } catch (NumberFormatException e) {
            System.out.println("  ✘  Cantidad inválida.");
        }
    }

    // ── Opción 9: Buscar ──────────────────────────────
    private static void buscarProducto(GestorInventario gestor, Scanner sc) {
        System.out.println("\n── Buscar Producto ──");
        System.out.print("  Código o nombre: ");
        String term = sc.nextLine().trim().toLowerCase();
        boolean encontrado = false;
        for (Producto p : gestor.getProductos()) {
            if (p.getCodigo().toLowerCase().contains(term) ||
                p.getNombre().toLowerCase().contains(term)) {
                System.out.println("  " + p);
                encontrado = true;
            }
        }
        if (!encontrado) System.out.println("  ✘  No se encontró ningún producto.");
    }
}

// =============================================
// CLASE ABSTRACTA: Producto
// =============================================
abstract class Producto {
    protected String nombre;
    protected String codigo;
    protected double precioCompra;
    protected double precioVenta;
    protected int cantidad;

    public Producto(String codigo, String nombre, double precioCompra, double precioVenta, int cantidad) {
        this.codigo = codigo;
        this.nombre = nombre;
        this.precioCompra = precioCompra;
        this.precioVenta = precioVenta;
        this.cantidad = cantidad;
    }

    public String getCodigo()        { return codigo; }
    public String getNombre()        { return nombre; }
    public double getPrecioCompra()  { return precioCompra; }
    public double getPrecioVenta()   { return precioVenta; }
    public int    getCantidad()      { return cantidad; }

    public void setNombre(String n)       { this.nombre = n; }
    public void setPrecioCompra(double v) { this.precioCompra = v; }
    public void setPrecioVenta(double v)  { this.precioVenta = v; }
    public void setCantidad(int c)        { this.cantidad = c; }

    public abstract String getTipo();

    public double calcularGanancia() {
        return (precioVenta - precioCompra) * cantidad;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s (%s) | Compra: $%.0f | Venta: $%.0f | Cant: %d",
            codigo, nombre, getTipo(), precioCompra, precioVenta, cantidad);
    }
}

// =============================================
// CLASES HIJAS
// =============================================
class ProductoFisico extends Producto {
    public ProductoFisico(String codigo, String nombre, double precioCompra, double precioVenta, int cantidad) {
        super(codigo, nombre, precioCompra, precioVenta, cantidad);
    }
    @Override public String getTipo() { return "Físico"; }
}

class ProductoDigital extends Producto {
    public ProductoDigital(String codigo, String nombre, double precioCompra, double precioVenta, int cantidad) {
        super(codigo, nombre, precioCompra, precioVenta, cantidad);
    }
    @Override public String getTipo() { return "Digital"; }
}

class ProductoServicio extends Producto {
    public ProductoServicio(String codigo, String nombre, double precioCompra, double precioVenta, int cantidad) {
        super(codigo, nombre, precioCompra, precioVenta, cantidad);
    }
    @Override public String getTipo() { return "Servicio"; }
}

// =============================================
// CLASE: Tarifas
// =============================================
class Tarifas {
    public static final double MARGEN_MINIMO = 0.10;
    public static final double IVA           = 0.19;

    public static double calcularPrecioSugerido(double precioCompra, double margen) {
        if (margen < MARGEN_MINIMO) {
            System.out.println("Advertencia: el margen es menor al mínimo recomendado (" + (MARGEN_MINIMO * 100) + "%)");
        }
        return precioCompra * (1 + margen);
    }

    public static double calcularPrecioConIva(double precio) {
        return precio * (1 + IVA);
    }

    public static double calcularMargen(double precioCompra, double precioVenta) {
        if (precioCompra == 0) return 0;
        return ((precioVenta - precioCompra) / precioCompra) * 100;
    }

    public static String obtenerResumenTarifas() {
        return String.format(
            "IVA aplicado: %.0f%%\nMargen mínimo recomendado: %.0f%%",
            IVA * 100, MARGEN_MINIMO * 100
        );
    }

    public static void mostrarTarifas() {
        System.out.println("         TABLA DE TARIFAS              ");
        System.out.printf(" IVA aplicado:         %5.0f%%         %n", IVA * 100);
        System.out.printf(" Margen mínimo recom.: %5.0f%%         %n", MARGEN_MINIMO * 100);
    }
}

// =============================================
// CLASE: Venta
// =============================================
class Venta {
    private final String codigoProducto;
    private final String nombreProducto;
    private final int cantidadVendida;
    private final double precioVenta;
    private final double precioCompra;
    private final LocalDateTime fechaHora;

    public Venta(String codigoProducto, String nombreProducto, int cantidadVendida,
                 double precioVenta, double precioCompra) {
        this.codigoProducto  = codigoProducto;
        this.nombreProducto  = nombreProducto;
        this.cantidadVendida = cantidadVendida;
        this.precioVenta     = precioVenta;
        this.precioCompra    = precioCompra;
        this.fechaHora       = LocalDateTime.now();
    }

    public String        getCodigoProducto()  { return codigoProducto; }
    public String        getNombreProducto()  { return nombreProducto; }
    public int           getCantidadVendida() { return cantidadVendida; }
    public LocalDateTime getFechaHora()       { return fechaHora; }
    public double        getTotalVenta()      { return precioVenta * cantidadVendida; }
    public double        getCostoVenta()      { return precioCompra * cantidadVendida; }
    public double        getGananciaVenta()   { return getTotalVenta() - getCostoVenta(); }

    @Override
    public String toString() {
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");
        return String.format("[%s] %s x%d | Total: $%.0f | Ganancia: $%.0f | %s",
            codigoProducto, nombreProducto, cantidadVendida,
            getTotalVenta(), getGananciaVenta(), fechaHora.format(fmt));
    }
}

// =============================================
// CLASE: GestorInventario
// =============================================
class GestorInventario {

    private final List<Producto> productos = new ArrayList<>();
    private final List<Venta>    ventas    = new ArrayList<>();
    public static final int CAPACIDAD_MAXIMA = 1000;

    public List<Producto> getProductos() { return productos; }
    public List<Venta>    getVentas()    { return ventas; }

    public boolean agregarProducto(Producto p) {
        if (productos.size() >= CAPACIDAD_MAXIMA) {
            System.out.println("Error: se alcanzó la capacidad máxima del inventario (" + CAPACIDAD_MAXIMA + ").");
            return false;
        }
        if (buscarPorCodigo(p.getCodigo()) != null) return false;
        productos.add(p);
        return true;
    }

    public Producto buscarPorCodigo(String codigo) {
        for (Producto p : productos)
            if (p.getCodigo().equalsIgnoreCase(codigo.trim())) return p;
        return null;
    }

    public boolean eliminarProducto(String codigo) {
        Producto p = buscarPorCodigo(codigo);
        if (p == null) return false;
        productos.remove(p);
        return true;
    }

    public boolean actualizarProducto(String codigo, String nombre,
                                      double precioCompra, double precioVenta, int cantidad) {
        Producto p = buscarPorCodigo(codigo);
        if (p == null) return false;
        if (nombre != null && !nombre.isEmpty()) p.setNombre(nombre);
        if (precioCompra > 0) p.setPrecioCompra(precioCompra);
        if (precioVenta  > 0) p.setPrecioVenta(precioVenta);
        if (cantidad >= 0)    p.setCantidad(cantidad);
        return true;
    }

    public String registrarVenta(String codigo, int cantidad) {
        Producto p = buscarPorCodigo(codigo);
        if (p == null)                  return "Producto no encontrado.";
        if (cantidad <= 0)              return "La cantidad debe ser mayor a cero.";
        if (p.getCantidad() < cantidad) return "Stock insuficiente. Disponible: " + p.getCantidad();
        p.setCantidad(p.getCantidad() - cantidad);
        ventas.add(new Venta(p.getCodigo(), p.getNombre(), cantidad, p.getPrecioVenta(), p.getPrecioCompra()));
        return "OK";
    }

    public double calcularCapitalInvertido() {
        double total = 0;
        for (Producto p : productos) total += p.getPrecioCompra() * p.getCantidad();
        return total;
    }

    public double calcularValorInventario() {
        double total = 0;
        for (Producto p : productos) total += p.getPrecioVenta() * p.getCantidad();
        return total;
    }

    public double calcularTotalVentas() {
        double total = 0;
        for (Venta v : ventas) total += v.getTotalVenta();
        return total;
    }

    public double calcularGananciaNeta() {
        double total = 0;
        for (Venta v : ventas) total += v.getGananciaVenta();
        return total;
    }

    public void mostrarEstado() {
        System.out.println("       ESTADO DEL INVENTARIO          ");
        System.out.printf("  Productos registrados: %5d          %n", productos.size());
        System.out.printf("  Ventas realizadas:     %5d         %n", ventas.size());
        System.out.printf("  Capital invertido:  $%,10.0f       %n", calcularCapitalInvertido());
        System.out.printf("  Valor inventario:   $%,10.0f       %n", calcularValorInventario());
        System.out.printf("  Total vendido:      $%,10.0f       %n", calcularTotalVentas());
        System.out.printf("  Ganancia neta:      $%,10.0f       %n", calcularGananciaNeta());
    }

    public void mostrarProductos() {
        if (productos.isEmpty()) {
            System.out.println("No hay productos registrados.");
            return;
        }
        System.out.println("\n── Productos en inventario ──────────────────────────────────────────");
        for (Producto p : productos) System.out.println("  " + p.toString());
        System.out.println("─────────────────────────────────────────────────────────────────────");
    }

    public void mostrarVentas() {
        if (ventas.isEmpty()) {
            System.out.println("No hay ventas registradas.");
            return;
        }
        System.out.println("\n── Historial de ventas ──────────────────────────────────────────────");
        for (Venta v : ventas) System.out.println("  " + v.toString());
        System.out.println("─────────────────────────────────────────────────────────────────────");
    }
}