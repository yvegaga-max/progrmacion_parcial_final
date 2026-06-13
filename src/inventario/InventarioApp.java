package inventario;

import java.text.NumberFormat;
import java.util.Locale;

import javafx.application.Application;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.*;

// =============================================
// CLASE PRINCIPAL: InventarioApp (JavaFX)
// =============================================
public class InventarioApp extends Application {

    private final GestorInventario gestor = new GestorInventario();
    private final ObservableList<ProductoFila> filaProductos = FXCollections.observableArrayList();
    private TableView<ProductoFila> tabla;
    private Label lblCapital, lblValor, lblVentas, lblGanancia, lblTotal;
    private ListView<String> listaVentas;

    @SuppressWarnings("deprecation")
    private static final NumberFormat MONEDA = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

    // ── Modelo observable para la tabla ──────────────
    public static class ProductoFila {
        private final StringProperty  codigo, nombre, tipo;
        private final DoubleProperty  precioCompra, precioVenta, ganancia;
        private final IntegerProperty cantidad;

        public ProductoFila(Producto p) {
            codigo       = new SimpleStringProperty(p.getCodigo());
            nombre       = new SimpleStringProperty(p.getNombre());
            tipo         = new SimpleStringProperty(p.getTipo());
            precioCompra = new SimpleDoubleProperty(p.getPrecioCompra());
            precioVenta  = new SimpleDoubleProperty(p.getPrecioVenta());
            cantidad     = new SimpleIntegerProperty(p.getCantidad());
            ganancia     = new SimpleDoubleProperty(p.calcularGanancia());
        }

        public StringProperty  codigoProperty()      { return codigo; }
        public StringProperty  nombreProperty()       { return nombre; }
        public StringProperty  tipoProperty()         { return tipo; }
        public DoubleProperty  precioCompraProperty() { return precioCompra; }
        public DoubleProperty  precioVentaProperty()  { return precioVenta; }
        public IntegerProperty cantidadProperty()     { return cantidad; }
        public DoubleProperty  gananciaProperty()     { return ganancia; }
        public String getCodigo() { return codigo.get(); }
    }

    // ── Arranque ──────────────────────────────────────
    @Override
    public void start(Stage stage) {
        stage.setTitle("Sistema de Inventario SAMADIGITAL");
        stage.setMinWidth(1000);
        stage.setMinHeight(650);

        BorderPane root = new BorderPane();
        root.setStyle("-fx-background-color: #0f172a;");
        root.setTop(construirHeader());
        root.setCenter(construirCentro());
        root.setBottom(construirFooter());

        stage.setScene(new Scene(root, 1100, 700));
        stage.show();

        cargarDatosEjemplo();
        refrescar();
    }

    // ── Header ────────────────────────────────────────
    private HBox construirHeader() {
        HBox header = new HBox(12);
        header.setStyle("-fx-background-color: #1e293b; -fx-padding: 16 24;");
        header.setAlignment(Pos.CENTER_LEFT);

        Label icono = new Label("📦");
        icono.setStyle("-fx-font-size: 24px;");

        VBox titulos = new VBox(2);
        Label t1 = new Label("Sistema de Inventario SAMADIGITAL");
        t1.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #f1f5f9;");
        Label t2 = new Label("Gestión completa de productos y ventas");
        t2.setStyle("-fx-font-size: 12px; -fx-text-fill: #94a3b8;");
        titulos.getChildren().addAll(t1, t2);

        header.getChildren().addAll(icono, titulos);
        return header;
    }

    // ── Centro ────────────────────────────────────────
    private SplitPane construirCentro() {
        SplitPane split = new SplitPane();
        split.setStyle("-fx-background-color: #0f172a;");

        VBox izq = new VBox(12);
        izq.setPadding(new Insets(16));
        izq.setStyle("-fx-background-color: #0f172a;");
        tabla = construirTabla();
        izq.getChildren().addAll(construirBotones(), tabla);
        VBox.setVgrow(tabla, Priority.ALWAYS);

        split.getItems().addAll(izq, construirPanelDerecho());
        split.setDividerPositions(0.68);
        return split;
    }

    // ── Botones ───────────────────────────────────────
    private HBox construirBotones() {
        HBox barra = new HBox(8);
        barra.setAlignment(Pos.CENTER_LEFT);

        Button btnAgregar  = boton("➕ Agregar",  "#22c55e", "#166534");
        Button btnEditar   = boton("✏️ Editar",   "#3b82f6", "#1d4ed8");
        Button btnEliminar = boton("🗑 Eliminar", "#ef4444", "#991b1b");
        Button btnVender   = boton("💰 Vender",   "#f59e0b", "#92400e");
        Button btnBuscar   = boton("🔍 Buscar",   "#8b5cf6", "#5b21b6");
        Button btnTarifas  = boton("📋 Tarifas",  "#0891b2", "#164e63");

        btnAgregar.setOnAction(e  -> dialogoAgregar());
        btnEditar.setOnAction(e   -> dialogoEditar());
        btnEliminar.setOnAction(e -> eliminarSeleccionado());
        btnVender.setOnAction(e   -> dialogoVender());
        btnBuscar.setOnAction(e   -> dialogoBuscar());
        btnTarifas.setOnAction(e  -> mostrarTarifas());

        barra.getChildren().addAll(btnAgregar, btnEditar, btnEliminar, btnVender, btnBuscar, btnTarifas);
        return barra;
    }

    private Button boton(String texto, String bg, String hover) {
        Button b = new Button(texto);
        String base = String.format(
            "-fx-background-color: %s; -fx-text-fill: white; -fx-font-weight: bold;" +
            "-fx-font-size: 12px; -fx-padding: 8 14; -fx-background-radius: 6; -fx-cursor: hand;", bg);
        b.setStyle(base);
        b.setOnMouseEntered(e -> b.setStyle(base.replace(bg, hover)));
        b.setOnMouseExited(e  -> b.setStyle(base));
        return b;
    }

    // ── Tabla ─────────────────────────────────────────
    private TableView<ProductoFila> construirTabla() {
        TableView<ProductoFila> tv = new TableView<>(filaProductos);
        tv.setStyle("-fx-background-color: #1e293b; -fx-table-cell-border-color: #334155; -fx-font-size: 13px;");
        Label ph = new Label("No hay productos. Usa ➕ Agregar para comenzar.");
        ph.setStyle("-fx-text-fill: #64748b; -fx-font-size: 14px;");
        tv.setPlaceholder(ph);

        tv.getColumns().addAll(
            col("Código",     "codigo",       90),
            col("Nombre",     "nombre",      160),
            col("Tipo",       "tipo",         80),
            colMoneda("P. Compra", "precioCompra", 110),
            colMoneda("P. Venta",  "precioVenta",  110),
            colEntero("Cantidad",  "cantidad",      80),
            colMoneda("Ganancia",  "ganancia",     110)
        );

        tv.setRowFactory(t -> {
            TableRow<ProductoFila> row = new TableRow<>();
            row.setStyle("-fx-background-color: #1e3a52;");
            row.selectedProperty().addListener((o, old, sel) ->
                row.setStyle(sel ? "-fx-background-color: #2563eb;" : "-fx-background-color: #1e3a52;")
            );
            return row;
        });
        return tv;
    }

    private TableColumn<ProductoFila, String> col(String titulo, String campo, int ancho) {
        TableColumn<ProductoFila, String> c = new TableColumn<>(titulo);
        c.setCellValueFactory(new PropertyValueFactory<>(campo));
        c.setPrefWidth(ancho);
        c.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setText(null); return; }
                setText(val);
                setStyle("-fx-text-fill: #e2e8f0; -fx-background-color: transparent;");
            }
        });
        return c;
    }

    private TableColumn<ProductoFila, Double> colMoneda(String titulo, String campo, int ancho) {
        TableColumn<ProductoFila, Double> c = new TableColumn<>(titulo);
        c.setCellValueFactory(new PropertyValueFactory<>(campo));
        c.setPrefWidth(ancho);
        c.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Double val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setText(null); return; }
                setText(MONEDA.format(val));
                setStyle("-fx-text-fill: #86efac; -fx-alignment: CENTER-RIGHT;");
            }
        });
        return c;
    }

    private TableColumn<ProductoFila, Integer> colEntero(String titulo, String campo, int ancho) {
        TableColumn<ProductoFila, Integer> c = new TableColumn<>(titulo);
        c.setCellValueFactory(new PropertyValueFactory<>(campo));
        c.setPrefWidth(ancho);
        c.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(Integer val, boolean empty) {
                super.updateItem(val, empty);
                if (empty || val == null) { setText(null); return; }
                setText(String.valueOf(val));
                setStyle("-fx-text-fill: " + (val <= 5 ? "#fca5a5" : "#e2e8f0") + "; -fx-alignment: CENTER;");
            }
        });
        return c;
    }

    // ── Panel derecho ─────────────────────────────────
    private VBox construirPanelDerecho() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(16));
        panel.setStyle("-fx-background-color: #0f172a;");

        Label titulo = new Label("📊 Resumen");
        titulo.setStyle("-fx-text-fill: #f1f5f9; -fx-font-size: 16px; -fx-font-weight: bold;");

        lblCapital  = lv("$0");
        lblValor    = lv("$0");
        lblVentas   = lv("$0");
        lblGanancia = lv("$0");
        lblTotal    = lv("0 productos");

        VBox tarjetas = new VBox(8);
        tarjetas.getChildren().addAll(
            tarjeta("💼 Capital invertido", lblCapital,  "#1e3a5f"),
            tarjeta("🏷️ Valor inventario",  lblValor,    "#1a3a2a"),
            tarjeta("💳 Total vendido",     lblVentas,   "#3b2a1a"),
            tarjeta("📈 Ganancia neta",     lblGanancia, "#2a1a3a"),
            tarjeta("📦 Productos",         lblTotal,    "#1e293b")
        );

        Label tVentas = new Label("🧾 Ventas recientes");
        tVentas.setStyle("-fx-text-fill: #f1f5f9; -fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 0 0 0;");

        listaVentas = new ListView<>();
        listaVentas.setStyle("-fx-background-color: #1e293b; -fx-text-fill: #e2e8f0; -fx-font-size: 12px;");
        listaVentas.setPrefHeight(180);
        VBox.setVgrow(listaVentas, Priority.ALWAYS);

        panel.getChildren().addAll(titulo, tarjetas, tVentas, listaVentas);
        return panel;
    }

    private VBox tarjeta(String titulo, Label valor, String color) {
        VBox card = new VBox(4);
        card.setStyle(String.format("-fx-background-color: %s; -fx-background-radius: 8; -fx-padding: 10 14;", color));
        Label lbl = new Label(titulo);
        lbl.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 11px;");
        card.getChildren().addAll(lbl, valor);
        return card;
    }

    private Label lv(String texto) {
        Label l = new Label(texto);
        l.setStyle("-fx-text-fill: #f1f5f9; -fx-font-size: 15px; -fx-font-weight: bold;");
        return l;
    }

    // ── Footer ────────────────────────────────────────
    private HBox construirFooter() {
        HBox footer = new HBox();
        footer.setStyle("-fx-background-color: #1e293b; -fx-padding: 8 24;");
        footer.setAlignment(Pos.CENTER_LEFT);
        Label lbl = new Label("Sistema de Inventario v1.0  •  Datos en memoria durante la sesión");
        lbl.setStyle("-fx-text-fill: #475569; -fx-font-size: 11px;");
        footer.getChildren().add(lbl);
        return footer;
    }

    // ── Diálogos ──────────────────────────────────────
    private void dialogoAgregar() {
        Dialog<ButtonType> dlg = dlg("Agregar Producto", "Nuevo producto al inventario");
        GridPane grid = grid();

        TextField fCodigo = tf("Ej: P001"), fNombre = tf("Nombre"),
                  fCompra = tf("0.00"),     fVenta  = tf("0.00"), fCant = tf("0");
        ComboBox<String> cbTipo = new ComboBox<>(
            FXCollections.observableArrayList("Físico", "Digital", "Servicio"));
        cbTipo.setValue("Físico");
        cbTipo.setStyle("-fx-background-color: #334155; -fx-text-fill: #f1f5f9;");

        grid.addRow(0, et("Código:"),        fCodigo);
        grid.addRow(1, et("Nombre:"),        fNombre);
        grid.addRow(2, et("Tipo:"),          cbTipo);
        grid.addRow(3, et("Precio Compra:"), fCompra);
        grid.addRow(4, et("Precio Venta:"),  fVenta);
        grid.addRow(5, et("Cantidad:"),      fCant);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        estilizarBotones(dlg);

        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            try {
                String cod = fCodigo.getText().trim();
                String nom = fNombre.getText().trim();
                double pC  = Double.parseDouble(fCompra.getText().trim());
                double pV  = Double.parseDouble(fVenta.getText().trim());
                int cant   = Integer.parseInt(fCant.getText().trim());

                if (cod.isEmpty() || nom.isEmpty()) {
                    alerta("Error", "Código y nombre son obligatorios.");
                    return;
                }

                double margen = Tarifas.calcularMargen(pC, pV);
                String aviso  = margen < (Tarifas.MARGEN_MINIMO * 100)
                    ? "\n⚠️ Margen bajo: " + String.format("%.1f%%", margen) + " (mínimo recomendado: 10%)" : "";

                Producto p;
                switch (cbTipo.getValue()) {
                    case "Digital":  p = new ProductoDigital(cod, nom, pC, pV, cant);  break;
                    case "Servicio": p = new ProductoServicio(cod, nom, pC, pV, cant); break;
                    default:         p = new ProductoFisico(cod, nom, pC, pV, cant);
                }

                if (!gestor.agregarProducto(p)) {
                    alerta("Error", "Ya existe el código: " + cod);
                } else {
                    if (!aviso.isEmpty()) alerta("Producto agregado", "Producto registrado." + aviso);
                    refrescar();
                }
            } catch (NumberFormatException ex) {
                alerta("Error", "Ingresa valores numéricos válidos.");
            }
        });
    }

    private void dialogoEditar() {
        ProductoFila sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) { alerta("Aviso", "Selecciona un producto de la tabla."); return; }
        Producto p = gestor.buscarPorCodigo(sel.getCodigo());
        if (p == null) return;

        Dialog<ButtonType> dlg = dlg("Editar Producto", "Editando: " + p.getNombre());
        GridPane grid = grid();
        TextField fNombre = tf(p.getNombre()),
                  fCompra = tf(String.valueOf(p.getPrecioCompra())),
                  fVenta  = tf(String.valueOf(p.getPrecioVenta())),
                  fCant   = tf(String.valueOf(p.getCantidad()));

        grid.addRow(0, et("Nombre:"),        fNombre);
        grid.addRow(1, et("Precio Compra:"), fCompra);
        grid.addRow(2, et("Precio Venta:"),  fVenta);
        grid.addRow(3, et("Cantidad:"),      fCant);

        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        estilizarBotones(dlg);

        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            try {
                gestor.actualizarProducto(
                    p.getCodigo(),
                    fNombre.getText().trim(),
                    Double.parseDouble(fCompra.getText().trim()),
                    Double.parseDouble(fVenta.getText().trim()),
                    Integer.parseInt(fCant.getText().trim())
                );
                refrescar();
            } catch (NumberFormatException ex) {
                alerta("Error", "Valores numéricos inválidos.");
            }
        });
    }

    private void eliminarSeleccionado() {
        ProductoFila sel = tabla.getSelectionModel().getSelectedItem();
        if (sel == null) { alerta("Aviso", "Selecciona un producto de la tabla."); return; }

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
            "¿Eliminar '" + sel.nombreProperty().get() + "'?", ButtonType.YES, ButtonType.NO);
        confirm.setTitle("Confirmar"); confirm.setHeaderText(null);
        confirm.getDialogPane().setStyle("-fx-background-color: #1e293b;");

        confirm.showAndWait().ifPresent(bt -> {
            if (bt == ButtonType.YES) { gestor.eliminarProducto(sel.getCodigo()); refrescar(); }
        });
    }

    private void dialogoVender() {
        ProductoFila sel = tabla.getSelectionModel().getSelectedItem();
        Dialog<ButtonType> dlg = dlg("Registrar Venta", "Datos de la venta");
        GridPane grid = grid();
        TextField fCodigo = tf(sel != null ? sel.getCodigo() : ""), fCant = tf("1");
        grid.addRow(0, et("Código:"),   fCodigo);
        grid.addRow(1, et("Cantidad:"), fCant);
        dlg.getDialogPane().setContent(grid);
        dlg.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        estilizarBotones(dlg);

        dlg.showAndWait().ifPresent(bt -> {
            if (bt != ButtonType.OK) return;
            try {
                String res = gestor.registrarVenta(
                    fCodigo.getText().trim(), Integer.parseInt(fCant.getText().trim()));
                if (!res.equals("OK")) alerta("Error", res);
                else refrescar();
            } catch (NumberFormatException ex) {
                alerta("Error", "Cantidad inválida.");
            }
        });
    }

    private void dialogoBuscar() {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Buscar"); dlg.setHeaderText("Buscar por código o nombre"); dlg.setContentText("Término:");
        dlg.getDialogPane().setStyle("-fx-background-color: #1e293b;");

        dlg.showAndWait().ifPresent(t -> {
            String term = t.toLowerCase().trim();
            for (ProductoFila f : filaProductos) {
                if (f.codigoProperty().get().toLowerCase().contains(term) ||
                    f.nombreProperty().get().toLowerCase().contains(term)) {
                    tabla.getSelectionModel().select(f);
                    tabla.scrollTo(f);
                    return;
                }
            }
            alerta("Sin resultados", "No se encontró: " + t);
        });
    }

    private void mostrarTarifas() {
        String info = Tarifas.obtenerResumenTarifas()
            + String.format("\n\nEjemplo con precio de compra $100.000:\n"
                + "  Precio sugerido (30%% margen): %s\n"
                + "  Con IVA incluido: %s",
                MONEDA.format(Tarifas.calcularPrecioSugerido(100000, 0.30)),
                MONEDA.format(Tarifas.calcularPrecioConIva(Tarifas.calcularPrecioSugerido(100000, 0.30))));
        alerta("Tabla de Tarifas", info);
    }

    // ── Helpers ───────────────────────────────────────
    private void refrescar() {
        filaProductos.clear();
        gestor.getProductos().forEach(p -> filaProductos.add(new ProductoFila(p)));

        lblCapital.setText(MONEDA.format(gestor.calcularCapitalInvertido()));
        lblValor.setText(MONEDA.format(gestor.calcularValorInventario()));
        lblVentas.setText(MONEDA.format(gestor.calcularTotalVentas()));
        lblGanancia.setText(MONEDA.format(gestor.calcularGananciaNeta()));
        lblTotal.setText(gestor.getProductos().size() + " productos");

        ObservableList<String> items = FXCollections.observableArrayList();
        gestor.getVentas().forEach(v -> items.add(0,
            v.getNombreProducto() + " — x" + v.getCantidadVendida() + " — " + MONEDA.format(v.getTotalVenta())));
        listaVentas.setItems(items);
    }

    private Dialog<ButtonType> dlg(String titulo, String header) {
        Dialog<ButtonType> d = new Dialog<>();
        d.setTitle(titulo); d.setHeaderText(header);
        d.getDialogPane().setStyle("-fx-background-color: #1e293b;");
        return d;
    }

    private void estilizarBotones(Dialog<?> dlg) {
        dlg.getDialogPane().lookupButton(ButtonType.OK).setStyle(
            "-fx-background-color: #22c55e; -fx-text-fill: white; -fx-font-weight: bold;");
        dlg.getDialogPane().lookupButton(ButtonType.CANCEL).setStyle(
            "-fx-background-color: #475569; -fx-text-fill: white;");
    }

    private GridPane grid() {
        GridPane g = new GridPane();
        g.setHgap(12); g.setVgap(10); g.setPadding(new Insets(16));
        g.setStyle("-fx-background-color: #1e293b;");
        g.getColumnConstraints().addAll(new ColumnConstraints(130), new ColumnConstraints(220));
        return g;
    }

    private TextField tf(String valor) {
        TextField tf = new TextField(valor);
        tf.setStyle("-fx-background-color: #334155; -fx-text-fill: #f1f5f9;" +
                    "-fx-border-color: #475569; -fx-border-radius: 4; -fx-background-radius: 4; -fx-padding: 6 10;");
        return tf;
    }

    private Label et(String texto) {
        Label l = new Label(texto);
        l.setStyle("-fx-text-fill: #94a3b8; -fx-font-size: 13px;");
        return l;
    }

    private void alerta(String titulo, String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle(titulo); a.setHeaderText(null); a.setContentText(msg);
        a.getDialogPane().setStyle("-fx-background-color: #1e293b;");
        a.showAndWait();
    }

    // ── Datos de ejemplo ──────────────────────────────
    private void cargarDatosEjemplo() {
        gestor.agregarProducto(new ProductoFisico("P001",  "Teclado Mecánico",  80000, 150000, 15));
        gestor.agregarProducto(new ProductoFisico("P002",  "Mouse Gamer",       45000,  90000, 22));
        gestor.agregarProducto(new ProductoDigital("D001", "Licencia Office",  120000, 220000, 50));
        gestor.agregarProducto(new ProductoServicio("S001","Mantenimiento PC",  30000,  80000, 10));
        gestor.registrarVenta("P001", 3);
        gestor.registrarVenta("D001", 5);
    }

    public static void main(String[] args) {
        launch(args);
    }
}