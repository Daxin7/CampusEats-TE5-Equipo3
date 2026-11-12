package campuseats;

import campuseats.metricas.*;
import campuseats.repositorio.RepoAnalitica;
import campuseats.repositorio.RepoAnaliticaEnMemoria;
import campuseats.servicio.ServicioAnalitica;

import java.time.LocalDateTime;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        System.out.println("====================================");
        System.out.println("       CAMPUS EATS - ANALISIS       ");
        System.out.println("====================================");

        RepoAnalitica repositorio = new RepoAnaliticaEnMemoria();

        // Creamos las métricas incluyendo las propias de ticket, tiempos y pagos rechazados
        List<Metrica> metricas = List.of(
                new PlatosTop(),
                new PedidosPorHora(),
                new GananciaTotal(),
                new TicketPromedio(),
                new EstadisticasOperativas(repositorio.obtenerTodosLosPagos(), "tiempo"),
                new EstadisticasOperativas(repositorio.obtenerTodosLosPagos(), "rechazados")
        );

        ServicioAnalitica servicio = new ServicioAnalitica(repositorio, metricas, null);

        // Ejecuta la carga de archivos
        servicio.cargar();

        // Muestra el resumen completo dentro del rango de fechas
        servicio.resumen(
                LocalDateTime.of(2026, 10, 19, 0, 0),
                LocalDateTime.of(2026, 10, 30, 23, 59)
        );
    }
}