package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/** Item que recarga TODA la municion de la nave. Desaparece si no se recoge a tiempo. */
public class ItemRecarga {

	private static final float VIDA_UTIL = 10f;
	private static final float RADIO = 18f;

	private final float x;
	private float y;
	private float restante = VIDA_UTIL;
	private final Sprite icono;

	public ItemRecarga(float x, float y, Texture txBala) {
		this.x = MathUtils.clamp(x, RADIO, Constantes.ANCHO - RADIO);
		this.y = MathUtils.clamp(y, RADIO, Constantes.ALTO - 120f);
		icono = new Sprite(txBala);
		float alto = 30f;
		float ratio = icono.getWidth() / icono.getHeight();
		icono.setSize(alto * ratio, alto);
		icono.setOriginCenter();
	}

	public void update(float delta) {
		y -= 20f * delta;
		if (y < RADIO) y = RADIO;
		restante -= delta;
	}

	/** Parpadea durante sus ultimos 3 segundos. */
	private boolean visible() {
		return restante > 3f || ((int) (restante * 5f) % 2 == 0);
	}

	public void dibujarFondo(ShapeRenderer shape) {
		if (!visible()) return;
		float pulso = 3f * MathUtils.sin(restante * 6f);
		shape.setColor(0.2f, 0.9f, 1f, 0.25f);
		shape.circle(x, y, RADIO + 4f + pulso, 24);
		shape.setColor(0.2f, 0.9f, 1f, 0.35f);
		shape.circle(x, y, RADIO - 2f, 24);
	}

	public void dibujar(SpriteBatch batch) {
		if (!visible()) return;
		icono.setCenter(x, y);
		icono.draw(batch);
	}

	public boolean colisionaCon(float cx, float cy, float r) {
		float dx = x - cx;
		float dy = y - cy;
		float rr = RADIO + r;
		return dx * dx + dy * dy < rr * rr;
	}

	public boolean isExpirado() { return restante <= 0; }
}
