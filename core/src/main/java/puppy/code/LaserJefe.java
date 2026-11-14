package puppy.code;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;

/** Rayo laser rojo disparado por la Estrella de la Muerte. */
public class LaserJefe extends Proyectil {

	private static final float LARGO = 40f;

	private final float dirX, dirY;
	private boolean destruido = false;

	/** angulo en grados (0 = derecha, 90 = arriba, -90 = abajo). */
	public LaserJefe(float x, float y, float anguloGrados, float velocidad) {
		super(x, y, MathUtils.cosDeg(anguloGrados) * velocidad,
				MathUtils.sinDeg(anguloGrados) * velocidad);
		this.dirX = MathUtils.cosDeg(anguloGrados);
		this.dirY = MathUtils.sinDeg(anguloGrados);
	}

	@Override
	public void update(float delta) {
		avanzar(delta);
		if (fueraDePantalla(60f)) destruido = true;
	}

	/** Debe llamarse con el ShapeRenderer ya iniciado en modo Filled. */
	@Override
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

	@Override
	public boolean haTerminado() { return destruido; }
	public boolean isDestruido() { return haTerminado(); }
}
