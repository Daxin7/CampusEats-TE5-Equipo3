package campuseats.metricas;

import campuseats.modelo.LineaAnalitica;
import campuseats.modelo.LineaPedido;
import java.math.BigDecimal;
import java.util.List;

public class GananciaTotal implements Metrica {

    @Override
    public String nombre() {
        return "Ganancia total acumulada";
    }

    @Override
    public Object calcular(List<?> datos) {
        BigDecimal gananciaTotal = BigDecimal.ZERO;

        for (Object obj : datos) {
            LineaPedido linea = null;

            if (obj instanceof LineaAnalitica analitica) {
                linea = analitica.linea();
            } else if (obj instanceof LineaPedido pedidoLinea) {
                linea = pedidoLinea;
            }

            if (linea != null && linea.precioUnitario() != null) {
                BigDecimal subtotal = linea.precioUnitario().multiply(BigDecimal.valueOf(linea.cantidad()));
                gananciaTotal = gananciaTotal.add(subtotal);
            }
        }

        System.out.println("Resultado (Ganancia Total): $" + gananciaTotal);
        return gananciaTotal;
    }
}