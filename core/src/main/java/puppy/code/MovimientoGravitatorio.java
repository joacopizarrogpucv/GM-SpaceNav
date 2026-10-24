package puppy.code;

/** Estrategia concreta: la trayectoria se curva hacia un planeta (mas fuerte mientras mas cerca). */
public class MovimientoGravitatorio implements EstrategiaMovimiento {

	private static final float VELOCIDAD_MAXIMA = 320f;
	private final Planeta planeta;

	public MovimientoGravitatorio(Planeta planeta) {
		this.planeta = planeta;
	}

	@Override
	public void mover(Ball2 a, float delta) {
		float dx = planeta.getX() - a.getCentroX();
		float dy = planeta.getY() - a.getCentroY();
		float dist = (float) Math.sqrt(dx * dx + dy * dy);

		if (dist > 1f) {
			float intensidad = planeta.getGravedad() * Math.max(0f, 1f - dist / planeta.getRadioZona());
			a.sumarVelocidad(dx / dist * intensidad * delta, dy / dist * intensidad * delta);
		}
		a.limitarVelocidad(VELOCIDAD_MAXIMA);
		a.avanzar(delta);

		// no puede atravesar el nucleo del planeta: sale rebotado
		if (dist < planeta.getRadio() + a.getRadio()) {
			a.alejarDe(planeta.getX(), planeta.getY());
		}
	}
}
