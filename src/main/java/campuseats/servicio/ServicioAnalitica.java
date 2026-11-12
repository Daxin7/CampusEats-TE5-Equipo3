package campuseats.servicio;

import campuseats.fabrica.FabricaFuentes;
import campuseats.fuentes.FuenteDatos;
import campuseats.limpieza.*;
import campuseats.metricas.Metrica;
import campuseats.modelo.LineaAnalitica;
import campuseats.modelo.Pago;
import campuseats.modelo.Pedido;
import campuseats.modelo.Plato;
import campuseats.repositorio.RepoAnalitica;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map;

public class ServicioAnalitica {
    private final RepoAnalitica repositorio;
    private final List<Metrica> metricas;
    
    public ServicioAnalitica(RepoAnalitica repositorio, List<Metrica> metricas, Pipeline pipeline){
        this.repositorio = repositorio;
        this.metricas = metricas;
    }
    
    public void cargar(){
        try{
            FabricaFuentes fabrica = new FabricaFuentes();
            ObjectMapper mapper = new ObjectMapper();
            Path rutaConfig = Path.of("config/fuentes.json");

            if (!Files.exists(rutaConfig)){
                throw new IllegalStateException("No existe config/fuentes.json");
            }

            JsonNode configuracion = mapper.readTree(rutaConfig.toFile());
            JsonNode fuentes = configuracion.path("fuentes");

            List<Pedido> pedidos = new ArrayList<>();
            List<Pago> pagos = new ArrayList<>();
            List<Plato> platos = new ArrayList<>();

            System.out.println("\nLeyendo fuentes configuradas...\n");
            
            for(JsonNode fuente : fuentes){
                String tipo = fuente.path("tipo").asText();
                Path ruta = Path.of(fuente.path("ruta").asText());

                if(!Files.exists(ruta)){
                    throw new IllegalStateException("Archivo no encontrado: " + ruta);
                }

                FuenteDatos<?> adaptador = fabrica.crear(tipo, ruta);
                List<?> registros = adaptador.leer();

                for (Object registro : registros){
                    if (registro instanceof Pedido pedido){
                        pedidos.add(pedido);
                    } else if (registro instanceof Pago pago){
                        pagos.add(pago);
                    } else if (registro instanceof Plato plato){
                        platos.add(plato); // <-- ¡AQUÍ ESTABA EL DETALLE! Capturamos los platos del menú
                    }
                }
                System.out.println("------------------------------------");
                System.out.println("Tipo: " + tipo);
                System.out.println("Archivo: " + ruta);
                System.out.println("Adaptador: " + adaptador.getClass().getSimpleName());
                System.out.println("Registros leídos: " + registros.size());
            }
            List<LineaAnalitica> lineas = LineaAnalitica.desdePedidos(pedidos);

            System.out.println("\nLIMPIEZA DE DATOS");
            System.out.println("Lineas iniciales: " + lineas.size());

            Pipeline pipelineCompleto = new Pipeline(List.of(
                    new SinDuplicados(),
                    new CantidadesValidas(),
                    new SoloPagados(),
                    new PagoVerificado(pagos),
                    new CompletarNombrePlato(platos) // Ahora sí recibe la lista con los 12 platos del menú
            ));

            List<LineaAnalitica> limpias = pipelineCompleto.ejecutar(lineas);

            Set<String> numerosPedido = new HashSet<>();
            for(LineaAnalitica registro : limpias){
                numerosPedido.add(registro.pedido().numeroPedido());
            }
            System.out.printf("Quedan al final: %d lineas de %d pedidos%n", 
                limpias.size(), numerosPedido.size());
            
            repositorio.guardarLineas(limpias);
            repositorio.guardarPagos(pagos);

            System.out.println("\nLECTURA Y LIMPIEZA FINALIZADAS");
        
        } catch (Exception e){
            System.err.println("\nERROR AL PROCESAR LOS DATOS: ");
            System.err.println(e.getMessage());
            e.printStackTrace();
            System.exit(1);
        }
    }

    public void resumen(LocalDateTime desde, LocalDateTime hasta){
        System.out.println("\n==================================");
        System.out.println("        RESUMEN DE ESTADISTICAS     ");
        System.out.println("==================================");

        List<LineaAnalitica> datosLimpios = repositorio.lineasEntre(desde, hasta);

        for (Metrica metrica : metricas){
            System.out.println("\nMétrica: " + metrica.nombre());
            Object resultado = metrica.calcular(datosLimpios);
            
            if (resultado instanceof List<?> lista) {
                for (Object item : lista) {
                    System.out.println("  " + item);
                }
            } else if (resultado instanceof Map<?, ?> mapa) {
                for (var entry : mapa.entrySet()) {
                    System.out.println("  - Hora " + entry.getKey() + ": " + entry.getValue() + " pedidos");
                }
            } else {
                System.out.println("  Resultado: " + resultado);
            }
        }
        System.out.println("\n===================================");
    }
}