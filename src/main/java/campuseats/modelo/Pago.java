
package campuseats.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Representa una transacción de la pasarela de pagos.
 */
public record Pago(
        String referencia,
        BigDecimal monto,
        String estadoTx,
        String codigoAutorizacion,
        String motivoRechazo,
        LocalDateTime fechaHora
) {
}
