
package campuseats.modelo;

import java.math.BigDecimal;


public record LineaPedido(
        int platoId,
        int cantidad,
        BigDecimal precioUnitario
) {
}
