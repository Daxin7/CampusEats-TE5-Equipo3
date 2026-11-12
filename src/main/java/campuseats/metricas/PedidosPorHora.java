package campuseats.metricas;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import campuseats.modelo.LineaAnalitica;
import campuseats.modelo.LineaPedido;
import campuseats.modelo.Pedido;

public class PedidosPorHora implements Metrica {

    @Override
    public String nombre() {
        return "Pedidos por hora";
    }

    @Override
    public Object calcular(List<?> datos) {
        Map<String, Set<Object>> pedidosPorHora = new HashMap<>();

        for (Object obj : datos) {
            String hora = null;
            Object idPedido = null;

            if (obj instanceof LineaAnalitica analitica) {
                Pedido pedido = analitica.pedido();
                if (pedido != null && pedido.registradoEn() != null) {
                    // Extraemos la hora en formato "HH:00" o la hora exacta de registradoEn
                    int horaInt = pedido.registradoEn().getHour();
                    hora = String.format("%02d:00", horaInt);
                    idPedido = pedido.numeroPedido();
                }
            } else if (obj instanceof LineaPedido linea) {
                hora = linea.hora();
                idPedido = linea.idPedido();
            }

            if (hora != null && idPedido != null) {
                pedidosByHoraPut(pedidosPorHora, hora, idPedido);
            }
        }

        Map<String, Integer> resultado = new HashMap<>();
        for (Map.Entry<String, Set<Object>> entry : pedidosPorHora.entrySet()) {
            resultado.put(entry.getKey(), entry.getValue().size());
        }

        // Imprimir resultados 
        System.out.println("Resultados (Pedidos por hora): " + resultado);

        return resultado;
    }

    private void pedidosByHoraPut(Map<String, Set<Object>> mapa, String hora, Object idPedido) {
        mapa.putIfAbsent(hora, new HashSet<>());
        mapa.get(hora).add(idPedido);
    }
}