# Space Snap

**Space Snap** es un videojuego 2D de supervivencia espacial desarrollado en Java con el motor **LibGDX** como parte del Proyecto GM de **Programación Avanzada**. Parte del juego base *Space Navigation* e incorpora nuevas mecánicas, pantallas y patrones de diseño.

**Equipo:** Matías Andrade, Joaquín Pizarro y Luciano Castañeda.

## Objetivo del juego

Controla una nave, esquiva y destruye asteroides y consigue el mayor puntaje posible. La nave comienza con 3 vidas (máximo 5). Durante la partida aparecen asteroides comunes y especiales, cometas, planetas errantes con lava y, cada cierto tiempo, un jefe: la Estrella de la Muerte.

**El puntaje solo aumenta al destruir asteroides (+10 cada uno) o al derrotar al jefe (400 × nivel).** No suma por el paso del tiempo. El multiplicador x2 duplica ambos mientras está activo. Al perder todas las vidas se muestra una pantalla con el puntaje, el récord, el tiempo sobrevivido, los jefes derrotados y los asteroides destruidos.

## Dificultades

| | Normal | Difícil |
| --- | --- | --- |
| Tamaño de asteroides | uniforme | entre 1× y 1,5× |
| Velocidad | constante | aumenta con el tiempo (tope ×2,2) |
| Asteroides máximos en pantalla | 17 (sube hasta 21 con el tiempo) | 21 (sube hasta 25 con el tiempo) |
| Aparición de un asteroide | cada 1,0 s | cada 0,65 s |
| Balas máximas (recarga de 5 s por bala) | 5 | 10 |
| Probabilidad de asteroide dorado | 5 % | 27 % (solo en los grandes) |
| Asteroides máximos durante el jefe | 7 a 9 según su nivel | 8 a 12 según su nivel |
| Vida del jefe (nivel 1, 2, 3…) | 1000, 2000, 3000… | 2000, 4000, 6000… |
| Láseres del jefe | cada 1,8 s, 300 px/s | cada 1,1 s, 380 px/s |

## Elementos del juego

- **Asteroides comunes:** rebotan en los bordes y entre ellos (se desvían entre 35° y 45°). Se destruyen con disparos; al chocar con la nave restan una vida.
- **Asteroide dorado:** al destruirlo suelta un ítem que recarga toda la munición. Desaparece a los 14 s (parpadea al final).
- **Asteroide verde:** muy raro. Al destruirlo con una bala cura 1 vida. Desaparece a los 10 s.
- **Cometa:** cruza de una esquina a la opuesta, avisa 2 s antes con una franja y el texto "AHI VIENE EL COMETA!". Es de color aleatorio (celeste, rojo, verde o violeta). No se puede destruir, destruye los asteroides que toca y **mata a la nave al contacto**. Aparece por primera vez entre los 15 y 30 s y luego cada 45 a 75 s. Con una probabilidad de 5 % a 15 % es **dorado** y al terminar de cruzar suelta un ítem **x2** que duplica el puntaje durante 30 s.
- **Planeta errante:** planeta destruido con lava que cruza la pantalla (primera vez entre los 35 y 55 s, luego cada 40 a 65 s). Su gravedad curva los asteroides cercanos, desvía las balas y daña a la nave si la toca. Lanza bolas de lava, principalmente hacia abajo y algunas apuntadas a la nave. La lava quita 1 vida a la nave y destruye los asteroides que toca.
- **Jefe (Estrella de la Muerte):** aparece cada 2 a 3 minutos. Al aparecer desaparecen los asteroides y suena su música. Dispara láseres rojos y se vuelve más agresivo con el nivel: nivel 1 quieto; desde el nivel 2 se mueve de lado a lado; desde el 3 también sube y baja y sus ráfagas y abanicos tienen más rayos; desde el 4 lanza anillos de rayos. Suelta un ítem de recarga cada 15 s.
- **Exclusión de eventos:** el jefe, el cometa y el planeta errante nunca coinciden en pantalla.

## Controles normales

| Contexto | Teclas | Acción |
| --- | --- | --- |
| Menú principal | Flechas ↑ / ↓ | Cambiar opción |
| Menú principal | Flechas ← / → | Cambiar dificultad |
| Menú principal | Enter | Seleccionar |
| Partida | Flechas o WASD en los tres modos | Mover o controlar la nave según el modo seleccionado |
| Partida | Espacio | Disparar, cuando hay munición |
| Partida | Tab | Cambiar el modo de movimiento |
| Partida | P / Esc | Pausar o reanudar |
| Pausa | M | Volver al menú principal |
| Game Over | Enter | Reiniciar |
| Game Over | M / Esc | Volver al menú principal |

Los tres modos son **Inercia** (cada pulsación suma velocidad y la nave sigue deslizando), **Directo** (movimiento en 8 direcciones mientras se mantiene pulsada la tecla) y **Rotacional** (giro, aceleración y frenado; los disparos salen hacia donde apunta la nave).

## Teclas de prueba (desarrollo y evaluación)

**Estas teclas son herramientas de comprobación, no controles necesarios para jugar.** Permiten mostrar eventos sin esperar su aparición normal, por ejemplo durante una demostración.

| Tecla | Evento de prueba |
| --- | --- |
| **B** | Inicia el enfrentamiento con el **jefe** |
| **V** | Genera un **cometa dorado** (con multiplicador x2) |
| **C** | Genera el **planeta errante** |

Se habilitan solo cuando `Constantes.TECLAS_DEBUG` es `true`, en `core/src/main/java/puppy/code/Constantes.java`. Si ya hay un jefe, un cometa o un planeta activo, la tecla puede no tener efecto inmediato. La partida normal incluye apariciones automáticas independientemente de estas teclas. Para una versión sin atajos, configura `TECLAS_DEBUG = false`.

## Requisitos para ejecutar

- Windows (versión de escritorio usada durante el desarrollo).
- **JDK 11 o superior.** El proyecto compila con `release 11`, y está configurado para ser compatible con JDK 11.
- Eclipse IDE (versión *for Java Developers*, que incluye Buildship para Gradle) o una terminal.
- Acceso a internet en la primera ejecución, para que Gradle descargue sus dependencias.

### Ejecutar con Eclipse

1. En **Window → Preferences → Java → Installed JREs**, agrega tu JDK y márcalo como predeterminado. En **Window → Preferences → Gradle**, elige el mismo JDK en *Java home*.
2. Menú **File → Import → Gradle → Existing Gradle Project**.
3. Selecciona la **carpeta raíz del repositorio**, la que contiene `settings.gradle`, `build.gradle` y `gradlew.bat`.
4. En *Import Options*, deja **Gradle wrapper** y elige tu JDK en *Java home*. Pulsa *Finish* y espera a que termine la sincronización (varios minutos la primera vez).
5. Abre la vista **Window → Show View → Other → Gradle → Gradle Tasks**.
6. Ejecuta **SpaceNavigation-parent → lwjgl3 → application → run**. La tarea usa la carpeta `assets` como directorio de trabajo, por lo que encuentra imágenes y sonidos.

El repositorio incluye el **Gradle Wrapper**, no hace falta instalar Gradle por separado.

## Música del jefe

El juego reproduce `assets/jefe-musica.ogg` en bucle mientras hay un jefe. Para usar otra pista, copia tu archivo (ogg, mp3 o wav) a `assets/` y cambia la constante `ARCHIVO_MUSICA_JEFE` de `PantallaJuego.java`. Si el archivo no existe, el juego funciona igual, pero el jefe no tiene música propia.

## Organización del código

| Ruta | Contenido |
| --- | --- |
| `core/src/main/java/puppy/code/` | Todo el juego (31 clases, ver tabla siguiente) |
| `lwjgl3/src/main/java/puppy/Lwjgl3/` | Lanzador de escritorio `Lwjgl3Launcher` |
| `assets/` | Imágenes y audio |
| `build.gradle`, `settings.gradle` | Configuración general |
| `core/build.gradle`, `lwjgl3/build.gradle` | Configuración de los módulos |

| Grupo | Clases |
| --- | --- |
| Pantallas | `SpaceNavigation`, `PantallaBase` (abstracta), `PantallaMenu`, `PantallaJuego`, `PantallaGameOver` |
| Jugador | `Nave4`, `Bullet`, `Municion` |
| Enemigos y eventos | `Ball2` (asteroide), `TipoAsteroide`, `Cometa`, `Planeta`, `PlanetaErrante`, `Lava`, `JefeEstrella`, `LaserJefe` |
| Ítems | `ItemRecarga`, `ItemMultiplicador` |
| Movimiento (Strategy) | `EstrategiaMovimiento`, `MovimientoRecto`, `MovimientoGravitatorio`, `EstrategiaMovimientoNave`, `MovimientoInercia`, `MovimientoDirecto`, `MovimientoRotacional` |
| Configuración y creación | `ConfiguracionJuego` (Singleton), `Dificultad`, `AsteroideBuilder` (Builder), `Constantes` |
| Visual | `Fondo` (estrellas con parallax), `Texto` (texto con sombra) |

## Solución de problemas

- **"A project with the name SpaceNavigation-parent already exists" / "Duplicate root element":** quedaron proyectos viejos en Eclipse. Bórralos del workspace (sin marcar *Delete contents on disk*), borra `.project` y `.settings` de la carpeta del proyecto e impórtalo una sola vez.
- **"Dependency requires at least JVM runtime version 17":** tu `lwjgl3/build.gradle` aún tiene el plugin Construo. Esta versión ya no lo incluye. Si aparece, borra la línea del plugin `io.github.fourlastor.construo`, la importación de `Target` y el bloque `construo { ... }`.
- **`Java home ... does not exist`:** elige tu JDK en *Java home* durante la importación, o borra `.settings`.
- **`ClassNotFoundException` al iniciar:** comprueba que el lanzador de `lwjgl3/build.gradle` sea `puppy.Lwjgl3.Lwjgl3Launcher`.
- **No aparecen las tareas de Gradle:** pulsa el botón de refrescar en la vista *Gradle Tasks* o usa *Gradle → Refresh Gradle Project*.
- **Cruces rojas tras copiar archivos:** refresca (F5) y luego *Gradle → Refresh Gradle Project*.

## Créditos

Adaptación académica de *Space Navigation*, ampliada por el equipo para la asignatura Programación Avanzada.
