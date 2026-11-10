package campuseats.modelo;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Representa un pedido registrado en CampusEats.
 */
public record Pedido(
        String numeroPedido,
        LocalDateTime registradoEn,
        String estado,
        List<LineaPedido> lineas,
        BigDecimal total,
        List<HistorialEstado> historialEstados
) {
}
