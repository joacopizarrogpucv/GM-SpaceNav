# Space Snap

**Space Snap** es un videojuego 2D de supervivencia espacial desarrollado en Java con el motor **LibGDX** como parte del Proyecto GM de **Programación Avanzada**. Parte del juego base *Space Navigation* e incorpora nuevas mecánicas, pantallas y patrones de diseño.

**Equipo:** Matías Andrade, Joaquín Pizarro y Luciano Castañeda.

## Objetivo del juego

Controla una nave, esquiva o destruye asteroides y sobrevive el mayor tiempo posible para conseguir la puntuación más alta. La nave comienza con tres vidas. Durante la partida aparecen asteroides comunes y especiales, cometas, un jefe y planetas errantes con influencia gravitatoria.

El juego dispone de dificultades **Normal** y **Difícil**, munición limitada con recarga automática, bonificaciones y distintos modos de movimiento. Al perder todas las vidas, se muestra el puntaje final y el récord de la sesión.

### Eventos y elementos destacados

- **Asteroides comunes:** obstáculos que pueden destruirse mediante disparos.
- **Asteroides especiales:** algunos ofrecen recarga de munición o recuperación de vida.
- **Cometa:** atraviesa el escenario e interactúa con otros elementos; puede otorgar una bonificación.
- **Jefe espacial:** aparece durante la partida, tiene ataques propios y modifica el desafío.
- **Planeta errante:** cruza el escenario de forma periódica, altera la trayectoria de asteroides cercanos mediante su campo gravitatorio, desvía proyectiles próximos y puede dañar a la nave por contacto. **Aparece automáticamente**; el jugador no necesita activar ninguna tecla para encontrarlo.

## Controles normales

| Contexto | Teclas | Acción |
| --- | --- | --- |
| Menú principal | Flechas ↑ / ↓ | Cambiar opción |
| Menú principal | Flechas ← / → | Cambiar dificultad |
| Menú principal | Enter | Seleccionar |
| Partida | Flechas o WASD | Mover o controlar la nave según el modo seleccionado |
| Partida | Espacio | Disparar, cuando hay munición |
| Partida | Tab | Cambiar el modo de movimiento |
| Partida | P / Esc | Pausar o reanudar |
| Pausa | M | Volver al menú principal |
| Game Over | Enter | Reiniciar |
| Game Over | M / Esc | Volver al menú principal |

Los tres modos son **Inercia** (se modifica la velocidad con pulsaciones), **Directo** (movimiento mientras se mantiene pulsada la dirección) y **Rotacional** (giro, aceleración y frenado). Las flechas y WASD ofrecen controles equivalentes dentro de cada modo, pero las mecánicas de movimiento siguen siendo diferentes.

## Teclas de prueba (desarrolladores y evaluación)

**Estas teclas son herramientas de comprobación, no controles necesarios para jugar.** Permiten mostrar eventos sin esperar a su aparición normal, por ejemplo durante una demostración al ayudante.

| Tecla | Evento de prueba |
| --- | --- |
| **B** | Inicia el enfrentamiento con el **jefe** |
| **V** | Genera el **cometa** de prueba |
| **C** | Genera el **planeta errante** |

Se habilitan únicamente cuando `Constantes.TECLAS_DEBUG` está en `true`, dentro de `core/src/main/java/puppy/code/Constantes.java`. Algunos eventos no pueden iniciarse simultáneamente: si ya existe un jefe, cometa o planeta activo, una tecla puede no producir un efecto inmediato. La partida normal sigue incluyendo apariciones automáticas, independientemente de estas teclas.

Para una versión sin atajos de prueba, puede configurarse `TECLAS_DEBUG = false`. Si se necesitan en la evaluación, deben mantenerse activadas e identificarse claramente como funciones de demostración.

## Requisitos para ejecutar

- Windows (versión de escritorio utilizada durante el desarrollo).
- **JDK 21 para ejecutar Gradle** en la configuración actual. El plugin Gradle `Construo 1.4.1` requiere Java 17 o superior.
- Eclipse IDE con soporte para proyectos Gradle, o una terminal PowerShell.
- Acceso a internet en la primera ejecución, si Gradle debe descargar dependencias.

### Ejecutar con Eclipse

1. Abre Eclipse y selecciona `File → Import → Gradle → Existing Gradle Project`.
2. Selecciona la **carpeta raíz del repositorio**, la que contiene `settings.gradle`, `build.gradle` y `gradlew.bat`.
3. En las opciones, elige **Gradle wrapper** y **JDK 21** como `Java home` para Gradle.
4. Finaliza la importación. Si es necesario, utiliza `Gradle → Refresh Gradle Project`.
5. Abre `Gradle Tasks → SpaceNavigation-lwjgl3 → application → run` y ejecuta la tarea `run`.

El repositorio incluye el **Gradle Wrapper**: no es necesario instalar Gradle por separado.

## Organización del código

| Ruta | Contenido |
| --- | --- |
| `core/src/main/java/puppy/code/` | Pantallas, nave, asteroides, planeta errante, munición y lógica principal |
| `lwjgl3/src/main/java/puppy/Lwjgl3/` | Lanzador de escritorio `Lwjgl3Launcher` |
| `assets/` | Recursos gráficos y de audio |
| `build.gradle`, `settings.gradle` | Configuración general del proyecto |
| `core/build.gradle`, `lwjgl3/build.gradle` | Configuración de los módulos |

## Patrones de diseño

- **Singleton:** `ConfiguracionJuego` comparte la dificultad, el modo de movimiento y el récord durante la ejecución.
- **Strategy:** `EstrategiaMovimiento` y `EstrategiaMovimientoNave` permiten variar el desplazamiento de asteroides y nave. El planeta errante utiliza una estrategia gravitatoria para influir en asteroides cercanos.
- **Template Method:** `PantallaBase` establece el flujo común de las pantallas mediante `render(float)`.
- **Builder:** `AsteroideBuilder` permite configurar y validar asteroides antes de crearlos.

El análisis, los UML y la evidencia de los requisitos **GM1–GM10** corresponden al informe técnico del proyecto.

## Notas de solución de problemas

- Si Gradle falla usando Java 11 por el complemento Construo, configura **JDK 21 como JVM de Gradle**.
- Si ocurre `ClassNotFoundException` al iniciar, comprueba que el lanzador en `lwjgl3/build.gradle` sea `puppy.Lwjgl3.Lwjgl3Launcher`.
- Si falta la tarea `compileJava`, comprueba que exista el archivo **`build.gradle` principal** y vuelve a sincronizar Gradle.
- Si Eclipse no muestra las tareas, actualízalas desde la vista **Gradle Tasks** o sincroniza el proyecto.

## Créditos

Adaptación académica de *Space Navigation*, ampliada por el equipo para la asignatura Programación Avanzada.
