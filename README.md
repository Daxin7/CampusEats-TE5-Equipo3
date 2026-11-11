# Tarea 3 simplificada: limpieza de CampusEats

Esta versión conserva las reglas del taller y usa ciclos `for`, condiciones
`if` y listas. Hay siete archivos nuevos de la tarea 3 y una integración en
`Main.java`. Las tareas 1 y 2 del repositorio se conservan.

## Abrir y ejecutar

1. Descomprime el ZIP y abre en IntelliJ la carpeta que contiene `pom.xml`.
2. Selecciona **JDK 21** como SDK del proyecto y carga las dependencias de Maven.
3. Ejecuta `campuseats.Main`. El directorio de trabajo debe ser la raíz del
   proyecto, donde están `config/` y `datos/`.

Con Maven instalado, también puedes ejecutar desde esa carpeta:

```bash
mvn clean test exec:java
```

La carpeta `src/test` contiene pruebas de apoyo; no necesitas modificarla para
implementar o explicar la tarea 3.

## Qué datos recibes

La tarea 1 ya define `Pedido`, `LineaPedido`, `HistorialEstado` y `Pago`:

| Objeto | Información relevante |
| --- | --- |
| `Pedido` | Número, fecha, total, historial y lista de platos pedidos. |
| `LineaPedido` | Identificador del plato, cantidad y precio unitario original. |
| `HistorialEstado` | Un estado por el que pasó el pedido y su fecha. |
| `Pago` | Referencia al pedido, monto y estado de una transacción de la pasarela. |

Una **línea** es un tipo de plato dentro de un pedido. Un pedido con un
almuerzo y un jugo tiene dos líneas. La cantidad indica cuántas unidades de
ese plato hay en su línea.

Para limpiar hay que conocer tanto la línea como su pedido. Por eso agregamos
`LineaAnalitica`, que los mantiene juntos sin modificar los modelos del equipo.

En las clases de esta tarea verás estas instrucciones repetidas:

- `List<LineaAnalitica>`: una lista cuyos elementos son líneas con su contexto.
- `new ArrayList<>()`: crea una lista inicialmente vacía.
- `for (... : lineas)`: recorre las líneas una por una.
- `resultado.add(registro)`: conserva una línea válida en la nueva lista.
- `return resultado`: devuelve la lista que contiene las líneas válidas.
- `registro.pedido()` y `registro.linea()`: métodos generados por el `record`
  para acceder al pedido completo y a la línea individual.

Los filtros construyen una lista de salida en lugar de borrar elementos de la
lista mientras la recorren. Así la lista de entrada conserva sus registros.

## Los ocho archivos, con su código completo

## 1. LineaAnalitica.java

Ubicación: `src/main/java/campuseats/modelo/LineaAnalitica.java`.

**Función:** guardar juntos el pedido, una de sus líneas, la posición de origen y el nombre del plato completado desde el menú.

Un `record` declara un tipo de datos. Java genera el constructor y los métodos
`origenPedido()`, `pedido()`, `linea()` y `nombrePlato()`.

El método `desdePedidos` transforma una lista de pedidos en una lista de líneas:

1. El primer `for` recorre los pedidos. `i` es su posición, empezando en cero.
2. El segundo `for` recorre los platos dentro de ese pedido.
3. `new LineaAnalitica(i, pedido, linea)` vincula cada línea con su pedido.
4. `resultado.add(...)` agrega ese vínculo a la lista de salida.

`conNombrePlato(nombre)` permite devolver una nueva instancia completando el nombre del plato obtenido del menú sin mutar el objeto.

```java
package campuseats.modelo;

import java.util.ArrayList;
import java.util.List;

/**
 * Une cada línea con su pedido sin modificar los modelos de los adaptadores.
 * origenPedido identifica la aparición del pedido en la lista leída: dos
 * apariciones con el mismo número deben distinguirse para eliminar duplicados.
 * nombrePlato almacena el nombre del plato completado desde el menú.
 */
public record LineaAnalitica(
        int origenPedido,
        Pedido pedido,
        LineaPedido linea,
        String nombrePlato
) {
    public LineaAnalitica(int origenPedido, Pedido pedido, LineaPedido linea) {
        this(origenPedido, pedido, linea, null);
    }

    public LineaAnalitica conNombrePlato(String nuevoNombre) {
        return new LineaAnalitica(origenPedido, pedido, linea, nuevoNombre);
    }

    public static List<LineaAnalitica> desdePedidos(List<Pedido> pedidos) {
        List<LineaAnalitica> resultado = new ArrayList<>();
        for (int i = 0; i < pedidos.size(); i++) {
            Pedido pedido = pedidos.get(i);
            for (LineaPedido linea : pedido.lineas()) {
                resultado.add(new LineaAnalitica(i, pedido, linea));
            }
        }
        return resultado;
    }
}
```

## 2. Filtro.java

Ubicación: `src/main/java/campuseats/limpieza/Filtro.java`.

**Función:** definir el contrato común de todos los filtros.

La interfaz dice: «un filtro debe tener un método `aplicar` que reciba una lista
de líneas y devuelva una lista de líneas». No decide cuál regla se aplica.

`implements Filtro` en las siguientes clases significa que cumplen ese contrato.
`@Override` indica que el método implementa el que exige la interfaz.

```java
package campuseats.limpieza;

import campuseats.modelo.LineaAnalitica;
import java.util.List;

/** Cada filtro aplica una única regla y devuelve las líneas que la cumplen. */
public interface Filtro {
    List<LineaAnalitica> aplicar(List<LineaAnalitica> lineas);
}
```

## 3. SinDuplicados.java

Ubicación: `src/main/java/campuseats/limpieza/SinDuplicados.java`.

**Función:** conservar todas las líneas de la primera aparición de un pedido
y descartar las de apariciones posteriores con el mismo número.

`Map<String, Integer>` asocia un número de pedido con su primer origen. Por
ejemplo: `P-001` se asocia con `0`. `HashMap` es la implementación de ese mapa.

- `containsKey(numero)` pregunta si ya conocemos el número de pedido.
- `!` significa «no»: si todavía no lo conocemos, guardamos su origen.
- `put(numero, origen)` guarda esa asociación.
- `get(numero)` recupera el primer origen que guardamos.
- Si el origen de la línea coincide con ese primer origen, la conservamos.

Ejemplo:

| Origen | Número | Plato | Decisión |
| --- | --- | --- | --- |
| 0 | P-001 | Almuerzo | Conservar: es la primera aparición. |
| 0 | P-001 | Jugo | Conservar: pertenece a esa misma aparición. |
| 2 | P-001 | Almuerzo | Descartar: el pedido aparece otra vez. |
| 2 | P-001 | Jugo | Descartar: pertenece a la aparición repetida. |

Usar únicamente un conjunto de números vistos para eliminar líneas borraría
el jugo de la primera aparición. Por eso también guardamos el origen.

La conversión `desdePedidos` debe hacerse **una sola vez sobre la lista completa**
que entrará al pipeline, para que cada aparición tenga su posición de origen.

```java
package campuseats.limpieza;

import campuseats.modelo.LineaAnalitica;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Conserva todas las líneas de la primera aparición de cada pedido. */
public class SinDuplicados implements Filtro {
    @Override
    public List<LineaAnalitica> aplicar(List<LineaAnalitica> lineas) {
        Map<String, Integer> primerOrigen = new HashMap<>();
        List<LineaAnalitica> resultado = new ArrayList<>();
        for (LineaAnalitica registro : lineas) {
            String numero = registro.pedido().numeroPedido();

            // La primera vez guardamos de qué aparición proviene el pedido.
            if (!primerOrigen.containsKey(numero)) {
                primerOrigen.put(numero, registro.origenPedido());
            }

            // Conservamos todos los platos de esa primera aparición.
            if (primerOrigen.get(numero) == registro.origenPedido()) {
                resultado.add(registro);
            }
        }
        return resultado;
    }
}
```

## 4. CantidadesValidas.java

Ubicación: `src/main/java/campuseats/limpieza/CantidadesValidas.java`.

**Función:** conservar las líneas cuya cantidad sea mayor que cero.

En `registro.linea().cantidad()`, primero accedemos a la línea individual y
luego consultamos su cantidad. Si la cantidad es positiva, se agrega a la
salida. Si es cero o negativa, no se agrega.

Se descarta la línea inválida; los otros platos del pedido pueden conservarse.

**Límite del modelo existente:** `cantidad` ya es de tipo `int`. El adaptador
original usa `asInt()`, por lo que una cantidad fraccionaria podría convertirse
antes de llegar al filtro. Para aceptar futuras fuentes con decimales se debe
validar la cantidad en el adaptador antes de convertirla. Los datos del taller
usan cantidades enteras y el caso inválido es cero.

```java
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
```

## 5. SoloPagados.java

Ubicación: `src/main/java/campuseats/limpieza/SoloPagados.java`.

**Función:** conservar las líneas de los pedidos que pasaron por `PAGADO`.

El primer `for` recorre las líneas. El segundo recorre el historial del pedido
de cada línea. `equals` compara el texto del estado con `PAGADO`.

Cuando encuentra ese estado, agrega la línea y ejecuta `break`. `break` sale
del ciclo del historial, no del ciclo que recorre todas las líneas. Así evita
agregar la misma línea varias veces si se repite el evento en el historial.

Se consulta el historial porque el estado actual puede ser `ENTREGADO`, aunque
el pedido sí pasó por `PAGADO`. Mirar solo el estado actual excluiría ventas válidas.

```java
package campuseats.limpieza;

import campuseats.modelo.LineaAnalitica;
import campuseats.modelo.HistorialEstado;
import java.util.ArrayList;
import java.util.List;

/** Un pedido entregado también cuenta si antes pasó por PAGADO. */
public class SoloPagados implements Filtro {
    @Override
    public List<LineaAnalitica> aplicar(List<LineaAnalitica> lineas) {
        List<LineaAnalitica> resultado = new ArrayList<>();

        for (LineaAnalitica registro : lineas) {
            for (HistorialEstado evento : registro.pedido().historialEstados()) {
                if ("PAGADO".equals(evento.estado())) {
                    resultado.add(registro);
                    break; // Esta línea se agrega una sola vez.
                }
            }
        }
        return resultado;
    }
}
```

## 6. PagoVerificado.java

Ubicación: `src/main/java/campuseats/limpieza/PagoVerificado.java`.

**Función:** conservar una línea si su pedido tiene al menos una transacción
aprobada por el monto correcto.

`private final List<Pago> pagos` guarda la lista de transacciones recibida en
el constructor. `this.pagos = pagos` asigna el parámetro al atributo del objeto.
No lee el CSV: esa lectura ya la hizo el adaptador de la tarea 1.

Para cada línea, el ciclo interior busca un pago que cumpla tres condiciones:

1. Su referencia coincide con el número del pedido.
2. Su estado es `APROBADA`.
3. Su monto coincide con `pedido.total()`.

`&&` exige que las tres condiciones sean verdaderas al mismo tiempo.

Los montos son `BigDecimal`. `compareTo(...) == 0` significa que tienen el
mismo valor numérico, por lo que `3.5` y `3.50` se consideran iguales.

Al encontrar un pago válido, agrega la línea y sale del ciclo de pagos con
`break`. Si hubo un intento rechazado y después uno aprobado por el monto
correcto, el pedido se conserva. No se suman intentos de pago.

El total es el original del pedido; no se recalcula usando el precio del menú
actual. Las líneas de un mismo pedido se verifican contra el mismo total, por
lo que, si ningún pago sirve, ninguna de sus líneas se conserva.

```java
package campuseats.limpieza;

import campuseats.modelo.LineaAnalitica;
import campuseats.modelo.Pago;
import campuseats.modelo.Pedido;
import java.util.ArrayList;
import java.util.List;

/** Verifica el pago contra el total histórico, sin usar precios del menú actual. */
public class PagoVerificado implements Filtro {
    private final List<Pago> pagos;

    public PagoVerificado(List<Pago> pagos) {
        this.pagos = pagos;
    }

    @Override
    public List<LineaAnalitica> aplicar(List<LineaAnalitica> lineas) {
        List<LineaAnalitica> resultado = new ArrayList<>();

        for (LineaAnalitica registro : lineas) {
            Pedido pedido = registro.pedido();

            for (Pago pago : pagos) {
                if (pedido.numeroPedido().equals(pago.referencia())
                        && "APROBADA".equals(pago.estadoTx())
                        && pago.monto().compareTo(pedido.total()) == 0) {
                    resultado.add(registro);
                    break; // Un pago válido es suficiente para conservar la línea.
                }
            }
        }
        return resultado;
    }
}
```

## 7. CompletarNombrePlato.java

Ubicación: `src/main/java/campuseats/limpieza/CompletarNombrePlato.java`.

**Función:** completar la información de cada línea vinculando el nombre del plato desde el menú.

En el patrón Pipes & Filters, los filtros no solo descartan registros inválidos, también pueden transformar o enriquecer la información. Siguiendo la pista del taller (*«primero se limpia y después se completa la información (nombre del plato desde el menú)»*), este filtro se ubica al final del pipeline:

1. El constructor recibe `List<Plato>` (leída previamente con `MenuCsvAdapter`) y construye un mapa rápido de `idPlato` $\rightarrow$ `nombre`.
2. Para cada línea limpia, busca el nombre de su plato (`linea.platoId()`).
3. Retorna la línea analítica con `nombrePlato` debidamente completado, dejándola lista para calcular las métricas de la Tarea 4 (como `PlatosTop`).
4. Al no descartar registros, reporta 0 líneas descartadas.

```java
package campuseats.limpieza;

import campuseats.modelo.LineaAnalitica;
import campuseats.modelo.Plato;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Completa la información de cada línea con el nombre del plato desde el menú. */
public class CompletarNombrePlato implements Filtro {
    private final Map<Integer, String> nombresPorId;

    public CompletarNombrePlato(List<Plato> platos) {
        this.nombresPorId = new HashMap<>();
        for (Plato plato : platos) {
            this.nombresPorId.put(plato.idPlato(), plato.nombre());
        }
    }

    @Override
    public List<LineaAnalitica> aplicar(List<LineaAnalitica> lineas) {
        List<LineaAnalitica> resultado = new ArrayList<>();
        for (LineaAnalitica registro : lineas) {
            String nombre = nombresPorId.get(registro.linea().platoId());
            resultado.add(registro.conNombrePlato(nombre != null ? nombre : "Plato " + registro.linea().platoId()));
        }
        return resultado;
    }
}
```

## 8. Pipeline.java

Ubicación: `src/main/java/campuseats/limpieza/Pipeline.java`.

**Función:** pasar las líneas por todos los filtros y mostrar cuántas descarta
cada uno.

El constructor recibe una `List<Filtro>`. El orden de esa lista es el orden de
ejecución. `new ArrayList<>(entrada)` crea una copia de la lista de entrada.

En cada vuelta del `for`:

1. `antes = actuales.size()` guarda cuántas líneas hay antes del filtro.
2. `actuales = filtro.aplicar(actuales)` reemplaza la lista con la salida del filtro.
3. `antes - actuales.size()` calcula cuántas líneas se descartaron.
4. `System.out.println(...)` muestra el resultado de ese paso.

`getClass().getSimpleName()` obtiene el nombre de la clase del filtro para
imprimirlo, por ejemplo `SoloPagados`.

Al terminar, `return actuales` entrega las líneas limpias. No hay una clase
adicional para el reporte. El pipeline no necesita conocer las reglas internas:
solo llama al método `aplicar` que exige la interfaz `Filtro`.

```java
package campuseats.limpieza;

import campuseats.modelo.LineaAnalitica;
import java.util.ArrayList;
import java.util.List;

/** Ejecuta filtros en el orden recibido y muestra sus descartes en consola. */
public class Pipeline {
    private final List<Filtro> filtros;

    public Pipeline(List<Filtro> filtros) {
        this.filtros = filtros;
    }

    public List<LineaAnalitica> ejecutar(List<LineaAnalitica> entrada) {
        List<LineaAnalitica> actuales = new ArrayList<>(entrada);

        for (Filtro filtro : filtros) {
            int antes = actuales.size();
            actuales = filtro.aplicar(actuales);
            int descartadas = antes - actuales.size();

            System.out.println(filtro.getClass().getSimpleName()
                    + ": " + descartadas + " líneas descartadas");
        }
        return actuales;
    }
}
```

## Cómo se integra en Main.java

El ciclo de lectura original se conserva. Antes de recorrer las fuentes se
crean tres listas para reunir los pedidos, las transacciones y los platos del menú:

```java
List<Pedido> pedidos = new ArrayList<>();
List<Pago> pagos = new ArrayList<>();
List<Plato> platos = new ArrayList<>();
```

Después de `List<?> registros = adaptador.leer();`, se guarda cada objeto según
su tipo. La misma fábrica sigue eligiendo el adaptador de cada fuente:

```java
for (Object registro : registros) {
    if (registro instanceof Pedido pedido) {
        pedidos.add(pedido);
    } else if (registro instanceof Pago pago) {
        pagos.add(pago);
    } else if (registro instanceof Plato plato) {
        platos.add(plato);
    }
}
```

`instanceof` comprueba el tipo del objeto. Los objetos del menú leídos por `MenuCsvAdapter`
se recopilan para alimentar el filtro final de enriquecimiento.

Después de leer todas las fuentes, se ejecuta este bloque:

```java
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
```

`new SinDuplicados()` crea el primer filtro. Los filtros de validación descartan registros erróneos
y finalmente `new CompletarNombrePlato(platos)` asocia el nombre de cada plato según el menú.

`List.of(...)` reúne las instancias de los filtros en el orden elegido.
`pipeline.ejecutar(lineas)` ejecuta la limpieza y devuelve su resultado.

Un `Set` conserva elementos distintos. Al agregar los números de los pedidos
limpios a un `HashSet`, cada número queda una sola vez. Por eso su tamaño es
el número de pedidos y no el número de líneas.

Las tareas 4 y 5 pueden consumir `limpias`. Deben calcular sus métricas sobre
esa lista: cada elemento contiene tanto la información depurada como `registro.nombrePlato()`.

## Orden de ejecución y resultado

1. Las 633 líneas entran a `SinDuplicados`: descarta 2 y quedan 631.
2. Esas 631 entran a `CantidadesValidas`: descarta 1 y quedan 630.
3. Esas 630 entran a `SoloPagados`: descarta 35 y quedan 595.
4. Esas 595 entran a `PagoVerificado`: descarta 2 y quedan 593.
5. Esas 593 entran a `CompletarNombrePlato`: completa los nombres desde el menú (0 descartadas).

Salida de la tarea 3:

```text
LIMPIEZA DE DATOS
Líneas iniciales: 633
SinDuplicados: 2 líneas descartadas
CantidadesValidas: 1 líneas descartadas
SoloPagados: 35 líneas descartadas
PagoVerificado: 2 líneas descartadas
CompletarNombrePlato: 0 líneas descartadas
Quedan al final: 593 líneas de 388 pedidos
```

Los precios históricos no se reemplazan y no se descarta un plato por estar
retirado del menú actual. El pipeline entrega cada línea con su nombre de plato completado
para que la Tarea 4 pueda calcular directamente los platos más vendidos.

## Qué cambió frente a la primera versión

- Se eliminó `ResultadoLimpieza` y su registro interno de descartes.
- `Pipeline.ejecutar` ahora devuelve directamente `List<LineaAnalitica>`.
- El pipeline imprime el reporte, sin construir objetos adicionales para él.
- Los filtros usan `for`, `if` y `break`, en lugar de streams y lambdas.
- `PagoVerificado` recorre la lista de pagos, sin índices ni cachés adicionales.

La búsqueda de pagos repite el recorrido para cada línea. Para los archivos
pequeños del taller permite entender la regla fácilmente. Con muchos más
registros se podría volver a usar un índice por número de pedido para ahorrar
recorridos, conservando la misma regla de validación.

## Explicación breve para presentar

> Implementé Pipeline porque cada regla de limpieza puede cambiar por separado.
> Todos los filtros cumplen la interfaz Filtro: reciben líneas y devuelven las
> que cumplen su regla. El pipeline ejecuta esos filtros en orden y muestra la
> diferencia de tamaño antes y después de cada paso. La salida de un filtro
> es la entrada del siguiente. Al final entrega la lista limpia para las métricas.
