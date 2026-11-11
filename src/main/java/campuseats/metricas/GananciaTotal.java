package campuseats.metricas;

import campuseats.modelo.LineaPedido;
import java.math.BigDecimal;
import java.util.List;

public class GananciaTotal implements Metrica {

    @Override
    public String nombre() {
        return "Ganancia total acumulada";
    }

    @Override
    public Object calcular(List datos) {
        BigDecimal gananciaTotal = BigDecimal.ZERO;

        for (Object obj : datos) {
            LineaPedido linea = (LineaPedido) obj;
            BigDecimal subtotal = linea.precioUnitario().multiply(BigDecimal.valueOf(linea.cantidad()));
            gananciaTotal = gananciaTotal.add(subtotal);
        }

        return gananciaTotal;
    }
}