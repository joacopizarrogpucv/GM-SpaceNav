package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;

/**
 * Modo nuevo 2 (estilo Asteroids): IZQ/DER o A/D rotan, ARRIBA/W acelera,
 * ABAJO/S frena. Los disparos salen en la direccion en que apunta la nave.
 */
public class MovimientoRotacional implements EstrategiaMovimientoNave {

	private static final float GIRO = 200f;      // grados por segundo
	private static final float EMPUJE = 450f;    // px/s^2
	private static final float ROCE = 0.8f;
	private static final float MAX = 450f;

	@Override
	public void mover(Nave4 n, float delta) {
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) n.setAngulo(n.getAngulo() + GIRO * delta);
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) n.setAngulo(n.getAngulo() - GIRO * delta);

		float vx = n.getXVel();
		float vy = n.getYVel();

		if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) {
			vx += -MathUtils.sinDeg(n.getAngulo()) * EMPUJE * delta;
			vy +=  MathUtils.cosDeg(n.getAngulo()) * EMPUJE * delta;
		}
		float roce = (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) ? 3.5f : ROCE;
		float factor = Math.max(0f, 1f - roce * delta);
		vx *= factor;
		vy *= factor;

		float v = (float) Math.sqrt(vx * vx + vy * vy);
		if (v > MAX) { vx = vx / v * MAX; vy = vy / v * MAX; }
		n.setXVel(vx);
		n.setYVel(vy);
	}

	@Override
	public String getNombre() { return "Rotacional"; }
}
