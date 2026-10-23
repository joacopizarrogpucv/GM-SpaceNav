package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Align;

public class PantallaMenu extends PantallaBase {

	private static final int JUGAR = 0, DIFICULTAD = 1, SALIR = 2;
	private int indice = 0;
	private final ConfiguracionJuego config = ConfiguracionJuego.getInstancia();

	public PantallaMenu(SpaceNavigation game) {
		super(game);
	}

	private String[] opciones() {
		return new String[] { "Jugar", "Dificultad: " + config.getDificultad().getNombre(), "Salir" };
	}

	@Override
	protected void procesarEntrada() {
		int n = opciones().length;
		if (Gdx.input.isKeyJustPressed(Input.Keys.UP))   indice = (indice + n - 1) % n;
		if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN)) indice = (indice + 1) % n;

		if (indice == DIFICULTAD && (Gdx.input.isKeyJustPressed(Input.Keys.LEFT)
				|| Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))) {
			config.setDificultad(config.getDificultad().siguiente());
		}

		if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER)) {
			switch (indice) {
			case JUGAR:
				cambiarA(new PantallaJuego(game));
				break;
			case DIFICULTAD:
				config.setDificultad(config.getDificultad().siguiente());
				break;
			case SALIR:
				Gdx.app.exit();
				break;
			default:
				break;
			}
		}
	}

	@Override
	protected void actualizar(float delta) {}

	@Override
	protected void dibujar() {
		BitmapFont f = game.getFont();
		game.getBatch().begin();

		f.setColor(Color.CYAN);
		f.getData().setScale(3.5f);
		f.draw(game.getBatch(), "SPACE SNAP", 0, 650, Constantes.ANCHO, Align.center, false);

		f.getData().setScale(2f);
		String[] ops = opciones();
		for (int i = 0; i < ops.length; i++) {
			boolean sel = (i == indice);
			f.setColor(sel ? Color.YELLOW : Color.WHITE);
			String texto = sel ? "> " + ops[i] + " <" : ops[i];   // cursor visual
			f.draw(game.getBatch(), texto, 0, 470 - i * 70, Constantes.ANCHO, Align.center, false);
		}

		f.setColor(Color.LIGHT_GRAY);
		f.getData().setScale(1.2f);
		f.draw(game.getBatch(), "Flechas: navegar   Enter: seleccionar   (Izq/Der cambia dificultad)",
				0, 200, Constantes.ANCHO, Align.center, false);
		f.draw(game.getBatch(), "En juego: ESPACIO dispara | TAB cambia el modo de movimiento | P pausa",
				0, 160, Constantes.ANCHO, Align.center, false);

		f.setColor(Color.WHITE);
		f.getData().setScale(2f);
		game.getBatch().end();
	}
}
