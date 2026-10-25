package puppy.code;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/** Bola de lava lanzada por el planeta errante. Dana a la nave y destruye asteroides. */
public class Lava {

	private static final float VIDA = 3.4f;

	private float x, y, vx, vy;
	private final float radio;
	private float restante = VIDA;

	public Lava(float x, float y, float vx, float vy) {
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
		this.radio = MathUtils.random(8f, 12f);
	}

	public void update(float delta) {
		x += vx * delta;
		y += vy * delta;
		float roce = Math.max(0f, 1f - 0.15f * delta);
		vx *= roce;
		vy *= roce;
		restante -= delta;
	}

	public boolean choca(float cx, float cy, float r) {
		float dx = x - cx;
		float dy = y - cy;
		float rr = radio + r;
		return dx * dx + dy * dy < rr * rr;
	}

	public boolean isExpirada() {
		return restante <= 0 || x < -80 || x > Constantes.ANCHO + 80 || y < -80 || y > Constantes.ALTO + 80;
	}

	/** Debe llamarse con el ShapeRenderer ya iniciado en modo Filled. */
	public void dibujar(ShapeRenderer shape) {
		float a = Math.min(1f, restante / 0.6f);
		float v = (float) Math.sqrt(vx * vx + vy * vy);
		float ux = v > 0.001f ? vx / v : 0f;
		float uy = v > 0.001f ? vy / v : -1f;
		for (int k = 5; k >= 1; k--) {                       // estela
			shape.setColor(1f, 0.35f, 0.05f, (0.36f - k * 0.055f) * a);
			shape.circle(x - ux * k * radio * 0.9f, y - uy * k * radio * 0.9f, radio * (1f - k * 0.14f), 14);
		}
		shape.setColor(1f, 0.3f, 0f, 0.25f * a);               // resplandor
		shape.circle(x, y, radio * 1.8f, 20);
		shape.setColor(1f, 0.45f, 0.05f, a);                   // cuerpo
		shape.circle(x, y, radio, 18);
		shape.setColor(1f, 0.85f, 0.3f, a);                    // nucleo caliente
		shape.circle(x, y, radio * 0.5f, 14);
	}

	public float getX() { return x; }
	public float getY() { return y; }
	public float getRadio() { return radio; }
}
