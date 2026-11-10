
package campuseats.fuentes;

import campuseats.modelo.Plato;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class MenuCsvAdapter
        implements FuenteDatos<Plato> {

    private final Path ruta;

    public MenuCsvAdapter(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    public List<Plato> leer() throws IOException {

        List<Plato> platos = new ArrayList<>();

        try (var filas = Files.lines(ruta)) {

            filas.skip(1)
                    .filter(fila -> !fila.isBlank())
                    .forEach(fila -> {

                        String[] columnas = fila.split(",", -1);

                        if (columnas.length != 5) {
                            throw new IllegalArgumentException(
                                    "Fila CSV inválida: " + fila
                            );
                        }

                        Plato plato = new Plato(
                                Integer.parseInt(
                                        columnas[0].trim()
                                ),
                                columnas[1].trim(),
                                columnas[2].trim(),
                                new BigDecimal(
                                        columnas[3].trim()
                                ),
                                columnas[4].trim()
                        );

                        platos.add(plato);
                    });
        }

        return platos;
    }
}
