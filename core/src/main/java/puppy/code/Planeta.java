package puppy.code;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/** Cuerpo celeste con zona de influencia gravitatoria. El jefe hereda de esta clase. */
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

	public void dibujarZona(ShapeRenderer shape) {
		shape.begin(ShapeType.Filled);
		shape.setColor(0.3f, 0.5f, 1f, 0.08f);
		shape.circle(x, y, radioZona, 60);
		shape.end();
		shape.begin(ShapeType.Line);
		shape.setColor(0.3f, 0.6f, 1f, 0.35f);
		shape.circle(x, y, radioZona, 60);
		shape.end();
	}

	public void dibujar(ShapeRenderer shape) {
		dibujarZona(shape);
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
