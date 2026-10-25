package puppy.code;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/** Rayo laser rojo disparado por la Estrella de la Muerte. */
public class LaserJefe {

	private static final float LARGO = 40f;

	private float x, y;
	private final float dirX, dirY, velocidad;
	private boolean destruido = false;

	/** angulo en grados (0 = derecha, 90 = arriba, -90 = abajo). */
	public LaserJefe(float x, float y, float anguloGrados, float velocidad) {
		this.x = x;
		this.y = y;
		this.dirX = MathUtils.cosDeg(anguloGrados);
		this.dirY = MathUtils.sinDeg(anguloGrados);
		this.velocidad = velocidad;
	}

	public void update(float delta) {
		x += dirX * velocidad * delta;
		y += dirY * velocidad * delta;
		if (x < -60 || x > Constantes.ANCHO + 60 || y < -60 || y > Constantes.ALTO + 60) destruido = true;
	}

	/** Debe llamarse con el ShapeRenderer ya iniciado en modo Filled. */
	public void dibujar(ShapeRenderer shape) {
		shape.setColor(1f, 0.05f, 0.05f, 0.22f);                         // halo
		shape.rectLine(x, y, x - dirX * LARGO, y - dirY * LARGO, 16f);
		shape.setColor(1f, 0.1f, 0.1f, 1f);
		shape.rectLine(x, y, x - dirX * LARGO, y - dirY * LARGO, 7f);
		shape.setColor(1f, 0.7f, 0.7f, 1f);
		shape.rectLine(x, y, x - dirX * LARGO, y - dirY * LARGO, 2.5f);
		shape.setColor(1f, 0.85f, 0.85f, 1f);                             // punta brillante
		shape.circle(x, y, 4.5f, 10);
	}

	public float getX() { return x; }
	public float getY() { return y; }
	public boolean isDestruido() { return destruido; }
}
