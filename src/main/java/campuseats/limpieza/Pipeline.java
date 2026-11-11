package campuseats.limpieza;

import campuseats.modelo.LineaAnalitica;
import java.util.ArrayList;
import java.util.List;

/** Ejecuta filtros en el orden recibido y muestra sus descartes en consola. */
public class Pipeline {
    private final List<Filtro> filtros;

    public Pipeline(List<Filtro> filtros) {
        this.filtros = filtros;
    }

    public List<LineaAnalitica> ejecutar(List<LineaAnalitica> entrada) {
        List<LineaAnalitica> actuales = new ArrayList<>(entrada);

        for (Filtro filtro : filtros) {
            int antes = actuales.size();
            actuales = filtro.aplicar(actuales);
            int descartadas = antes - actuales.size();

            System.out.println(filtro.getClass().getSimpleName()
                    + ": " + descartadas + " líneas descartadas");
        }
        return actuales;
    }
}
