package puppy.code;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;

/** Nave del jugador. La forma de moverla se delega en una EstrategiaMovimientoNave. */
public class Nave4 {

	private static final float TAM = 45f;
	private static final float VELOCIDAD_BALA = 500f;
	private static final float TIEMPO_HERIDO = 0.8f;
	public static final int MAX_VIDAS = 5;

	private boolean destruida = false;
	private int vidas = 3;
	private float xVel = 0;
	private float yVel = 0;
	private float angulo = 0;   // 0 = apunta hacia arriba
	private final Sprite spr;
	private final Sound sonidoHerido;
	private boolean herido = false;
	private float tiempoHerido;
	private EstrategiaMovimientoNave estrategia;

	public Nave4(float x, float y, Texture tx, Sound soundChoque, EstrategiaMovimientoNave estrategia) {
		this.sonidoHerido = soundChoque;
		this.estrategia = estrategia;
		spr = new Sprite(tx);
		spr.setBounds(x, y, TAM, TAM);
		spr.setOriginCenter();
	}

	public void actualizar(float delta) {
		if (herido) {
			tiempoHerido -= delta;
			if (tiempoHerido <= 0) herido = false;
			return;
		}
		estrategia.mover(this, delta);

		float nx = spr.getX() + xVel * delta;
		float ny = spr.getY() + yVel * delta;
		if (nx < 0) { nx = 0; xVel = Math.abs(xVel); }
		else if (nx + TAM > Constantes.ANCHO) { nx = Constantes.ANCHO - TAM; xVel = -Math.abs(xVel); }
		if (ny < 0) { ny = 0; yVel = Math.abs(yVel); }
		else if (ny + TAM > Constantes.ALTO) { ny = Constantes.ALTO - TAM; yVel = -Math.abs(yVel); }
		spr.setPosition(nx, ny);
		spr.setRotation(angulo);
	}

	public void dibujar(SpriteBatch batch) {
		if (herido) {
			float x = spr.getX();
			spr.setX(x + MathUtils.random(-2, 2));
			spr.draw(batch);
			spr.setX(x);
		} else {
			spr.draw(batch);
		}
	}

	/** Crea una bala que sale desde la punta de la nave en la direccion en que apunta. */
	public Bullet crearBala(Texture txBala) {
		float dirX = -MathUtils.sinDeg(angulo);
		float dirY = MathUtils.cosDeg(angulo);
		float cx = getCentroX() + dirX * TAM / 2f;
		float cy = getCentroY() + dirY * TAM / 2f;
		return new Bullet(cx, cy, dirX * VELOCIDAD_BALA, dirY * VELOCIDAD_BALA, angulo, txBala);
	}

	public boolean checkCollision(Ball2 b) {
		return checkCollision(b.getCentroX(), b.getCentroY(), b.getRadio());
	}

	/** Colision contra un circulo cualquiera (asteroide, laser). Si impacta, resta una vida. */
	public boolean checkCollision(float cx, float cy, float radio) {
		if (herido) return false;
		float dx = getCentroX() - cx;
		float dy = getCentroY() - cy;
		float r = TAM / 2f * 0.8f + radio;
		if (dx * dx + dy * dy < r * r) {
			vidas--;
			herido = true;
			tiempoHerido = TIEMPO_HERIDO;
			xVel = 0;
			yVel = 0;
			sonidoHerido.play();
			if (vidas <= 0) destruida = true;
			return true;
		}
		return false;
	}

	/** Suma una vida (asteroide verde). Devuelve false si ya esta al maximo. */
	public boolean curar() {
		if (vidas >= MAX_VIDAS) return false;
		vidas++;
		return true;
	}

	/** Muerte instantanea (cometa): ignora vidas e invulnerabilidad. */
	public void matar() {
		vidas = 0;
		destruida = true;
		herido = false;
		xVel = 0;
		yVel = 0;
		sonidoHerido.play();
	}

	public void setEstrategia(EstrategiaMovimientoNave e) {
		this.estrategia = e;
		xVel = 0;
		yVel = 0;
		angulo = 0;
		spr.setRotation(0);
	}

	public boolean estaDestruido() { return !herido && destruida; }
	public boolean estaHerido() { return herido; }
	public int getVidas() { return vidas; }
	public void setVidas(int vidas) { this.vidas = vidas; }
	public float getXVel() { return xVel; }
	public void setXVel(float v) { xVel = v; }
	public float getYVel() { return yVel; }
	public void setYVel(float v) { yVel = v; }
	public float getAngulo() { return angulo; }
	public void setAngulo(float a) { angulo = a; }
	public float getCentroX() { return spr.getX() + TAM / 2f; }
	public float getCentroY() { return spr.getY() + TAM / 2f; }
}
