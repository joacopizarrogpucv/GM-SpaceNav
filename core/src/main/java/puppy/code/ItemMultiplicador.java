package puppy.code;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;

/**
 * Item que suelta el cometa (a veces). Al recogerlo, el puntaje vale x2 durante un tiempo.
 * Desaparece si no se recoge en VIDA_UTIL segundos (parpadea al final).
 */
public class ItemMultiplicador {

	public static final float VIDA_UTIL = 7f;
	private static final float RADIO = 22f;

	private final float x;
	private final float y;
	private float restante = VIDA_UTIL;

	public ItemMultiplicador(float x, float y) {
		this.x = MathUtils.clamp(x, RADIO, Constantes.ANCHO - RADIO);
		this.y = MathUtils.clamp(y, RADIO + 60f, Constantes.ALTO - 140f);
	}

	public void update(float delta) { restante -= delta; }

	/** Parpadea durante sus ultimos 2 segundos. */
	private boolean visible() {
		return restante > 2f || ((int) (restante * 6f) % 2 == 0);
	}

	public void dibujarFondo(ShapeRenderer shape) {
		if (!visible()) return;
		float pulso = 3f * MathUtils.sin(restante * 7f);
		shape.setColor(1f, 0.6f, 0.1f, 0.25f);
		shape.circle(x, y, RADIO + 8f + pulso, 28);
		shape.setColor(1f, 0.75f, 0.1f, 0.95f);
		shape.circle(x, y, RADIO, 28);
		shape.setColor(0.45f, 0.2f, 0f, 1f);
		shape.circle(x, y, RADIO - 5f, 28);
	}

	/** Dibuja el texto "x2" (con el SpriteBatch ya iniciado). */
	public void dibujarTexto(SpriteBatch batch, BitmapFont f) {
		if (!visible()) return;
		f.getData().setScale(1.3f);
		f.setColor(1f, 0.9f, 0.3f, 1f);
		f.draw(batch, "x2", x - 30f, y + 10f, 60f, Align.center, false);
	}

	public boolean colisionaCon(float cx, float cy, float r) {
		float dx = x - cx;
		float dy = y - cy;
		float rr = RADIO + r;
		return dx * dx + dy * dy < rr * rr;
	}

	public boolean isExpirado() { return restante <= 0; }
}
