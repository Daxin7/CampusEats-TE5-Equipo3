package campuseats.metricas;

import campuseats.modelo.LineaAnalitica;
import java.math.BigDecimal;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TicketPromedio implements Metrica {

    @Override
    public String nombre() {
        return "Ticket promedio";
    }

    @Override
    public Object calcular(List<?> datos) {
        BigDecimal totalVentas = BigDecimal.ZERO;
        Set<String> pedidosUnicos = new HashSet<>();

        for (Object obj : datos) {
            if (obj instanceof LineaAnalitica analitica) {
                if (analitica.pedido() != null) {
                    pedidosUnicos.add(analitica.pedido().numeroPedido());
                }
                if (analitica.linea() != null && analitica.linea().precioUnitario() != null) {
                    BigDecimal precio = analitica.linea().precioUnitario();
                    int cantidad = analitica.linea().cantidad();
                    // Multiplicamos precioUnitario * cantidad usando BigDecimal
                    BigDecimal subtotal = precio.multiply(BigDecimal.valueOf(cantidad));
                    totalVentas = totalVentas.add(subtotal);
                }
            }
        }

        if (pedidosUnicos.isEmpty()) {
            return "0,00 dólares por pedido";
        }

        // Calculamos el promedio dividiendo el total entre la cantidad de pedidos únicos
        BigDecimal ticketPromedio = totalVentas.divide(
                BigDecimal.valueOf(pedidosUnicos.size()), 
                2, 
                java.math.RoundingMode.HALF_UP
        );

        return String.format("%.2f dólares por pedido", ticketPromedio.doubleValue()).replace(".", ",");
    }
}