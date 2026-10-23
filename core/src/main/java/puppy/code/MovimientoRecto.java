package puppy.code;

/** Estrategia concreta: trayectoria en linea recta. */
public class MovimientoRecto implements EstrategiaMovimiento {
	@Override
	public void mover(Ball2 asteroide, float delta) {
		asteroide.avanzar(delta);
	}
}
