package campuseats.limpieza;

import campuseats.modelo.LineaAnalitica;
import campuseats.modelo.Plato;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Completa la información de cada línea con el nombre del plato desde el menú. */
public class CompletarNombrePlato implements Filtro {
    private final Map<Integer, String> nombresPorId;

    public CompletarNombrePlato(List<Plato> platos) {
        this.nombresPorId = new HashMap<>();
        for (Plato plato : platos) {
            this.nombresPorId.put(plato.idPlato(), plato.nombre());
        }
    }

    @Override
    public List<LineaAnalitica> aplicar(List<LineaAnalitica> lineas) {
        List<LineaAnalitica> resultado = new ArrayList<>();
        for (LineaAnalitica registro : lineas) {
            String nombre = nombresPorId.get(registro.linea().platoId());
            resultado.add(registro.conNombrePlato(nombre != null ? nombre : "Plato " + registro.linea().platoId()));
        }
        return resultado;
    }
}
