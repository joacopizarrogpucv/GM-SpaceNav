package puppy.code;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/**
 * Cometa que cruza la pantalla de una esquina a la opuesta. Destruye los asteroides que toca
 * y mata a la nave al contacto. Antes de aparecer muestra un aviso con su trayectoria.
 */
public class Cometa {

	private static final float RADIO = 24f;
	private static final float VELOCIDAD = 520f;
	private static final float TIEMPO_AVISO = 2f;
	private static final float MARGEN = 70f;
	private static final float PROB_MULT_MIN = 0.05f;   // cada cometa tiene entre 5 % y 15 %
	private static final float PROB_MULT_MAX = 0.15f;   // de probabilidad de soltar el multiplicador x2

	private final float x0, y0, dirX, dirY, longitud;
	private float aviso = TIEMPO_AVISO;
	private float recorrido = 0f;
	private float reloj = 0f;
	private final boolean portaMultiplicador;

	/** Cometa normal: el multiplicador sale por azar. */
	public Cometa() {
		this(false);
	}

	/** forzarMultiplicador = true solo para pruebas (tecla V). */
	public Cometa(boolean forzarMultiplicador) {
		portaMultiplicador = forzarMultiplicador
				|| MathUtils.randomBoolean(MathUtils.random(PROB_MULT_MIN, PROB_MULT_MAX));
		boolean izquierda = MathUtils.randomBoolean();
		boolean arriba = MathUtils.randomBoolean();
		x0 = izquierda ? -MARGEN : Constantes.ANCHO + MARGEN;
		y0 = arriba ? Constantes.ALTO + MARGEN : -MARGEN;
		float x1 = izquierda ? Constantes.ANCHO + MARGEN : -MARGEN;
		float y1 = arriba ? -MARGEN : Constantes.ALTO + MARGEN;
		float dx = x1 - x0;
		float dy = y1 - y0;
		longitud = (float) Math.sqrt(dx * dx + dy * dy);
		dirX = dx / longitud;
		dirY = dy / longitud;
	}

	public void update(float delta) {
		reloj += delta;
		if (aviso > 0) {
			aviso -= delta;
			return;
		}
		recorrido += VELOCIDAD * delta;
	}

	public boolean enAviso() { return aviso > 0; }
	public boolean portaMultiplicador() { return portaMultiplicador; }
	public boolean estaActivo() { return aviso <= 0 && recorrido < longitud; }
	public boolean haTerminado() { return aviso <= 0 && recorrido >= longitud; }

	public float getX() { return x0 + dirX * recorrido; }
	public float getY() { return y0 + dirY * recorrido; }

	public boolean choca(float cx, float cy, float r) {
		if (!estaActivo()) return false;
		float dx = getX() - cx;
		float dy = getY() - cy;
		float rr = RADIO + r;
		return dx * dx + dy * dy < rr * rr;
	}

	/** Franja de peligro mostrada antes de que llegue el cometa (ShapeRenderer en modo Filled). */
	public void dibujarAviso(ShapeRenderer shape) {
		float parpadeo = 0.25f + 0.2f * MathUtils.sin(reloj * 12f);
		shape.setColor(1f, 0.3f, 0.1f, 0.10f);
		shape.rectLine(x0, y0, x0 + dirX * longitud, y0 + dirY * longitud, RADIO * 2f);
		shape.setColor(1f, 0.3f, 0.1f, parpadeo);
		shape.rectLine(x0, y0, x0 + dirX * longitud, y0 + dirY * longitud, 4f);
	}

	/** Cabeza y cola del cometa (ShapeRenderer en modo Filled). */
	public void dibujar(ShapeRenderer shape) {
		float x = getX();
		float y = getY();
		for (int k = 10; k >= 1; k--) {
			shape.setColor(0.5f, 0.85f, 1f, 0.45f - k * 0.04f);
			shape.circle(x - dirX * k * 16f, y - dirY * k * 16f, RADIO * (1f - k * 0.08f), 24);
		}
		shape.setColor(0.6f, 0.9f, 1f, 1f);
		shape.circle(x, y, RADIO, 28);
		shape.setColor(1f, 1f, 1f, 1f);
		shape.circle(x, y, RADIO * 0.6f, 24);
	}
}
