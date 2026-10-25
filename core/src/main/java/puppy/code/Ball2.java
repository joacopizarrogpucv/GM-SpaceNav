package puppy.code;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;

/** Asteroide. Su movimiento se delega en una EstrategiaMovimiento (patron Strategy). */
public class Ball2 {

	private final Vector2 posicion = new Vector2();   // esquina inferior izquierda
	private final Vector2 velocidad = new Vector2();  // px/s
	private final float tamano;
	private final Sprite spr;
	private EstrategiaMovimiento estrategia;
	private float factorVelocidad = 1f;
	private float enfriamiento = 0f;                  // evita choques repetidos entre los mismos asteroides
	private float angulo = 0f;
	private final TipoAsteroide tipo;                  // comun, dorado (item) o verde (cura)
	private float restante;                            // segundos que le quedan (solo dorado y verde)

	/** Solo se crea a traves de AsteroideBuilder. */
	Ball2(AsteroideBuilder b) {
		tamano = b.getTamano();
		spr = new Sprite(b.getTextura());
		spr.setSize(tamano, tamano);
		spr.setOriginCenter();
		posicion.set(b.getX(), b.getY());
		velocidad.set(b.getVx(), b.getVy());
		estrategia = b.getEstrategia();
		tipo = b.getTipo();
		restante = tipo.getVidaUtil();
		spr.setColor(tipo.getR(), tipo.getG(), tipo.getB(), 1f);   // dorado / verde / sin tono
		spr.setPosition(posicion.x, posicion.y);
	}

	public void update(float delta) {
		if (enfriamiento > 0) enfriamiento -= delta;
		if (tipo.getVidaUtil() > 0) restante -= delta;
		estrategia.mover(this, delta);
		rebotarEnBordes();
		angulo += 30f * delta;
		spr.setRotation(angulo);
		spr.setPosition(posicion.x, posicion.y);
	}

	/** Desplaza el asteroide segun su velocidad actual (usada por las estrategias). */
	public void avanzar(float delta) {
		posicion.mulAdd(velocidad, delta * factorVelocidad);
	}

	private void rebotarEnBordes() {
		if (posicion.x < 0) { posicion.x = 0; velocidad.x = Math.abs(velocidad.x); }
		else if (posicion.x + tamano > Constantes.ANCHO) { posicion.x = Constantes.ANCHO - tamano; velocidad.x = -Math.abs(velocidad.x); }
		if (posicion.y < 0) { posicion.y = 0; velocidad.y = Math.abs(velocidad.y); }
		else if (posicion.y + tamano > Constantes.ALTO) { posicion.y = Constantes.ALTO - tamano; velocidad.y = -Math.abs(velocidad.y); }
	}

	/** Colision entre asteroides: ambos se desvian entre 35 y 45 grados. */
	public void chocar(Ball2 otro) {
		if (enfriamiento > 0 || otro.enfriamiento > 0) return;

		float dx = getCentroX() - otro.getCentroX();
		float dy = getCentroY() - otro.getCentroY();
		float dist = (float) Math.sqrt(dx * dx + dy * dy);
		float minimo = getRadio() + otro.getRadio();
		if (dist >= minimo) return;

		desviar();
		otro.desviar();

		// separarlos para que no queden pegados
		Vector2 n = new Vector2(dx, dy);
		if (n.len2() == 0) n.set(1, 0);
		n.nor();
		float solape = minimo - dist;
		posicion.mulAdd(n, solape / 2f);
		otro.posicion.mulAdd(n, -solape / 2f);

		enfriamiento = 0.4f;
		otro.enfriamiento = 0.4f;
	}

	private void desviar() {
		float grados = MathUtils.random(35f, 45f) * (MathUtils.randomBoolean() ? 1 : -1);
		float c = MathUtils.cosDeg(grados);
		float s = MathUtils.sinDeg(grados);
		float nx = velocidad.x * c - velocidad.y * s;
		float ny = velocidad.x * s + velocidad.y * c;
		velocidad.set(nx, ny);
	}

	/** Los asteroides especiales parpadean en sus ultimos 3 segundos antes de desaparecer. */
	public void draw(SpriteBatch batch) {
		boolean porDesaparecer = tipo.getVidaUtil() > 0 && restante < 3f;
		spr.setAlpha(porDesaparecer ? 0.3f + 0.7f * Math.abs(MathUtils.sin(restante * 8f)) : 1f);
		spr.draw(batch);
	}

	/** true cuando un asteroide dorado o verde ya agoto su tiempo y debe desaparecer. */
	public boolean isExpirado() { return tipo.getVidaUtil() > 0 && restante <= 0; }

	// ---- utilidades para las estrategias ----
	public void sumarVelocidad(float ax, float ay) { velocidad.add(ax, ay); }
	public void limitarVelocidad(float max) { if (velocidad.len2() > max * max) velocidad.setLength(max); }
	public void alejarDe(float cx, float cy) {
		Vector2 d = new Vector2(getCentroX() - cx, getCentroY() - cy);
		if (d.len2() == 0) d.set(1, 0);
		velocidad.set(d.nor().scl(velocidad.len()));
	}

	// ---- getters / setters ----
	public float getCentroX() { return posicion.x + tamano / 2f; }
	public float getCentroY() { return posicion.y + tamano / 2f; }
	public TipoAsteroide getTipo() { return tipo; }
	public float getRadio() { return tamano / 2f * 0.9f; }
	public void setEstrategia(EstrategiaMovimiento e) { this.estrategia = e; }
	public void setFactorVelocidad(float f) { this.factorVelocidad = f; }
}
