package campuseats.fabrica;

import campuseats.fuentes.*;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class FabricaFuentes {

    // Registro de constructores de adaptadores
    private final Map<String, Function<Path, FuenteDatos<?>>>
            constructores = new HashMap<>();

    public FabricaFuentes() {

        registrar("app_json", AppPedidosJsonAdapter::new);

        registrar("pasarela_csv", PasarelaCsvAdapter::new);

        registrar("menu_csv", MenuCsvAdapter::new);
    }

    /**
     * Registra un nuevo tipo de fuente.
     */
    public void registrar(
            String tipo,
            Function<Path, FuenteDatos<?>> constructor
    ) {
        if (tipo == null || tipo.isBlank() ||
                constructor == null) {
            throw new IllegalArgumentException(
                    "Tipo y constructor son obligatorios"
            );
        }

        constructores.put(tipo, constructor);
    }

    /**
     * Crear una fuente de datos según su tipo.
     */
    public FuenteDatos<?> crear(String tipo, Path ruta) {

        Function<Path, FuenteDatos<?>> constructor =
                constructores.get(tipo);

        if (constructor == null) {
            throw new IllegalArgumentException(
                    "Tipo de fuente no soportado: " + tipo
            );
        }

        return constructor.apply(ruta);
    }
}
