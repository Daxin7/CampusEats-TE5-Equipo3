
package campuseats.modelo;

import java.time.LocalDateTime;


public record HistorialEstado(
        String estado,
        LocalDateTime fechaHora,
        String usuario
) {
}
