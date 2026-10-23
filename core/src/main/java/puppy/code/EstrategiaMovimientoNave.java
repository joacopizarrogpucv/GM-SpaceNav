package puppy.code;

/**
 * PATRON STRATEGY (nave) - interfaz.
 * Permite alternar en caliente entre distintas formas de controlar la nave.
 */
public interface EstrategiaMovimientoNave {
	/** Lee el teclado y actualiza velocidad/angulo de la nave. */
	void mover(Nave4 nave, float delta);

	String getNombre();
}
