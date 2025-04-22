package com.retailsoft.utils;

import com.retailsoft.dto.ComandaDTO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class PrinterUtil {

    @Value("${retailsoft.app.printer.name}")
    private String printerName;

    // Ancho del papel - caracteres por línea para papel de 58mm
    private static final int CARACTERES_POR_LINEA = 32;

    public String generarTextoComanda(ComandaDTO comanda) {
        List<String> lineas = new ArrayList<>();

        lineas.add("====== COMANDA ======");
        lineas.add("Pedido #: " + comanda.getPedidoId());
        lineas.add("Mesa: " + comanda.getMesa());
        lineas.add("Mesero: " + comanda.getMesero());
        lineas.add(comanda.getFechaHora().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")));
        lineas.add("-------------------------");

        for (ComandaDTO.ItemComandaDTO item : comanda.getItems()) {
            lineas.add(item.getCategoria());
            lineas.add(item.getCantidad() + " x " + item.getProductoNombre());

            if (!item.getIngredientesAdicionales().isEmpty()) {
                lineas.add("  + " + String.join(", ", item.getIngredientesAdicionales()));
            }

            if (!item.getIngredientesEliminados().isEmpty()) {
                lineas.add("  - " + String.join(", ", item.getIngredientesEliminados()));
            }

            if (item.getObservaciones() != null && !item.getObservaciones().isEmpty()) {
                lineas.add("  Obs: " + item.getObservaciones());
            }

            lineas.add("-------------------------");
        }

        lineas.add("---- FIN DE COMANDA ----");
        return String.join("\n", lineas);
    }

    // Método para centrar texto en papel de 58mm
    private String centrarTexto(String texto) {
        if (texto.length() >= CARACTERES_POR_LINEA) {
            return texto;
        }

        int espaciosNecesarios = CARACTERES_POR_LINEA - texto.length();
        int espaciosIzquierda = espaciosNecesarios / 2;

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < espaciosIzquierda; i++) {
            sb.append(' ');
        }

        sb.append(texto);
        return sb.toString();
    }

    // Método para generar línea separadora
    private String generarLinea(char caracter) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < CARACTERES_POR_LINEA; i++) {
            sb.append(caracter);
        }
        return sb.toString();
    }

    // Método para dividir listas de ingredientes en líneas que quepan en el papel
    private List<String> dividirIngredientes(String prefijo, List<String> ingredientes) {
        List<String> lineas = new ArrayList<>();
        StringBuilder lineaActual = new StringBuilder(prefijo);

        for (int i = 0; i < ingredientes.size(); i++) {
            String ingrediente = ingredientes.get(i);

            // Verificar si agregando este ingrediente excedemos el ancho
            if (lineaActual.length() + ingrediente.length() + 2 > CARACTERES_POR_LINEA) {
                // Si excede, agregar línea actual y empezar nueva línea
                lineas.add(lineaActual.toString());
                lineaActual = new StringBuilder("   " + ingrediente);
            } else {
                // Si no excede, agregar a línea actual
                if (lineaActual.length() > prefijo.length()) {
                    lineaActual.append(", ");
                }
                lineaActual.append(ingrediente);
            }

            // Si es el último ingrediente, agregar la línea final
            if (i == ingredientes.size() - 1) {
                lineas.add(lineaActual.toString());
            }
        }

        return lineas;
    }
}
