package campuseats;

import campuseats.fabrica.FabricaFuentes;
import campuseats.fuentes.FuenteDatos;
import campuseats.limpieza.*;
import campuseats.modelo.LineaAnalitica;
import campuseats.modelo.Pago;
import campuseats.modelo.Pedido;
import campuseats.modelo.Plato;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

        System.out.println("====================================");
        System.out.println("       CAMPUS EATS - ANALISIS       ");
        System.out.println("====================================");

        try {

            FabricaFuentes fabrica = new FabricaFuentes();

            ObjectMapper mapper = new ObjectMapper();

            Path rutaConfig = Path.of("config/fuentes.json");

            if (!Files.exists(rutaConfig)) {
                throw new IllegalStateException(
                        "No existe config/fuentes.json"
                );
            }

            JsonNode configuracion =
                    mapper.readTree(rutaConfig.toFile());

            JsonNode fuentes = configuracion.path("fuentes");

            if (!fuentes.isArray()) {
                throw new IllegalStateException(
                        "La configuración debe contener un arreglo 'fuentes'"
                );
            }

            List<Pedido> pedidos = new ArrayList<>();
            List<Pago> pagos = new ArrayList<>();
            List<Plato> platos = new ArrayList<>();

            for (JsonNode fuente : fuentes) {

                String tipo = fuente.path("tipo").asText();

                Path ruta = Path.of(
                        fuente.path("ruta").asText()
                );

                if (!Files.exists(ruta)) {
                    throw new IllegalStateException(
                            "Archivo no encontrado: " + ruta
                    );
                }

                // Factory selecciona el adaptador
                FuenteDatos<?> adaptador =
                        fabrica.crear(tipo, ruta);

                // Adapter convierte los datos
                List<?> registros = adaptador.leer();

                for (Object registro : registros) {
                    if (registro instanceof Pedido pedido) {
                        pedidos.add(pedido);
                    } else if (registro instanceof Pago pago) {
                        pagos.add(pago);
                    } else if (registro instanceof Plato plato) {
                        platos.add(plato);
                    }
                }

                System.out.println("------------------------------------");
                System.out.println("Tipo: " + tipo);
                System.out.println("Archivo: " + ruta);
                System.out.println(
                        "Adaptador: " +
                                adaptador.getClass().getSimpleName()
                );
                System.out.println(
                        "Registros leídos: " + registros.size()
                );
            }

            List<LineaAnalitica> lineas = LineaAnalitica.desdePedidos(pedidos);
            Pipeline pipeline = new Pipeline(List.of(
                    new SinDuplicados(),
                    new CantidadesValidas(),
                    new SoloPagados(),
                    new PagoVerificado(pagos),
                    new CompletarNombrePlato(platos)
            ));
            System.out.println("\nLIMPIEZA DE DATOS");
            System.out.println("Líneas iniciales: " + lineas.size());

            List<LineaAnalitica> limpias = pipeline.ejecutar(lineas);

            // Un pedido puede tener varias líneas: contamos números distintos.
            Set<String> numerosPedido = new HashSet<>();
            for (LineaAnalitica registro : limpias) {
                numerosPedido.add(registro.pedido().numeroPedido());
            }
            System.out.printf("Quedan al final: %d líneas de %d pedidos%n",
                    limpias.size(), numerosPedido.size());

            System.out.println("\nLECTURA Y LIMPIEZA FINALIZADAS");


        } catch (Exception e) {

            System.err.println("\nERROR AL PROCESAR LOS DATOS:");
            System.err.println(e.getMessage());

            e.printStackTrace();

            System.exit(1);
        }
    }
}
