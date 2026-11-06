package org.example.practica4;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import java.io.InputStream;

public class EightOffGUI extends Application {
    private EightOffGame juego;

    private HBox panelReserva;
    private HBox panelCimentaciones;
    private HBox panelColumnas;
    private Label labelEstado;

    private static final double CARTA_ANCHO = 100;
    private static final double CARTA_ALTO = 145;
    private static final double DESPLAZAMIENTO_Y = 60; // Mayor espacio para ver símbolos y números
    private static final double SPACING_VBOX = DESPLAZAMIENTO_Y - CARTA_ALTO; // -85px

    @Override
    public void start(Stage primaryStage) {
        juego = new EightOffGame();
        juego.iniciarJuego();

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));
        root.setStyle("-fx-background-color: #0b6623;");

        HBox panelControles = new HBox(20);
        panelControles.setAlignment(Pos.CENTER);
        panelControles.setPadding(new Insets(10));

        Button btnReiniciar = new Button("Nuevo Juego");
        Button btnPista = new Button("Pista");
        Button btnDeshacer = new Button("Deshacer");

        String btnStyle = "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 8 16 8 16;";
        btnReiniciar.setStyle(btnStyle);
        btnPista.setStyle(btnStyle);
        btnDeshacer.setStyle(btnStyle);

        labelEstado = new Label("¡Bienvenido a Eight Off! Arrastra las cartas para moverlas.");
        labelEstado.setTextFill(Color.WHITE);
        labelEstado.setFont(Font.font("System", FontWeight.BOLD, 16));

        btnReiniciar.setOnAction(e -> {
            juego.iniciarJuego();
            actualizarTablero();
            labelEstado.setText("Nuevo juego iniciado.");
        });

        btnPista.setOnAction(e -> mostrarPista());

        btnDeshacer.setOnAction(e -> {
            if (juego.deshacerMovimiento()) {
                actualizarTablero();
                labelEstado.setText("Movimiento deshecho.");
            } else {
                labelEstado.setText("No hay movimientos para deshacer.");
            }
        });

        panelControles.getChildren().addAll(btnReiniciar, btnPista, btnDeshacer, labelEstado);
        root.setTop(panelControles);

        HBox panelSuperiorTablero = new HBox(50);
        panelSuperiorTablero.setAlignment(Pos.CENTER);
        panelSuperiorTablero.setPadding(new Insets(10));

        panelReserva = new HBox(12);
        panelCimentaciones = new HBox(12);

        panelSuperiorTablero.getChildren().addAll(
                crearSeccion("RESERVA (8)", panelReserva),
                crearSeccion("CIMENTACIÓN (4)", panelCimentaciones)
        );

        panelColumnas = new HBox(16);
        panelColumnas.setAlignment(Pos.TOP_CENTER);
        panelColumnas.setPadding(new Insets(15, 0, 20, 0));

        ScrollPane scrollColumnas = new ScrollPane(panelColumnas);
        scrollColumnas.setFitToWidth(true);
        scrollColumnas.setFitToHeight(true);
        scrollColumnas.setStyle("-fx-background: #0b6623; -fx-background-color: #0b6623; -fx-viewport-border: none;");

        VBox centroLayout = new VBox(15, panelSuperiorTablero, scrollColumnas);
        centroLayout.setAlignment(Pos.TOP_CENTER);
        root.setCenter(centroLayout);

        actualizarTablero();

        Scene scene = new Scene(root, 1280, 850);
        primaryStage.setTitle("Eight Off Solitaire - Práctica 4");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private VBox crearSeccion(String titulo, HBox contenedor) {
        Label lbl = new Label(titulo);
        lbl.setTextFill(Color.LIGHTGOLDENRODYELLOW);
        lbl.setFont(Font.font("System", FontWeight.BOLD, 14));
        VBox box = new VBox(6, lbl, contenedor);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private void actualizarTablero() {

        panelReserva.getChildren().clear();
        for (int i = 0; i < 8; i++) {
            final int idx = i;
            ListaSimple<Carta> res = juego.getReserva()[i];
            Carta carta = res.obtenerFinal();
            StackPane node = crearCartaView(carta, Movimiento.TipoOrigenDestino.RESERVA, idx, 0);
            configurarDestinoDrag(node, Movimiento.TipoOrigenDestino.RESERVA, idx);
            panelReserva.getChildren().add(node);
        }


        panelCimentaciones.getChildren().clear();
        for (int i = 0; i < 4; i++) {
            final int idx = i;
            ListaSimple<Carta> cim = juego.getCimentaciones()[i];
            Carta carta = cim.obtenerFinal();
            StackPane node = crearCartaView(carta, Movimiento.TipoOrigenDestino.CIMENTACION, idx, 0);
            configurarDestinoDrag(node, Movimiento.TipoOrigenDestino.CIMENTACION, idx);
            panelCimentaciones.getChildren().add(node);
        }


        panelColumnas.getChildren().clear();
        for (int i = 0; i < 8; i++) {
            final int idx = i;
            ListaSimple<Carta> col = juego.getColumnas()[i];

            VBox colBox = new VBox(SPACING_VBOX);
            colBox.setAlignment(Pos.TOP_CENTER);
            colBox.setPrefWidth(CARTA_ANCHO);

            if (col.estaVacia()) {
                StackPane vacio = crearCartaView(null, Movimiento.TipoOrigenDestino.COLUMNA, idx, 0);
                configurarDestinoDrag(vacio, Movimiento.TipoOrigenDestino.COLUMNA, idx);
                colBox.getChildren().add(vacio);
            } else {
                for (int c = 0; c < col.getTamano(); c++) {
                    Carta carta = col.obtener(c);
                    StackPane cardPane = crearCartaView(carta, Movimiento.TipoOrigenDestino.COLUMNA, idx, c);
                    configurarDestinoDrag(cardPane, Movimiento.TipoOrigenDestino.COLUMNA, idx);
                    colBox.getChildren().add(cardPane);
                }
            }
            panelColumnas.getChildren().add(colBox);
        }

        verificarEstadoJuego();
    }

    private StackPane crearCartaView(Carta carta, Movimiento.TipoOrigenDestino tipo, int origIdx, int posColumna) {
        StackPane cardNode = new StackPane();
        cardNode.setPrefSize(CARTA_ANCHO, CARTA_ALTO);
        cardNode.setMinSize(CARTA_ANCHO, CARTA_ALTO);
        cardNode.setMaxSize(CARTA_ANCHO, CARTA_ALTO);

        if (carta == null) {
            String rutaVacia = "/org/example/practica4/card_empty.png";
            InputStream streamVacia = getClass().getResourceAsStream(rutaVacia);

            if (streamVacia != null) {
                Image imgVacia = new Image(streamVacia);
                ImageView viewVacia = new ImageView(imgVacia);
                viewVacia.setFitWidth(CARTA_ANCHO);
                viewVacia.setFitHeight(CARTA_ALTO);
                cardNode.getChildren().add(viewVacia);
            } else {
                Rectangle rect = new Rectangle(CARTA_ANCHO, CARTA_ALTO);
                rect.setArcWidth(10); rect.setArcHeight(10);
                rect.setFill(Color.web("#0e4d1e")); rect.setStroke(Color.LIGHTGREEN);
                cardNode.getChildren().add(rect);
            }
        } else {
            String rankString = getRankFilename(carta.getValor());
            String suitString = carta.getPalo().getNombreFilename();
            String nombreArchivo = "card_" + suitString + "_" + rankString + ".png";
            String ruta = "/org/example/practica4/" + nombreArchivo;

            InputStream stream = getClass().getResourceAsStream(ruta);
            if (stream != null) {
                Image img = new Image(stream);
                ImageView view = new ImageView(img);
                view.setFitWidth(CARTA_ANCHO);
                view.setFitHeight(CARTA_ALTO);
                cardNode.getChildren().add(view);
            } else {
                Rectangle rect = new Rectangle(CARTA_ANCHO, CARTA_ALTO);
                rect.setFill(Color.WHITE); rect.setStroke(Color.BLACK); rect.setArcWidth(10); rect.setArcHeight(10);
                Label lbl = new Label(carta.toString());
                lbl.setFont(Font.font("Segoe UI", FontWeight.BOLD, 18));
                lbl.setTextFill((carta.getPalo() == Palo.CORAZON || carta.getPalo() == Palo.DIAMANTE) ? Color.RED : Color.BLACK);
                cardNode.getChildren().addAll(rect, lbl);
            }


            cardNode.setOnDragDetected(event -> {
                boolean dragValido = false;
                if (tipo == Movimiento.TipoOrigenDestino.COLUMNA) {
                    dragValido = juego.esSecuenciaValida(origIdx, posColumna);
                } else if (tipo == Movimiento.TipoOrigenDestino.RESERVA) {
                    dragValido = true;
                }

                if (dragValido) {
                    Dragboard db = cardNode.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(tipo.name() + ":" + origIdx + ":" + posColumna);
                    db.setContent(content);

                    db.setDragView(cardNode.snapshot(null, null));
                    event.consume();
                }
            });
        }

        return cardNode;
    }

    private void configurarDestinoDrag(StackPane target, Movimiento.TipoOrigenDestino destTipo, int destIdx) {
        target.setOnDragOver(event -> {
            if (event.getGestureSource() != target && event.getDragboard().hasString()) {
                event.acceptTransferModes(TransferMode.MOVE);
            }
            event.consume();
        });

        target.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean exito = false;

            if (db.hasString()) {
                String[] datos = db.getString().split(":");
                Movimiento.TipoOrigenDestino origTipo = Movimiento.TipoOrigenDestino.valueOf(datos[0]);
                int origIdx = Integer.parseInt(datos[1]);
                int posColumna = Integer.parseInt(datos[2]);

                if (origTipo == Movimiento.TipoOrigenDestino.COLUMNA) {
                    exito = juego.moverSecuencia(origIdx, posColumna, destTipo, destIdx);
                } else {
                    exito = juego.moverCarta(origTipo, origIdx, destTipo, destIdx);
                }

                if (exito) {
                    labelEstado.setText("Movimiento realizado con éxito.");
                } else {
                    labelEstado.setText("Movimiento no válido.");
                }
                actualizarTablero();
            }

            event.setDropCompleted(exito);
            event.consume();
        });
    }

    private String getRankFilename(int valor) {
        switch (valor) {
            case 1: return "A";
            case 11: return "J";
            case 12: return "Q";
            case 13: return "K";
            default:
                return String.format("%02d", valor);
        }
    }

    private void mostrarPista() {
        Movimiento pista = juego.obtenerPista();
        if (pista != null) {
            labelEstado.setText("PISTA: " + pista.toString());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Pista Disponible");
            alert.setHeaderText("Sugerencia de Movimiento");
            alert.setContentText(pista.toString());
            alert.showAndWait();
        } else {
            labelEstado.setText("No hay pistas disponibles.");
        }
    }

    private void verificarEstadoJuego() {
        if (juego.juegoGanado()) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("¡Felicidades!");
            alert.setHeaderText(null);
            alert.setContentText("¡Has ganado el juego de Eight Off!");
            alert.showAndWait();
        } else if (juego.esGameOver()) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Juego Terminado");
            alert.setHeaderText("Game Over");
            alert.setContentText("No quedan movimientos válidos posibles.");
            alert.showAndWait();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}