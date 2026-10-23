package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;

public class PantallaGameOver extends PantallaBase {

	private final int puntaje;

	public PantallaGameOver(SpaceNavigation game, int puntaje) {
		super(game);
		this.puntaje = puntaje;
	}

	@Override
	protected void procesarEntrada() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			cambiarA(new PantallaJuego(game));
		} else if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.M)) {
			cambiarA(new PantallaMenu(game));
		}
	}

	@Override
	protected void actualizar(float delta) {}

	@Override
	protected void dibujar() {
		BitmapFont f = game.getFont();
		game.getBatch().begin();

		f.setColor(Color.RED);
		f.getData().setScale(3.5f);
		f.draw(game.getBatch(), "GAME OVER", 0, 600, Constantes.ANCHO, Align.center, false);

		f.setColor(Color.WHITE);
		f.getData().setScale(2f);
		f.draw(game.getBatch(), "Puntaje: " + puntaje, 0, 450, Constantes.ANCHO, Align.center, false);
		f.draw(game.getBatch(), "Record: " + ConfiguracionJuego.getInstancia().getHighScore(),
				0, 390, Constantes.ANCHO, Align.center, false);

		f.setColor(Color.YELLOW);
		f.draw(game.getBatch(), "ENTER: reiniciar", 0, 270, Constantes.ANCHO, Align.center, false);
		f.draw(game.getBatch(), "M / ESC: menu principal", 0, 210, Constantes.ANCHO, Align.center, false);

		f.setColor(Color.WHITE);
		game.getBatch().end();
	}
}
