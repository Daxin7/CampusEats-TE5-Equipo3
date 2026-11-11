package campuseats.limpieza;

import campuseats.modelo.LineaAnalitica;
import campuseats.modelo.Pago;
import campuseats.modelo.Pedido;
import java.util.ArrayList;
import java.util.List;

/** Verifica el pago contra el total histórico, sin usar precios del menú actual. */
public class PagoVerificado implements Filtro {
    private final List<Pago> pagos;

    public PagoVerificado(List<Pago> pagos) {
        this.pagos = pagos;
    }

    @Override
    public List<LineaAnalitica> aplicar(List<LineaAnalitica> lineas) {
        List<LineaAnalitica> resultado = new ArrayList<>();

        for (LineaAnalitica registro : lineas) {
            Pedido pedido = registro.pedido();

            for (Pago pago : pagos) {
                if (pedido.numeroPedido().equals(pago.referencia())
                        && "APROBADA".equals(pago.estadoTx())
                        && pago.monto().compareTo(pedido.total()) == 0) {
                    resultado.add(registro);
                    break; // Un pago válido es suficiente para conservar la línea.
                }
            }
        }
        return resultado;
    }
}
