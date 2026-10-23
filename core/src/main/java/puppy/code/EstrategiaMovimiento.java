package puppy.code;

/**
 * PATRON STRATEGY (asteroides) - interfaz.
 * Problema: el asteroide debe poder cambiar su forma de moverse (recto / curvado por gravedad)
 * sin reescribir la clase Ball2 ni llenarla de if/else.
 */
public interface EstrategiaMovimiento {
	void mover(Ball2 asteroide, float delta);
}
