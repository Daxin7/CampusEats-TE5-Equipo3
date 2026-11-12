package campuseats.metricas;

import campuseats.modelo.LineaAnalitica;
import campuseats.modelo.LineaPedido;
import java.util.*;
import java.util.stream.Collectors;

public class PlatosTop implements Metrica {

    @Override
    public String nombre() {
        return "Platos más vendidos";
    }

    @Override
    public Object calcular(List<?> datos) {
        Map<String, Integer> conteoPlatos = new HashMap<>();

        for (Object obj : datos) {
            String nombrePlato = null;
            int cantidad = 0;

            if (obj instanceof LineaAnalitica analitica) {
                if (analitica.linea() != null) {
                    cantidad = analitica.linea().cantidad(); // Extraemos la cantidad siempre
                    
                    if (analitica.nombrePlato() != null && !analitica.nombrePlato().isBlank()) {
                        nombrePlato = analitica.nombrePlato(); // Usamos el nombre real del menú
                    } else {
                        nombrePlato = "Plato #" + analitica.linea().platoId(); // Respaldo por ID
                    }
                }
            } else if (obj instanceof LineaPedido linea) {
                nombrePlato = "Plato #" + linea.platoId();
                cantidad = linea.cantidad();
            }

            if (nombrePlato != null && cantidad > 0) {
                conteoPlatos.put(nombrePlato, conteoPlatos.getOrDefault(nombrePlato, 0) + cantidad);
            }
        }

        var topPlatos = conteoPlatos.entrySet().stream()
                .sorted((a, b) -> Integer.compare(b.getValue(), a.getValue()))
                .limit(5)
                .collect(Collectors.toList());

        List<String> resultadoFormateado = new ArrayList<>();
        int puesto = 1;
        for (var entry : topPlatos) {
            resultadoFormateado.add(puesto + ". " + entry.getKey() + ", " + entry.getValue() + " unidades");
            puesto++;
        }

        return resultadoFormateado;
    }
}