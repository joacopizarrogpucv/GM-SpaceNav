package puppy.code;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;

public class Bullet {

	private final Vector2 velocidad;
	private boolean destroyed = false;
	private final Sprite spr;

	public Bullet(float cx, float cy, float vx, float vy, float angulo, Texture tx) {
		spr = new Sprite(tx);
		spr.setOriginCenter();
		spr.setCenter(cx, cy);
		spr.setRotation(angulo);
		velocidad = new Vector2(vx, vy);
	}

	public void update(float delta) {
		spr.translate(velocidad.x * delta, velocidad.y * delta);
		if (spr.getX() < 0 || spr.getX() + spr.getWidth() > Constantes.ANCHO
				|| spr.getY() < 0 || spr.getY() + spr.getHeight() > Constantes.ALTO) {
			destroyed = true;
		}
	}

	public void draw(SpriteBatch batch) { spr.draw(batch); }

	public boolean checkCollision(Ball2 b2) {
		return checkCollision(b2.getCentroX(), b2.getCentroY(), b2.getRadio());
	}

	/** Colision contra un circulo cualquiera (asteroides, jefe). */
	public boolean checkCollision(float cx, float cy, float r) {
		float dx = spr.getX() + spr.getWidth() / 2f - cx;
		float dy = spr.getY() + spr.getHeight() / 2f - cy;
		if (dx * dx + dy * dy < r * r) {
			destroyed = true;
			return true;
		}
		return false;
	}

	/** Refleja una bala que entra al campo protector de un planeta errante. */
	public boolean rebotarEnPlaneta(float cx, float cy, float radio) {
		float dx = spr.getX() + spr.getWidth() / 2f - cx;
		float dy = spr.getY() + spr.getHeight() / 2f - cy;
		float dist2 = dx * dx + dy * dy;
		if (destroyed || dist2 >= radio * radio) return false;
		float dist = (float) Math.sqrt(dist2);
		float nx = dist < 0.001f ? 0f : dx / dist;
		float ny = dist < 0.001f ? 1f : dy / dist;
		float componenteEntrante = velocidad.x * nx + velocidad.y * ny;
		if (componenteEntrante < 0f) {
			velocidad.x -= 2f * componenteEntrante * nx;
			velocidad.y -= 2f * componenteEntrante * ny;
		}
		// Separa la bala del planeta para evitar que rebote cada fotograma.
		spr.setCenter(cx + nx * (radio + 5f), cy + ny * (radio + 5f));
		spr.setRotation((float) Math.toDegrees(Math.atan2(-velocidad.x, velocidad.y)));
		return true;
	}

	public boolean isDestroyed() { return destroyed; }
}
