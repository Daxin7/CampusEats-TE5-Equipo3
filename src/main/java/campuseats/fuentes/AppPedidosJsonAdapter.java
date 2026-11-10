
package campuseats.fuentes;

import campuseats.modelo.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AppPedidosJsonAdapter
        implements FuenteDatos<Pedido> {

    private final Path ruta;
    private final ObjectMapper mapper;

    public AppPedidosJsonAdapter(Path ruta) {
        this.ruta = ruta;
        this.mapper = new ObjectMapper();
    }

    @Override
    public List<Pedido> leer() throws IOException {

        JsonNode raiz = mapper.readTree(ruta.toFile());

        if (!raiz.isArray()) {
            throw new IOException(
                    "El archivo de pedidos debe contener un arreglo JSON"
            );
        }

        List<Pedido> pedidos = new ArrayList<>();

        for (JsonNode nodo : raiz) {

            List<LineaPedido> lineas = new ArrayList<>();

            for (JsonNode linea : nodo.path("lineas")) {
                lineas.add(new LineaPedido(
                        linea.path("platoId").asInt(),
                        linea.path("cantidad").asInt(),
                        new BigDecimal(
                                linea.path("precioUnitario").asText()
                        )
                ));
            }

            List<HistorialEstado> historial =
                    new ArrayList<>();

            for (JsonNode evento :
                    nodo.path("historialEstados")) {

                historial.add(new HistorialEstado(
                        evento.path("estado").asText(),
                        LocalDateTime.parse(
                                evento.path("fechaHora").asText()
                        ),
                        evento.path("usuario").asText()
                ));
            }

            Pedido pedido = new Pedido(
                    nodo.path("numeroPedido").asText(),
                    LocalDateTime.parse(
                            nodo.path("registradoEn").asText()
                    ),
                    nodo.path("estado").asText(),
                    lineas,
                    new BigDecimal(
                            nodo.path("total").asText()
                    ),
                    historial
            );

            pedidos.add(pedido);
        }

        return pedidos;
    }
}
