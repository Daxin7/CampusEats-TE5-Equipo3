package campuseats.limpieza;

import campuseats.modelo.LineaAnalitica;
import campuseats.modelo.HistorialEstado;
import java.util.ArrayList;
import java.util.List;

/** Un pedido entregado también cuenta si antes pasó por PAGADO. */
public class SoloPagados implements Filtro {
    @Override
    public List<LineaAnalitica> aplicar(List<LineaAnalitica> lineas) {
        List<LineaAnalitica> resultado = new ArrayList<>();

        for (LineaAnalitica registro : lineas) {
            for (HistorialEstado evento : registro.pedido().historialEstados()) {
                if ("PAGADO".equals(evento.estado())) {
                    resultado.add(registro);
                    break; // Esta línea se agrega una sola vez.
                }
            }
        }
        return resultado;
    }
}
