package puppy.code;

import java.util.ArrayList;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;

/**
 * Jefe: la Estrella de la Muerte. Es un Planeta (hereda su zona de gravedad) que dispara
 * rayos laser rojos. Vida y puntaje se multiplican por el numero de aparicion.
 */
public class JefeEstrella extends Planeta {

	public static final int VIDA_BASE = 1000;
	public static final int PUNTAJE_BASE = 400;

	private static final float RADIO = 130f;
	private static final float Y_OBJETIVO = Constantes.ALTO - 260f;
	private static final float VELOCIDAD_ENTRADA = 110f;

	private final int nivel;
	private final int vidaMax;
	private int vida;
	private final int puntaje;
	private final float intervaloLaser;
	private final float velocidadLaser;

	private boolean entrando = true;
	private float tiempoCombate = 0f;
	private float temporizadorAtaque = 0f;
	private int ataques = 0;
	private float reloj = 0f;
	private float destello = 0f;   // flash blanco breve al recibir un impacto

	public JefeEstrella(int nivel, Dificultad dificultad) {
		super(Constantes.ANCHO / 2f, Constantes.ALTO + RADIO + 20f, RADIO, 260f, 180f);
		this.nivel = nivel;
		this.vidaMax = VIDA_BASE * dificultad.getMultiplicadorVidaJefe() * nivel;   // en Dificil la vida base se duplica
		this.vida = vidaMax;
		this.puntaje = PUNTAJE_BASE * nivel;
		this.intervaloLaser = dificultad.getIntervaloLaser() * Math.max(0.6f, 1f - 0.05f * (nivel - 1));
		this.velocidadLaser = dificultad.getVelocidadLaser() * Math.min(1.5f, 1f + 0.08f * (nivel - 1));
	}

	/** Mueve al jefe y devuelve los lasers disparados en este cuadro (lista vacia casi siempre). */
	public ArrayList<LaserJefe> actualizar(float delta, float objetivoX, float objetivoY) {
		ArrayList<LaserJefe> nuevos = new ArrayList<>();
		reloj += delta;
		if (destello > 0) destello -= delta;

		if (entrando) {
			setPosicion(getX(), getY() - VELOCIDAD_ENTRADA * delta);
			if (getY() <= Y_OBJETIVO) {
				setPosicion(getX(), Y_OBJETIVO);
				entrando = false;
			}
			return nuevos;
		}

		// Movimiento segun el nivel: nivel 1 quieto; desde el 2 se desplaza de lado a lado,
		// cada vez mas rapido; desde el 3 tambien sube y baja.
		tiempoCombate += delta;
		float amplitudX = 0f, frecuencia = 0.35f, amplitudY = 0f;
		if (nivel >= 2) {
			amplitudX = Math.min(180f + 45f * (nivel - 2), 340f);
			frecuencia = Math.min(0.35f + 0.08f * (nivel - 2), 0.9f);
		}
		if (nivel >= 3) amplitudY = Math.min(30f + 25f * (nivel - 3), 110f);
		setPosicion(Constantes.ANCHO / 2f + MathUtils.sin(tiempoCombate * frecuencia) * amplitudX,
				Y_OBJETIVO + MathUtils.sin(tiempoCombate * (frecuencia + 0.4f)) * amplitudY);

		temporizadorAtaque += delta;
		if (temporizadorAtaque >= intervaloLaser) {
			temporizadorAtaque = 0f;
			ataques++;
			disparar(nuevos, objetivoX, objetivoY);
		}
		return nuevos;
	}

	/**
	 * Patrones de ataque (cada nivel agrega mas): rafaga apuntada a la nave, abanico hacia abajo
	 * y, desde el nivel 4, un anillo de rayos en todas direcciones cada 5 ataques.
	 */
	private void disparar(ArrayList<LaserJefe> lista, float objetivoX, float objetivoY) {
		float ox = getX() - getRadio() * 0.35f;   // sale desde el "superlaser"
		float oy = getY() + getRadio() * 0.4f;

		if (nivel >= 4 && ataques % 5 == 0) {
			int n = 12 + 2 * Math.min(nivel - 4, 3);
			float desfase = MathUtils.random(0f, 360f / n);
			for (int i = 0; i < n; i++) {
				lista.add(new LaserJefe(ox, oy, desfase + i * 360f / n, velocidadLaser * 0.85f));
			}
		} else if (ataques % 3 == 0) {
			int rayos = 9 + (nivel >= 3 ? 2 : 0) + (nivel >= 5 ? 2 : 0);
			float paso = 128f / (rayos - 1);
			for (int i = 0; i < rayos; i++) {
				lista.add(new LaserJefe(ox, oy, -154f + i * paso, velocidadLaser));
			}
		} else {
			int cantidad = 3 + (nivel >= 3 ? 2 : 0);
			float base = MathUtils.atan2(objetivoY - oy, objetivoX - ox) * MathUtils.radiansToDegrees;
			for (int i = 0; i < cantidad; i++) {
				lista.add(new LaserJefe(ox, oy, base + (i - (cantidad - 1) / 2f) * 10f, velocidadLaser));
			}
		}
	}

	/** Devuelve true si el jefe fue derrotado con este dano. */
	public boolean recibirDano(int dano) {
		vida = Math.max(0, vida - dano);
		destello = 0.12f;
		return vida == 0;
	}

	public boolean esVulnerable() { return !entrando; }
	public float getRadioImpacto() { return RADIO * 0.95f; }
	public float getCarga() { return Math.min(1f, temporizadorAtaque / intervaloLaser); }
	public int getNivel() { return nivel; }
	public int getVida() { return vida; }
	public int getVidaMax() { return vidaMax; }
	public int getPuntaje() { return puntaje; }

	private static final float[] ANG_GRIETA = { 200f, 320f, 250f, 30f, 290f, 160f };

	/** Dibuja la Estrella de la Muerte con formas simples (luces, ventanas, grietas por dano). */
	@Override
	public void dibujar(ShapeRenderer shape) {
		float x = getX(), y = getY(), r = getRadio();
		float carga = getCarga();

		shape.begin(ShapeType.Filled);
		// esfera base y sombreado
		shape.setColor(0.58f, 0.60f, 0.64f, 1f);
		shape.circle(x, y, r, 64);
		shape.setColor(1f, 1f, 1f, 0.10f);
		shape.circle(x - r * 0.25f, y + r * 0.25f, r * 0.5f, 40);
		shape.setColor(0f, 0f, 0f, 0.22f);
		shape.circle(x + r * 0.3f, y - r * 0.3f, r * 0.58f, 40);

		// ventanas encendidas en el hemisferio sur
		for (int gx = -7; gx <= 7; gx++) {
			for (int gy = -8; gy <= -1; gy++) {
				if (((gx * 7 + gy * 13) & 7) != 0) continue;
				float wx = x + gx * r * 0.105f, wy = y + gy * r * 0.095f;
				float dx = wx - x, dy = wy - y;
				if (dx * dx + dy * dy > r * r * 0.72f) continue;
				float luz = 0.45f + 0.45f * MathUtils.sin(reloj * 2.5f + gx * 1.7f + gy);
				shape.setColor(1f, 0.92f, 0.6f, luz);
				shape.rect(wx - 2.5f, wy - 1.5f, 5f, 3f);
			}
		}

		// trinchera ecuatorial, lineas de detalle y luces de la trinchera
		shape.setColor(0.18f, 0.19f, 0.22f, 1f);
		shape.rectLine(x - r * 0.985f, y, x + r * 0.985f, y, r * 0.07f);
		lineaDetalle(shape, x, y, r, 0.22f);
		lineaDetalle(shape, x, y, r, -0.22f);
		lineaDetalle(shape, x, y, r, 0.55f);
		lineaDetalle(shape, x, y, r, -0.55f);
		for (int i = 0; i < 26; i++) {
			float lx = x - r * 0.93f + i * (r * 1.86f / 25f);
			float luz = 0.35f + 0.55f * MathUtils.sin(reloj * 3f + i * 1.3f);
			shape.setColor(1f, 0.95f, 0.7f, Math.max(0.1f, luz));
			shape.circle(lx, y, 1.8f, 6);
		}

		// plato del superlaser
		float dx = x - r * 0.35f;
		float dy = y + r * 0.4f;
		shape.setColor(0.35f, 0.36f, 0.40f, 1f);
		shape.circle(dx, dy, r * 0.28f, 32);
		shape.setColor(0.27f, 0.28f, 0.32f, 1f);
		shape.circle(dx, dy, r * 0.23f, 32);
		shape.setColor(0.17f, 0.18f, 0.21f, 1f);
		shape.circle(dx, dy, r * 0.17f, 32);
		if (carga > 0.4f) {                        // anillo de carga que se expande
			float k = (carga - 0.4f) / 0.6f;
			shape.setColor(1f, 0.15f, 0.1f, 0.18f * k);
			shape.circle(dx, dy, r * 0.28f * (1f + 0.7f * k), 32);
		}
		shape.setColor(1f, 0.1f, 0.1f, 0.3f + 0.7f * carga);   // brilla rojo al cargar el disparo
		shape.circle(dx, dy, r * 0.06f + r * 0.05f * carga, 20);
		shape.setColor(1f, 0.8f, 0.8f, carga);
		shape.circle(dx, dy, r * 0.025f * carga + 1f, 12);

		// grietas incandescentes segun el dano recibido
		float danio = 1f - vida / (float) vidaMax;
		int grietas = (int) (danio * 6f + 0.0001f);
		for (int k = 0; k < grietas && k < ANG_GRIETA.length; k++) {
			float prevX = x + MathUtils.cosDeg(ANG_GRIETA[k]) * r * 0.3f;
			float prevY = y + MathUtils.sinDeg(ANG_GRIETA[k]) * r * 0.3f;
			for (int p = 1; p <= 5; p++) {
				float f = 0.3f + p * 0.13f;
				float a = ANG_GRIETA[k] + 9f * MathUtils.sin(f * 9f + k);
				float nx = x + MathUtils.cosDeg(a) * r * f;
				float ny = y + MathUtils.sinDeg(a) * r * f;
				shape.setColor(1f, 0.25f, 0f, 0.35f);
				shape.rectLine(prevX, prevY, nx, ny, 7f);
				shape.setColor(1f, 0.7f, 0.2f, 1f);
				shape.rectLine(prevX, prevY, nx, ny, 2.5f);
				prevX = nx;
				prevY = ny;
			}
		}

		// destello al recibir un impacto
		if (destello > 0) {
			shape.setColor(1f, 1f, 1f, 0.35f * (destello / 0.12f));
			shape.circle(x, y, r, 64);
		}
		shape.end();

		shape.begin(ShapeType.Line);
		shape.setColor(0.25f, 0.26f, 0.30f, 1f);
		shape.circle(x, y, r, 64);
		shape.end();
	}

	private void lineaDetalle(ShapeRenderer shape, float x, float y, float r, float d) {
		float mitad = (float) Math.sqrt(1f - d * d) * 0.96f * r;
		shape.rectLine(x - mitad, y + d * r, x + mitad, y + d * r, r * 0.02f);
	}
}
