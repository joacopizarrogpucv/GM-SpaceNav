package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;

public class PantallaGameOver extends PantallaBase {

	private final int puntaje;
	private final boolean esRecord;
	private final float tiempo;
	private final int jefesDerrotados;
	private final int asteroidesDestruidos;
	private float reloj = 0f;

	public PantallaGameOver(SpaceNavigation game, int puntaje, boolean esRecord, float tiempo,
			int jefesDerrotados, int asteroidesDestruidos) {
		super(game);
		this.puntaje = puntaje;
		this.esRecord = esRecord;
		this.tiempo = tiempo;
		this.jefesDerrotados = jefesDerrotados;
		this.asteroidesDestruidos = asteroidesDestruidos;
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
	protected void actualizar(float delta) {
		reloj += delta;
		game.getFondo().update(delta);
	}

	@Override
	protected void dibujar() {
		ShapeRenderer shape = game.getShape();
		prepararShape();
		game.getFondo().dibujar(shape);

		float cx = Constantes.ANCHO / 2f;
		float pw = 580f, ph = 320f, px = cx - pw / 2f, py = 235f;
		shape.begin(ShapeType.Filled);
		shape.setColor(0.35f, 0.02f, 0.02f, 0.10f + 0.04f * MathUtils.sin(reloj * 2f));   // leve tono rojo de fondo
		shape.rect(0, 0, Constantes.ANCHO, Constantes.ALTO);
		shape.setColor(0.02f, 0.03f, 0.10f, 0.78f);
		shape.rect(px, py, pw, ph);
		shape.setColor(1f, 0.2f, 0.2f, 0.8f);
		shape.rect(px, py + ph - 4f, pw, 4f);
		for (int i = 0; i < 6; i++) {            // separadores entre filas
			shape.setColor(1f, 1f, 1f, 0.08f);
			shape.rect(px + 24f, 520f - i * 46f - 14f, pw - 48f, 1.5f);
		}
		shape.end();

		game.getBatch().begin();
		BitmapFont f = game.getFont();

		// titulo con resplandor rojo
		f.getData().setScale(4.6f + 0.1f * MathUtils.sin(reloj * 2.5f));
		f.setColor(1f, 0.1f, 0.1f, 0.25f);
		for (int i = 0; i < 4; i++) {
			float ox = (i == 0 ? 4f : i == 1 ? -4f : 0f);
			float oy = (i == 2 ? 4f : i == 3 ? -4f : 0f);
			f.draw(game.getBatch(), "GAME OVER", ox, 735f + oy, Constantes.ANCHO, Align.center, false);
		}
		f.setColor(1f, 0.25f, 0.25f, 1f);
		Texto.sombra(game.getBatch(), f, "GAME OVER", 0, 735f, Constantes.ANCHO, Align.center);

		if (esRecord) {
			f.getData().setScale(1.8f);
			float a = 0.65f + 0.35f * MathUtils.sin(reloj * 5f);
			f.setColor(1f, 0.85f, 0.2f, a);
			Texto.sombra(game.getBatch(), f, "NUEVO RECORD!", 0, 640f, Constantes.ANCHO, Align.center);
		}

		// estadisticas
		int seg = (int) tiempo;
		String[] etiquetas = { "Puntaje", "Record", "Tiempo sobrevivido", "Jefes derrotados",
				"Asteroides destruidos", "Dificultad" };
		String[] valores = { String.valueOf(puntaje), String.valueOf(ConfiguracionJuego.getInstancia().getHighScore()),
				String.format("%02d:%02d", seg / 60, seg % 60), String.valueOf(jefesDerrotados),
				String.valueOf(asteroidesDestruidos), ConfiguracionJuego.getInstancia().getDificultad().getNombre() };
		for (int i = 0; i < etiquetas.length; i++) {
			float y = 520f - i * 46f;
			f.getData().setScale(i == 0 ? 1.9f : 1.5f);
			f.setColor(0.75f, 0.85f, 1f, 1f);
			Texto.sombra(game.getBatch(), f, etiquetas[i], px + 30f, y, 0, Align.left);
			f.setColor(i == 0 ? Color.GOLD : Color.WHITE);
			Texto.sombra(game.getBatch(), f, valores[i], px + 30f, y, pw - 60f, Align.right);
		}

		// opciones
		f.getData().setScale(1.8f);
		f.setColor(1f, 0.92f, 0.3f, 0.65f + 0.35f * MathUtils.sin(reloj * 4f));
		Texto.sombra(game.getBatch(), f, "ENTER  -  Reiniciar", 0, 190f, Constantes.ANCHO, Align.center);
		f.getData().setScale(1.4f);
		f.setColor(Color.LIGHT_GRAY);
		Texto.sombra(game.getBatch(), f, "M / ESC  -  Menu principal", 0, 140f, Constantes.ANCHO, Align.center);

		f.setColor(Color.WHITE);
		f.getData().setScale(2f);
		game.getBatch().end();
	}
}
