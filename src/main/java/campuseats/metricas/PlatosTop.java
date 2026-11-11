package campuseats.metricas;

import campuseats.modelo.LineaPedido;
import java.util.*;
import java.util.stream.Collectors;

public class PlatosTop implements Metrica {

    @Override
    public String nombre() {
        return "Platos mas vendidos";
    }

    @Override
    public Object calcular(List datos) {
        Map<Integer, Integer> conteoPlatos = new HashMap<>();

        for (Object obj : datos) {
            LineaPedido linea = (LineaPedido) obj;
            int platoId = linea.platoId();
            int cantidad = linea.cantidad();
            conteoPlatos.put(platoId, conteoPlatos.getOrDefault(platoId, 0) + cantidad);
        }

        return conteoPlatos.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(5)
                .collect(Collectors.toList());
    }
}