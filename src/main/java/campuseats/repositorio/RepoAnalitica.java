package campuseats.repositorio;

import campuseats.modelo.LineaAnalitica;
import campuseats.modelo.Pago;
import java.time.LocalDateTime;
import java.util.List;

public interface RepoAnalitica {
    void guardarLineas(List<LineaAnalitica> lineas);
    void guardarPagos(List<Pago> pagos);
    List<LineaAnalitica> lineasEntre(LocalDateTime desde, LocalDateTime hasta);
    List<Pago> pagosEntre(LocalDateTime desde, LocalDateTime hasta);
    List<LineaAnalitica> obtenerTodasLasLineas();
    List<Pago> obtenerTodosLosPagos();
}