package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;

/** Modo nuevo 1: movimiento directo en 8 direcciones (flechas o WASD), se detiene al soltar. */
public class MovimientoDirecto implements EstrategiaMovimientoNave {

	private static final float VELOCIDAD = 320f;

	@Override
	public void mover(Nave4 n, float delta) {
		float vx = 0, vy = 0;
		if (Gdx.input.isKeyPressed(Input.Keys.LEFT)  || Gdx.input.isKeyPressed(Input.Keys.A)) vx -= 1;
		if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) vx += 1;
		if (Gdx.input.isKeyPressed(Input.Keys.DOWN)  || Gdx.input.isKeyPressed(Input.Keys.S)) vy -= 1;
		if (Gdx.input.isKeyPressed(Input.Keys.UP)    || Gdx.input.isKeyPressed(Input.Keys.W)) vy += 1;
		if (vx != 0 && vy != 0) { vx *= 0.7071f; vy *= 0.7071f; }
		n.setXVel(vx * VELOCIDAD);
		n.setYVel(vy * VELOCIDAD);
	}

	@Override
	public String getNombre() { return "Directo"; }
}
