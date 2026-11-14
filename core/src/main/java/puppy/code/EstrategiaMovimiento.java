package puppy.code;

/**
 * STRATEGY: contrato unico de movimiento para cualquier entidad T.
 * El parametro generico evita casts y conserva el tipo real: Nave4 o Ball2.
 * La implementacion concreta decide el algoritmo; la entidad solo delega mover().
 *
 * @param <T> tipo de entidad que puede mover la estrategia
 */
public interface EstrategiaMovimiento<T> {
    void mover(T entidad, float delta);

    /** Nombre legible opcional para el HUD de estrategias seleccionables. */
    default String getNombre() { return getClass().getSimpleName(); }
}
