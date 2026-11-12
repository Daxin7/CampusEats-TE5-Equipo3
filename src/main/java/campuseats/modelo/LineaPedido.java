
package campuseats.modelo;

import java.math.BigDecimal;


public record LineaPedido(
        Object idPedido,
        String hora,
        int platoId,
        int cantidad,
        BigDecimal precioUnitario
) {
}
