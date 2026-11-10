
package campuseats.fuentes;

import campuseats.modelo.Pago;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


public class PasarelaCsvAdapter
        implements FuenteDatos<Pago> {

    private final Path ruta;

    private static final DateTimeFormatter FORMATO =
            DateTimeFormatter.ofPattern(
                    "dd/MM/yyyy HH:mm:ss"
            );

    public PasarelaCsvAdapter(Path ruta) {
        this.ruta = ruta;
    }

    @Override
    public List<Pago> leer() throws IOException {

        List<Pago> pagos = new ArrayList<>();

        try (var filas = Files.lines(ruta)) {

            filas.skip(1)
                    .filter(fila -> !fila.isBlank())
                    .forEach(fila -> {

                        String[] columnas = fila.split(";", -1);

                        if (columnas.length != 6) {
                            throw new IllegalArgumentException(
                                    "Fila CSV inválida: " + fila
                            );
                        }

                        BigDecimal monto = new BigDecimal(
                                columnas[1].trim()
                                        .replace(',', '.')
                        );

                        Pago pago = new Pago(
                                columnas[0].trim(),
                                monto,
                                columnas[2].trim(),
                                columnas[3].trim(),
                                columnas[4].trim(),
                                LocalDateTime.parse(
                                        columnas[5].trim(),
                                        FORMATO
                                )
                        );

                        pagos.add(pago);
                    });
        }

        return pagos;
    }
}
