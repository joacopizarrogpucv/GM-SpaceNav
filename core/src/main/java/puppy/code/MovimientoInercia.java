package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;

/** Modo inercia: flechas o WASD cambian la velocidad por pulsacion. */
public class MovimientoInercia implements EstrategiaMovimientoNave {

	private static final float PASO = 60f;
	private static final float MAX = 420f;

	@Override
	public void mover(Nave4 n, float delta) {
		if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT) || Gdx.input.isKeyJustPressed(Input.Keys.A)) n.setXVel(MathUtils.clamp(n.getXVel() - PASO, -MAX, MAX));
		if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT) || Gdx.input.isKeyJustPressed(Input.Keys.D)) n.setXVel(MathUtils.clamp(n.getXVel() + PASO, -MAX, MAX));
		if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) || Gdx.input.isKeyJustPressed(Input.Keys.S)) n.setYVel(MathUtils.clamp(n.getYVel() - PASO, -MAX, MAX));
		if (Gdx.input.isKeyJustPressed(Input.Keys.UP) || Gdx.input.isKeyJustPressed(Input.Keys.W)) n.setYVel(MathUtils.clamp(n.getYVel() + PASO, -MAX, MAX));
	}

	@Override
	public String getNombre() { return "Inercia"; }
}
