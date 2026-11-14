package puppy.code;

/** Estrategia concreta: trayectoria en linea recta. */
public class MovimientoRecto implements EstrategiaMovimiento<Ball2> {
	@Override
	public void mover(Ball2 asteroide, float delta) {
		asteroide.avanzar(delta);
	}
}
