package campuseats.metricas;

import java.util.List;

public interface Metrica {
    String nombre();
    Object calcular(List<?> datos);
}