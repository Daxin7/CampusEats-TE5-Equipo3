package campuseats.limpieza;

import campuseats.modelo.LineaAnalitica;
import java.util.List;

/** Cada filtro aplica una única regla y devuelve las líneas que la cumplen. */
public interface Filtro {
    List<LineaAnalitica> aplicar(List<LineaAnalitica> lineas);
}
