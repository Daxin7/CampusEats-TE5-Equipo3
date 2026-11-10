
package campuseats.modelo;

import java.math.BigDecimal;

/**
 * Representa un plato del menú de la cafetería.
 */
public record Plato(
        int idPlato,
        String nombre,
        String categoria,
        BigDecimal precioActual,
        String estado
) {
}
