package campuseats.limpieza;

import campuseats.modelo.LineaAnalitica;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Conserva todas las líneas de la primera aparición de cada pedido. */
public class SinDuplicados implements Filtro {
    @Override
    public List<LineaAnalitica> aplicar(List<LineaAnalitica> lineas) {
        Map<String, Integer> primerOrigen = new HashMap<>();
        List<LineaAnalitica> resultado = new ArrayList<>();
        for (LineaAnalitica registro : lineas) {
            String numero = registro.pedido().numeroPedido();

            // La primera vez guardamos de qué aparición proviene el pedido.
            if (!primerOrigen.containsKey(numero)) {
                primerOrigen.put(numero, registro.origenPedido());
            }

            // Conservamos todos los platos de esa primera aparición.
            if (primerOrigen.get(numero) == registro.origenPedido()) {
                resultado.add(registro);
            }
        }
        return resultado;
    }
}
