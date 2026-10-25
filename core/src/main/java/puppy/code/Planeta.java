package puppy.code;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/**
 * Cuerpo celeste con zona de influencia gravitatoria. La zona afecta a los asteroides pero
 * no se dibuja. El jefe y el planeta errante heredan de esta clase.
 */
public class Planeta {
	private float x, y;
	private final float radio, radioZona, gravedad;

	public Planeta(float x, float y, float radio, float radioZona, float gravedad) {
		this.x = x; this.y = y; this.radio = radio; this.radioZona = radioZona; this.gravedad = gravedad;
	}

	public boolean contieneEnZona(Ball2 a) {
		float dx = x - a.getCentroX();
		float dy = y - a.getCentroY();
		return dx * dx + dy * dy < radioZona * radioZona;
	}

	/** Dibujo basico (las subclases lo sobrescriben con su propio diseno). */
	public void dibujar(ShapeRenderer shape) {
		shape.begin(ShapeType.Filled);
		shape.setColor(0.85f, 0.45f, 0.2f, 1f);
		shape.circle(x, y, radio, 40);
		shape.end();
	}

	public void setPosicion(float x, float y) { this.x = x; this.y = y; }
	public float getX() { return x; }
	public float getY() { return y; }
	public float getRadio() { return radio; }
	public float getRadioZona() { return radioZona; }
	public float getGravedad() { return gravedad; }
}
