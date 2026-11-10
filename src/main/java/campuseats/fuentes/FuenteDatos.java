package campuseats.fuentes;

import java.io.IOException;
import java.util.List;

public interface FuenteDatos<T> {
    List<T> leer() throws IOException;
}
