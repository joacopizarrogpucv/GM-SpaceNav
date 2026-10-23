package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.MathUtils;

/** Modo original del juego: cada pulsacion suma/resta velocidad y la nave sigue deslizando. */
public class MovimientoInercia implements EstrategiaMovimientoNave {

	private static final float PASO = 60f;
	private static final float MAX = 420f;

	@Override
	public void mover(Nave4 n, float delta) {
		if (Gdx.input.isKeyJustPressed(Input.Keys.LEFT))  n.setXVel(MathUtils.clamp(n.getXVel() - PASO, -MAX, MAX));
		if (Gdx.input.isKeyJustPressed(Input.Keys.RIGHT)) n.setXVel(MathUtils.clamp(n.getXVel() + PASO, -MAX, MAX));
		if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN))  n.setYVel(MathUtils.clamp(n.getYVel() - PASO, -MAX, MAX));
		if (Gdx.input.isKeyJustPressed(Input.Keys.UP))    n.setYVel(MathUtils.clamp(n.getYVel() + PASO, -MAX, MAX));
	}

	@Override
	public String getNombre() { return "Inercia"; }
}
