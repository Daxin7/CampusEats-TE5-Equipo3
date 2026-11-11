package campuseats.metricas;

import campuseats.modelo.LineaPedido;
import java.util.*;

public class PedidosPorHora implements Metrica {

    @Override
    public String nombre() {
        return "Pedidos por hora";
    }

    @Override
    public Object calcular(List datos) {
        Map<String, Set<Object>> pedidosPorHora = new HashMap<>();

        for (Object obj : datos) {
            LineaPedido linea = (LineaPedido) obj;
            String hora = linea.hora(); // Ej: "13:00"
            Object idPedido = linea.idPedido();

            if (hora != null && idPedido != null) {
                pedidosByHoraPut(pedidosPorHora, hora, idPedido);
            }
        }

        Map<String, Integer> resultado = new HashMap<>();
        for (Map.Entry<String, Set<Object>> entry : pedidosPorHora.entrySet()) {
            resultado.put(entry.getKey(), entry.getValue().size());
        }

        return resultado;
    }

    private void pedidosByHoraPut(Map<String, Set<Object>> mapa, String hora, Object idPedido) {
        mapa.putIfAbsent(hora, new HashSet<>());
        mapa.get(hora).add(idPedido);
    }
}