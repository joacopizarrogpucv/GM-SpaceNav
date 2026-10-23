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

	public JefeEstrella(int nivel, Dificultad dificultad) {
		super(Constantes.ANCHO / 2f, Constantes.ALTO + RADIO + 20f, RADIO, 260f, 180f);
		this.nivel = nivel;
		this.vidaMax = VIDA_BASE * nivel;
		this.vida = vidaMax;
		this.puntaje = PUNTAJE_BASE * nivel;
		this.intervaloLaser = dificultad.getIntervaloLaser() * Math.max(0.6f, 1f - 0.05f * (nivel - 1));
		this.velocidadLaser = dificultad.getVelocidadLaser() * Math.min(1.5f, 1f + 0.08f * (nivel - 1));
	}

	/** Mueve al jefe y devuelve los lasers disparados en este cuadro (lista vacia casi siempre). */
	public ArrayList<LaserJefe> actualizar(float delta, float objetivoX, float objetivoY) {
		ArrayList<LaserJefe> nuevos = new ArrayList<>();

		if (entrando) {
			setPosicion(getX(), getY() - VELOCIDAD_ENTRADA * delta);
			if (getY() <= Y_OBJETIVO) {
				setPosicion(getX(), Y_OBJETIVO);
				entrando = false;
			}
			return nuevos;
		}

		tiempoCombate += delta;
		setPosicion(Constantes.ANCHO / 2f + MathUtils.sin(tiempoCombate * 0.5f) * 320f,
				Y_OBJETIVO + MathUtils.sin(tiempoCombate * 0.9f) * 20f);

		temporizadorAtaque += delta;
		if (temporizadorAtaque >= intervaloLaser) {
			temporizadorAtaque = 0f;
			ataques++;
			disparar(nuevos, objetivoX, objetivoY);
		}
		return nuevos;
	}

	private void disparar(ArrayList<LaserJefe> lista, float objetivoX, float objetivoY) {
		float ox = getX() - getRadio() * 0.35f;   // sale desde el "superlaser"
		float oy = getY() + getRadio() * 0.4f;

		if (ataques % 3 == 0) {
			// abanico de 9 rayos hacia abajo
			for (int i = 0; i < 9; i++) {
				lista.add(new LaserJefe(ox, oy, -90f + (i - 4) * 16f, velocidadLaser));
			}
		} else {
			// rafaga de 3 rayos apuntados a la nave
			float base = MathUtils.atan2(objetivoY - oy, objetivoX - ox) * MathUtils.radiansToDegrees;
			for (int i = -1; i <= 1; i++) {
				lista.add(new LaserJefe(ox, oy, base + i * 10f, velocidadLaser));
			}
		}
	}

	/** Devuelve true si el jefe fue derrotado con este dano. */
	public boolean recibirDano(int dano) {
		vida = Math.max(0, vida - dano);
		return vida == 0;
	}

	public boolean esVulnerable() { return !entrando; }
	public float getRadioImpacto() { return RADIO * 0.95f; }
	public float getCarga() { return Math.min(1f, temporizadorAtaque / intervaloLaser); }
	public int getNivel() { return nivel; }
	public int getVida() { return vida; }
	public int getVidaMax() { return vidaMax; }
	public int getPuntaje() { return puntaje; }

	/** Dibuja la Estrella de la Muerte con formas simples. */
	@Override
	public void dibujar(ShapeRenderer shape) {
		dibujarZona(shape);
		float x = getX(), y = getY(), r = getRadio();

		shape.begin(ShapeType.Filled);
		// esfera base y sombreado
		shape.setColor(0.58f, 0.60f, 0.64f, 1f);
		shape.circle(x, y, r, 64);
		shape.setColor(1f, 1f, 1f, 0.10f);
		shape.circle(x - r * 0.25f, y + r * 0.25f, r * 0.5f, 40);
		shape.setColor(0f, 0f, 0f, 0.20f);
		shape.circle(x + r * 0.3f, y - r * 0.3f, r * 0.55f, 40);

		// trinchera ecuatorial y lineas de detalle
		shape.setColor(0.18f, 0.19f, 0.22f, 1f);
		shape.rectLine(x - r * 0.985f, y, x + r * 0.985f, y, r * 0.07f);
		lineaDetalle(shape, x, y, r, 0.22f);
		lineaDetalle(shape, x, y, r, -0.22f);
		lineaDetalle(shape, x, y, r, 0.55f);
		lineaDetalle(shape, x, y, r, -0.55f);

		// plato del superlaser
		float dx = x - r * 0.35f;
		float dy = y + r * 0.4f;
		shape.setColor(0.35f, 0.36f, 0.40f, 1f);
		shape.circle(dx, dy, r * 0.28f, 32);
		shape.setColor(0.20f, 0.21f, 0.24f, 1f);
		shape.circle(dx, dy, r * 0.20f, 32);
		float carga = getCarga();
		shape.setColor(1f, 0.1f, 0.1f, 0.3f + 0.7f * carga);   // brilla rojo al cargar el disparo
		shape.circle(dx, dy, r * 0.06f + r * 0.05f * carga, 20);
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
