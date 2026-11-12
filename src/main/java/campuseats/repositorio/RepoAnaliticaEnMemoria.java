package campuseats.repositorio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import campuseats.modelo.LineaAnalitica;
import campuseats.modelo.Pago;

public class RepoAnaliticaEnMemoria implements RepoAnalitica{
    private List<LineaAnalitica> lineas = new ArrayList<>();
    private List<Pago> pagos = new ArrayList<>();

    @Override 
    public void guardarLineas(List<LineaAnalitica> lineas){
        if (lineas != null){
            this.lineas = lineas;
        }
    }

    @Override 
    public void guardarPagos(List<Pago> pagos){
        if (pagos != null){
            this.pagos = pagos;
        }
    }

    @Override
    public List<LineaAnalitica> lineasEntre(LocalDateTime desde, LocalDateTime hasta) {
        List<LineaAnalitica> filtradas = new ArrayList<>();
        
        for (LineaAnalitica linea : lineas) {
            if (linea.pedido() != null && linea.pedido().registradoEn() != null) {
                LocalDateTime fechaPedido = linea.pedido().registradoEn();
                
                // Valida que la fecha del pedido se encuentre dentro del rango inclusivo
                boolean despuertoDeOIgual = !fechaPedido.isBefore(desde);
                boolean antesDeOIgual = !fechaPedido.isAfter(hasta);
                
                if (despuertoDeOIgual && antesDeOIgual) {
                    filtradas.add(linea);
                }
            } else {
                filtradas.add(linea);
            }
        }
        
        return filtradas;
    }

    @Override
    public List<Pago> pagosEntre(LocalDateTime desde, LocalDateTime hasta) {
        return pagos;
    }

    @Override
    public List<LineaAnalitica> obtenerTodasLasLineas() {
        return lineas;
    }

    @Override
    public List<Pago> obtenerTodosLosPagos() {
        return pagos;
    }
}
