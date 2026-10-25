package puppy.code;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;

/**
 * Fondo espacial: tres capas de estrellas con parallax (las cercanas se mueven mas rapido)
 * y unas nebulosas tenues. Se comparte entre todas las pantallas para que no "salte" al cambiar.
 */
public class Fondo {

	private static final int CAPAS = 3;
	private static final int[] CANTIDAD = { 70, 40, 18 };
	private static final float[] VELOCIDAD = { 10f, 26f, 55f };
	private static final float[] TAMANO = { 1.4f, 2.1f, 3.2f };
	private static final float[] BRILLO = { 0.40f, 0.65f, 0.95f };
	private static final int NEBULOSAS = 4;
	private static final float[][] COLORES = {
			{ 0.45f, 0.15f, 0.60f }, { 0.10f, 0.30f, 0.55f },
			{ 0.60f, 0.15f, 0.30f }, { 0.10f, 0.45f, 0.45f } };

	private final float[][] sx = new float[CAPAS][];
	private final float[][] sy = new float[CAPAS][];
	private final float[][] fase = new float[CAPAS][];
	private final float[] nx = new float[NEBULOSAS];
	private final float[] ny = new float[NEBULOSAS];
	private final float[] nr = new float[NEBULOSAS];
	private float reloj = 0f;

	public Fondo() {
		for (int c = 0; c < CAPAS; c++) {
			sx[c] = new float[CANTIDAD[c]];
			sy[c] = new float[CANTIDAD[c]];
			fase[c] = new float[CANTIDAD[c]];
			for (int i = 0; i < CANTIDAD[c]; i++) {
				sx[c][i] = MathUtils.random(0f, Constantes.ANCHO);
				sy[c][i] = MathUtils.random(0f, Constantes.ALTO);
				fase[c][i] = MathUtils.random(0f, 6.28f);
			}
		}
		for (int i = 0; i < NEBULOSAS; i++) {
			nx[i] = MathUtils.random(100f, Constantes.ANCHO - 100f);
			ny[i] = MathUtils.random(0f, Constantes.ALTO);
			nr[i] = MathUtils.random(170f, 280f);
		}
	}

	public void update(float delta) {
		reloj += delta;
		for (int c = 0; c < CAPAS; c++) {
			for (int i = 0; i < CANTIDAD[c]; i++) {
				sy[c][i] -= VELOCIDAD[c] * delta;
				if (sy[c][i] < -4f) {
					sy[c][i] += Constantes.ALTO + 8f;
					sx[c][i] = MathUtils.random(0f, Constantes.ANCHO);
				}
			}
		}
		for (int i = 0; i < NEBULOSAS; i++) {
			ny[i] -= 4f * delta;
			if (ny[i] < -nr[i]) ny[i] += Constantes.ALTO + 2f * nr[i];
		}
	}

	/** Dibuja el fondo. Requiere blending activado y la proyeccion del ShapeRenderer ya asignada. */
	public void dibujar(ShapeRenderer shape) {
		shape.begin(ShapeType.Filled);
		for (int i = 0; i < NEBULOSAS; i++) {
			float[] col = COLORES[i];
			shape.setColor(col[0], col[1], col[2], 0.035f);
			shape.circle(nx[i], ny[i], nr[i], 40);
			shape.circle(nx[i], ny[i], nr[i] * 0.7f, 36);
			shape.circle(nx[i], ny[i], nr[i] * 0.4f, 32);
		}
		for (int c = 0; c < CAPAS; c++) {
			float t = TAMANO[c];
			for (int i = 0; i < CANTIDAD[c]; i++) {
				float brillo = BRILLO[c] * (0.7f + 0.3f * MathUtils.sin(reloj * 2f + fase[c][i]));
				shape.setColor(0.80f, 0.90f, 1f, brillo);
				if (c == CAPAS - 1) shape.circle(sx[c][i], sy[c][i], t / 2f, 6);
				else shape.rect(sx[c][i] - t / 2f, sy[c][i] - t / 2f, t, t);
			}
		}
		shape.end();
	}
}
