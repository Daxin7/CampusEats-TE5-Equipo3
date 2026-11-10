package campuseats;

import campuseats.fabrica.FabricaFuentes;
import campuseats.fuentes.FuenteDatos;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

/**
 * Prueba de integración de los patrones
 * Adapter y Factory.
 */
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

            System.out.println("\nLeyendo fuentes configuradas...\n");

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

            System.out.println("\n====================================");
            System.out.println("LECTURA FINALIZADA CORRECTAMENTE");
            System.out.println("====================================");

        } catch (Exception e) {

            System.err.println("\nERROR AL LEER LOS DATOS:");
            System.err.println(e.getMessage());

            e.printStackTrace();

            System.exit(1);
        }
    }
}
