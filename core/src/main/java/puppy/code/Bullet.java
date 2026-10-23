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

	public boolean isDestroyed() { return destroyed; }
}
