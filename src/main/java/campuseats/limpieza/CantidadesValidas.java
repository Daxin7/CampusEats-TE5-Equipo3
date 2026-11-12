package campuseats.limpieza;

import campuseats.modelo.LineaAnalitica;
import java.util.ArrayList;
import java.util.List;

public class CantidadesValidas implements Filtro {
    @Override
    public List<LineaAnalitica> aplicar(List<LineaAnalitica> lineas) {
        List<LineaAnalitica> resultado = new ArrayList<>();

        for (LineaAnalitica registro : lineas) {
            // cantidad ya es int en el modelo; aquí exigimos que sea positiva.
            if (registro.linea().cantidad() > 0) {
                resultado.add(registro);
            }
        }
        return resultado;
    }
}
