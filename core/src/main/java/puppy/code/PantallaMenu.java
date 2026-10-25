package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Align;

public class PantallaMenu extends PantallaBase {

	private static final int JUGAR = 0, DIFICULTAD = 1, SALIR = 2;
	private static final float Y_OPCION = 520f;      // parte alta de la primera opcion
	private static final float SEP_OPCION = 72f;

	private int indice = 0;
	private float reloj = 0f;
	private final ConfiguracionJuego config = ConfiguracionJuego.getInstancia();

	// decoracion
	private final Texture txNave;
	private final Texture txAsteroide;
	private final Sprite sprNave;
	private final Sprite astNormal;
	private final Sprite astDorado;
	private final Sprite astVerde;
	private float naveY = 330f;

	public PantallaMenu(SpaceNavigation game) {
		super(game);
		txNave = new Texture(Gdx.files.internal("MainShip3.png"));
		txAsteroide = new Texture(Gdx.files.internal("aGreyMedium4.png"));

		sprNave = new Sprite(txNave);
		sprNave.setSize(120f, 120f);
		sprNave.setOriginCenter();

		astNormal = crearAsteroide(95f, 1f, 1f, 1f);
		astDorado = crearAsteroide(60f, 1f, 0.85f, 0.2f);
		astVerde = crearAsteroide(48f, 0.3f, 1f, 0.3f);
	}

	private Sprite crearAsteroide(float tam, float r, float g, float b) {
		Sprite s = new Sprite(txAsteroide);
		s.setSize(tam, tam);
		s.setOriginCenter();
		s.setColor(r, g, b, 1f);
		return s;
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
	protected void actualizar(float delta) {
		reloj += delta;
		game.getFondo().update(delta);
		naveY = 330f + 14f * MathUtils.sin(reloj * 1.6f);
		sprNave.setPosition(150f, naveY);
		sprNave.setRotation(7f * MathUtils.sin(reloj * 0.9f));

		astNormal.setPosition(Constantes.ANCHO - 290f, 340f + 12f * MathUtils.sin(reloj * 1.1f));
		astNormal.setRotation(reloj * 25f);
		astDorado.setPosition(Constantes.ANCHO - 160f, 250f + 10f * MathUtils.sin(reloj * 1.4f + 1f));
		astDorado.setRotation(-reloj * 40f);
		astVerde.setPosition(Constantes.ANCHO - 230f, 450f + 9f * MathUtils.sin(reloj * 1.2f + 2f));
		astVerde.setRotation(reloj * 55f);
	}

	@Override
	protected void dibujar() {
		ShapeRenderer shape = game.getShape();
		prepararShape();
		game.getFondo().dibujar(shape);

		float cx = Constantes.ANCHO / 2f;
		shape.begin(ShapeType.Filled);
		// llama del motor de la nave decorativa
		float flama = 18f + 8f * MathUtils.sin(reloj * 30f);
		float fx = 150f + 60f;
		shape.setColor(1f, 0.55f, 0.1f, 0.9f);
		shape.triangle(fx - 9f, naveY + 6f, fx + 9f, naveY + 6f, fx, naveY + 6f - flama);
		shape.setColor(1f, 0.95f, 0.6f, 1f);
		shape.triangle(fx - 4f, naveY + 6f, fx + 4f, naveY + 6f, fx, naveY + 6f - flama * 0.55f);

		// barra de la opcion seleccionada y cursor animado
		float yTop = Y_OPCION - indice * SEP_OPCION;
		float cy = yTop - 13f;
		float osc = 6f * MathUtils.sin(reloj * 6f);
		shape.setColor(0.2f, 0.8f, 1f, 0.10f + 0.05f * MathUtils.sin(reloj * 3f));
		shape.rect(cx - 235f, cy - 26f, 470f, 52f);
		shape.setColor(0.2f, 0.8f, 1f, 0.7f);
		shape.rect(cx - 235f, cy - 26f, 470f, 2f);
		shape.rect(cx - 235f, cy + 24f, 470f, 2f);
		shape.setColor(1f, 0.9f, 0.2f, 1f);
		float xl = cx - 222f + osc;
		shape.triangle(xl, cy + 12f, xl, cy - 12f, xl + 17f, cy);
		float xr = cx + 222f - osc;
		shape.triangle(xr, cy + 12f, xr, cy - 12f, xr - 17f, cy);

		// panel de controles
		shape.setColor(0f, 0f, 0f, 0.35f);
		shape.rect(cx - 450f, 52f, 900f, 118f);
		shape.setColor(0.2f, 0.8f, 1f, 0.5f);
		shape.rect(cx - 450f, 168f, 900f, 2f);
		shape.end();

		game.getBatch().begin();
		BitmapFont f = game.getFont();

		// titulo con resplandor
		float esc = 4.4f + 0.1f * MathUtils.sin(reloj * 2f);
		f.getData().setScale(esc);
		f.setColor(0.1f, 0.6f, 1f, 0.28f);
		for (int i = 0; i < 4; i++) {
			float ox = (i == 0 ? 4f : i == 1 ? -4f : 0f);
			float oy = (i == 2 ? 4f : i == 3 ? -4f : 0f);
			f.draw(game.getBatch(), "SPACE SNAP", ox, 725f + oy, Constantes.ANCHO, Align.center, false);
		}
		f.setColor(0.55f, 0.95f, 1f, 1f);
		Texto.sombra(game.getBatch(), f, "SPACE SNAP", 0, 725f, Constantes.ANCHO, Align.center);

		f.getData().setScale(1.3f);
		f.setColor(0.75f, 0.85f, 1f, 1f);
		Texto.sombra(game.getBatch(), f, "Esquiva  -  Dispara  -  Sobrevive", 0, 640f, Constantes.ANCHO, Align.center);

		// opciones
		f.getData().setScale(2f);
		String[] ops = opciones();
		for (int i = 0; i < ops.length; i++) {
			boolean sel = (i == indice);
			f.setColor(sel ? Color.YELLOW : Color.WHITE);
			Texto.sombra(game.getBatch(), f, ops[i], 0, Y_OPCION - i * SEP_OPCION, Constantes.ANCHO, Align.center);
		}
		if (indice == DIFICULTAD) {
			f.getData().setScale(1f);
			f.setColor(Color.LIGHT_GRAY);
			Texto.sombra(game.getBatch(), f, "Izquierda / Derecha para cambiar", 0,
					Y_OPCION - SEP_OPCION - 30f, Constantes.ANCHO, Align.center);
		}

		// record
		f.getData().setScale(1.2f);
		f.setColor(1f, 0.85f, 0.3f, 1f);
		Texto.sombra(game.getBatch(), f, "RECORD  " + config.getHighScore(), 0, 740f, Constantes.ANCHO - 24f, Align.right);

		// controles
		f.getData().setScale(1.1f);
		f.setColor(Color.LIGHT_GRAY);
		Texto.sombra(game.getBatch(), f, "Menu:  Flechas para navegar  |  Enter para seleccionar", 0, 152f, Constantes.ANCHO, Align.center);
		f.setColor(Color.WHITE);
		Texto.sombra(game.getBatch(), f, "Juego:  Flechas mover  |  ESPACIO disparar  |  TAB cambiar movimiento  |  P pausa", 0, 120f, Constantes.ANCHO, Align.center);
		f.setColor(0.6f, 1f, 0.6f, 1f);
		Texto.sombra(game.getBatch(), f, "Asteroide verde: cura  |  Dorado: recarga  |  Cometa: no lo toques", 0, 88f, Constantes.ANCHO, Align.center);

		// decoracion
		sprNave.draw(game.getBatch());
		astNormal.draw(game.getBatch());
		astDorado.draw(game.getBatch());
		astVerde.draw(game.getBatch());

		f.setColor(Color.WHITE);
		f.getData().setScale(2f);
		game.getBatch().end();
	}

	@Override
	public void dispose() {
		txNave.dispose();
		txAsteroide.dispose();
	}
}
