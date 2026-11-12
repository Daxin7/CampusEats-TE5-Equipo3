package campuseats.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Une cada línea con su pedido sin modificar los modelos de los adaptadores.
 * origenPedido identifica la aparición del pedido en la lista leída: dos
 * apariciones con el mismo número deben distinguirse para eliminar duplicados.
 * nombrePlato almacena el nombre del plato completado desde el menú.
 */
public record LineaAnalitica(
        int origenPedido,
        Pedido pedido,
        LineaPedido linea,
        String nombrePlato
) {
    public LineaAnalitica(int origenPedido, Pedido pedido, LineaPedido linea) {
        this(origenPedido, pedido, linea, null);
    }

    public LineaAnalitica conNombrePlato(String nuevoNombre) {
        return new LineaAnalitica(origenPedido, pedido, linea, nuevoNombre);
    }

    public static List<LineaAnalitica> desdePedidos(List<Pedido> pedidos) {
        List<LineaAnalitica> resultado = new ArrayList<>();
        for (int i = 0; i < pedidos.size(); i++) {
            Pedido pedido = pedidos.get(i);
            for (LineaPedido linea : pedido.lineas()) {
                resultado.add(new LineaAnalitica(i, pedido, linea));
            }
        }
        return resultado;
    }
}
