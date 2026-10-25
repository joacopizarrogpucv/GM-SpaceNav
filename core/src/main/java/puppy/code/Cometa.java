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
	// colores posibles: celeste, rojo, verde y violeta (el que trae el x2 siempre es dorado)
	private static final float[][] PALETAS = {
			{ 0.5f, 0.85f, 1f }, { 1f, 0.25f, 0.2f }, { 0.3f, 1f, 0.4f }, { 0.75f, 0.4f, 1f } };
	private final float cr, cg, cb;

	/** Cometa normal: el multiplicador sale por azar. */
	public Cometa() {
		this(false);
	}

	/** forzarMultiplicador = true solo para pruebas (tecla V). */
	public Cometa(boolean forzarMultiplicador) {
		portaMultiplicador = forzarMultiplicador
				|| MathUtils.randomBoolean(MathUtils.random(PROB_MULT_MIN, PROB_MULT_MAX));
		float[] color = portaMultiplicador ? new float[] { 1f, 0.75f, 0.2f } : PALETAS[MathUtils.random(PALETAS.length - 1)];
		cr = color[0];
		cg = color[1];
		cb = color[2];
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

	public float getR() { return cr; }
	public float getG() { return cg; }
	public float getB() { return cb; }
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

	/** Franja de peligro con chevrones animados (ShapeRenderer en modo Filled). */
	public void dibujarAviso(ShapeRenderer shape) {
		float parpadeo = 0.25f + 0.2f * MathUtils.sin(reloj * 12f);
		float x1 = x0 + dirX * longitud;
		float y1 = y0 + dirY * longitud;
		shape.setColor(cr, cg, cb, 0.10f);
		shape.rectLine(x0, y0, x1, y1, RADIO * 2f);
		shape.setColor(cr, cg, cb, parpadeo);
		shape.rectLine(x0, y0, x1, y1, 4f);

		// chevrones que avanzan en la direccion del cometa
		float separacion = 150f;
		float desfase = (reloj * 320f) % separacion;
		float px = -dirY, py = dirX;
		for (float d = desfase; d < longitud; d += separacion) {
			float cx = x0 + dirX * d, cy = y0 + dirY * d;
			if (cx < -20 || cx > Constantes.ANCHO + 20 || cy < -20 || cy > Constantes.ALTO + 20) continue;
			shape.setColor(cr, cg, cb, 0.45f);
			shape.triangle(cx + dirX * 16f, cy + dirY * 16f,
					cx - dirX * 8f + px * 14f, cy - dirY * 8f + py * 14f,
					cx - dirX * 8f - px * 14f, cy - dirY * 8f - py * 14f);
		}
	}

	/** Cabeza, cola con degradado y chispas (ShapeRenderer en modo Filled). */
	public void dibujar(ShapeRenderer shape) {
		float x = getX();
		float y = getY();
		float px = -dirY, py = dirX;

		for (int k = 16; k >= 1; k--) {                       // cola
			float f = k / 16f;
			shape.setColor(cr, cg * (1f - 0.25f * f), cb * (1f - 0.1f * f), 0.42f * (1f - f) + 0.02f);
			shape.circle(x - dirX * k * 12f, y - dirY * k * 12f, RADIO * (1f - f * 0.75f), 22);
		}
		for (int k = 1; k <= 9; k++) {                        // chispas
			float f = k / 9f;
			float off = MathUtils.sin(reloj * 22f + k * 1.9f) * RADIO * 0.9f * f;
			shape.setColor(1f, 0.95f, 0.8f, 0.8f * (1f - f));
			shape.circle(x - dirX * k * 22f + px * off, y - dirY * k * 22f + py * off, 2.6f * (1f - f * 0.5f), 8);
		}
		shape.setColor(cr, cg, cb, 0.25f);                    // halo de la cabeza
		shape.circle(x, y, RADIO * 1.7f, 32);
		shape.setColor(cr, cg, cb, 1f);
		shape.circle(x, y, RADIO, 28);
		shape.setColor(1f, 1f, 1f, 1f);
		shape.circle(x, y, RADIO * 0.6f, 24);
	}
}
