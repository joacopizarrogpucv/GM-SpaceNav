package puppy.code;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;

/**
 * Minijefe: planeta destruido con lava que cruza la pantalla. Modifica la trayectoria de los
 * asteroides cercanos (gravedad) y lanza bolas de lava desde su herida.
 */
public final class PlanetaErrante extends Planeta {

	private static final float RADIO = 56f;
	private static final float RADIO_ZONA = 210f;
	private static final float FUERZA_GRAVEDAD = 190f;
	private static final float VELOCIDAD = 155f;

	private static final int SEGMENTOS = 48;
	private static final float ANGULO_HERIDA = 270f;    // la herida apunta hacia abajo (grados, local)
	private static final float ANCHO_HERIDA = 38f;      // semiancho angular de la herida
	private static final float INTERVALO_LAVA = 0.8f;
	private static final int GRIETAS = 5;
	private static final int ESCOMBROS = 6;

	private final float velocidadHorizontal;
	private final float[] perfil = new float[SEGMENTOS];          // radio relativo por segmento
	private final boolean[] enHerida = new boolean[SEGMENTOS];
	private final float[][] grietas = new float[GRIETAS][10];     // pares (angulo, fraccion de radio)
	private final float[] escombroAng = new float[ESCOMBROS];
	private final float[] escombroRad = new float[ESCOMBROS];
	private final float[] escombroVel = new float[ESCOMBROS];
	private final float[] px = new float[SEGMENTOS];
	private final float[] py = new float[SEGMENTOS];

	private float rotacion = 0f;
	private float reloj = 0f;
	private float temporizadorLava = 0f;

	public PlanetaErrante(boolean vieneDeIzquierda, float altura) {
		super(vieneDeIzquierda ? -RADIO : Constantes.ANCHO + RADIO,
				altura, RADIO, RADIO_ZONA, FUERZA_GRAVEDAD);
		velocidadHorizontal = vieneDeIzquierda ? VELOCIDAD : -VELOCIDAD;

		for (int i = 0; i < SEGMENTOS; i++) {
			float ang = i * 360f / SEGMENTOS;
			float d = Math.abs(((ang - ANGULO_HERIDA + 540f) % 360f) - 180f);   // distancia angular a la herida
			float base = 1f + 0.07f * MathUtils.random(-1f, 1f);                   // borde irregular
			float factor = 1f;
			if (d < ANCHO_HERIDA) {
				float t = d / ANCHO_HERIDA;
				factor = 0.50f + 0.50f * (t * t * (3f - 2f * t));                  // mordida suavizada
			}
			perfil[i] = base * factor;
			enHerida[i] = d < ANCHO_HERIDA;
		}
		for (int g = 0; g < GRIETAS; g++) {
			float ang = MathUtils.random(0f, 360f);
			for (int p = 0; p < 5; p++) {
				grietas[g][p * 2] = ang + MathUtils.random(-14f, 14f);
				grietas[g][p * 2 + 1] = 0.12f + p * 0.2f;
			}
		}
		for (int i = 0; i < ESCOMBROS; i++) {
			escombroAng[i] = MathUtils.random(0f, 360f);
			escombroRad[i] = MathUtils.random(1.45f, 1.9f) * RADIO;
			escombroVel[i] = MathUtils.random(10f, 28f) * (MathUtils.randomBoolean() ? 1 : -1);
		}
	}

	/** Mueve el planeta y devuelve la lava lanzada en este cuadro (casi siempre vacia). */
	public ArrayList<Lava> actualizar(float delta, float objetivoX, float objetivoY) {
		reloj += delta;
		rotacion = 14f * MathUtils.sin(reloj * 0.8f);   // se balancea, la herida sigue mirando hacia abajo
		setPosicion(getX() + velocidadHorizontal * delta, getY());
		for (int i = 0; i < ESCOMBROS; i++) escombroAng[i] += escombroVel[i] * delta;

		ArrayList<Lava> nuevas = new ArrayList<>();
		if (getX() > 0 && getX() < Constantes.ANCHO) {      // solo erupciona mientras esta en pantalla
			temporizadorLava += delta;
			if (temporizadorLava >= INTERVALO_LAVA) {
				temporizadorLava = 0f;
				int n = MathUtils.random(2, 4);
				for (int k = 0; k < n; k++) nuevas.add(crearLava(objetivoX, objetivoY));
			}
		}
		return nuevas;
	}

	/**
	 * Direccion de cada bola de lava: 25 % apuntada a la nave, 60 % en abanico hacia abajo
	 * y 15 % en cualquier direccion. Asi la mayor parte de la lava cae hacia donde esta el jugador.
	 */
	private Lava crearLava(float objetivoX, float objetivoY) {
		float sorteo = MathUtils.random();
		float ang;
		if (sorteo < 0.25f) {
			ang = MathUtils.atan2(objetivoY - getY(), objetivoX - getX()) * MathUtils.radiansToDegrees
					+ MathUtils.random(-10f, 10f);
		} else if (sorteo < 0.85f) {
			ang = -90f + MathUtils.random(-55f, 55f);
		} else {
			ang = MathUtils.random(0f, 360f);
		}
		float ox = getX() + MathUtils.cosDeg(ang) * getRadio() * 0.75f;
		float oy = getY() + MathUtils.sinDeg(ang) * getRadio() * 0.75f;
		float vel = MathUtils.random(150f, 260f);
		return new Lava(ox, oy, MathUtils.cosDeg(ang) * vel + velocidadHorizontal * 0.4f,
				MathUtils.sinDeg(ang) * vel);
	}

	public boolean haSalido() {
		return (velocidadHorizontal > 0 && getX() > Constantes.ANCHO + RADIO)
			|| (velocidadHorizontal < 0 && getX() < -RADIO);
	}

	/** Planeta roto con grietas de lava, una herida abierta y escombros a su alrededor. */
	@Override
	public void dibujar(ShapeRenderer shape) {
		float x = getX(), y = getY(), r = getRadio();
		float pulso = 0.5f + 0.5f * MathUtils.sin(reloj * 3f);

		for (int i = 0; i < SEGMENTOS; i++) {
			float ang = i * 360f / SEGMENTOS + rotacion;
			px[i] = x + MathUtils.cosDeg(ang) * r * perfil[i];
			py[i] = y + MathUtils.sinDeg(ang) * r * perfil[i];
		}

		shape.begin(ShapeType.Filled);
		// resplandor calido de la lava
		shape.setColor(1f, 0.35f, 0.05f, 0.07f);
		shape.circle(x, y, r * 1.45f, 40);
		shape.setColor(1f, 0.35f, 0.05f, 0.10f);
		shape.circle(x, y, r * 1.2f, 40);

		// cuerpo rocoso, sombreado con luz fija desde arriba a la izquierda
		for (int i = 0; i < SEGMENTOS; i++) {
			int j = (i + 1) % SEGMENTOS;
			float angMundo = (i + 0.5f) * 360f / SEGMENTOS + rotacion;
			float lum = 0.5f + 0.5f * MathUtils.cosDeg(angMundo - 135f);
			shape.setColor(0.14f + 0.16f * lum, 0.10f + 0.11f * lum, 0.10f + 0.10f * lum, 1f);
			shape.triangle(x, y, px[i], py[i], px[j], py[j]);
		}

		// grietas de lava
		for (int g = 0; g < GRIETAS; g++) {
			for (int p = 0; p < 4; p++) {
				float a0 = grietas[g][p * 2] + rotacion, f0 = grietas[g][p * 2 + 1];
				float a1 = grietas[g][p * 2 + 2] + rotacion, f1 = grietas[g][p * 2 + 3];
				float x0 = x + MathUtils.cosDeg(a0) * r * f0, y0 = y + MathUtils.sinDeg(a0) * r * f0;
				float x1 = x + MathUtils.cosDeg(a1) * r * f1, y1 = y + MathUtils.sinDeg(a1) * r * f1;
				shape.setColor(1f, 0.25f, 0f, 0.35f);
				shape.rectLine(x0, y0, x1, y1, 8f);
				shape.setColor(1f, 0.55f + 0.3f * pulso, 0.1f, 1f);
				shape.rectLine(x0, y0, x1, y1, 3f);
			}
		}

		// borde incandescente de la herida
		for (int i = 0; i < SEGMENTOS; i++) {
			int j = (i + 1) % SEGMENTOS;
			if (enHerida[i] && enHerida[j]) {
				shape.setColor(1f, 0.3f, 0f, 0.4f);
				shape.rectLine(px[i], py[i], px[j], py[j], 9f);
				shape.setColor(1f, 0.6f + 0.3f * pulso, 0.15f, 1f);
				shape.rectLine(px[i], py[i], px[j], py[j], 4f);
			}
		}

		// escombros que flotan alrededor
		for (int i = 0; i < ESCOMBROS; i++) {
			float ex = x + MathUtils.cosDeg(escombroAng[i]) * escombroRad[i];
			float ey = y + MathUtils.sinDeg(escombroAng[i]) * escombroRad[i];
			shape.setColor(0.22f, 0.16f, 0.14f, 1f);
			shape.circle(ex, ey, 3f + (i % 3), 8);
			shape.setColor(1f, 0.45f, 0.1f, 0.5f);
			shape.circle(ex, ey, 1.5f, 6);
		}
		shape.end();
	}
}
