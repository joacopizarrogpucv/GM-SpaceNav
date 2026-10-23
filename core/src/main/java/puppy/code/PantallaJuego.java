package puppy.code;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public class PantallaJuego extends PantallaBase {

	private static final float TAM_BASE_ASTEROIDE = 50f;

	private final ConfiguracionJuego config = ConfiguracionJuego.getInstancia();
	private final Dificultad dificultad = config.getDificultad();
	private final SpriteBatch batch;

	// recursos
	private final Sound explosionSound;
	private final Music gameMusic;
	private final Texture txAsteroide;
	private final Texture txNave;

	// estado de la partida
	private int score = 0;
	private float tiempo = 0f;
	private float acumuladorScore = 0f;
	private float acumuladorSpawn = 0f;
	private float factorVelocidad = 1f;
	private boolean pausado = false;

	private final Nave4 nave;
	private final ArrayList<Ball2> asteroides = new ArrayList<>();

	// estrategias
	private final EstrategiaMovimiento movRecto = new MovimientoRecto();
	private final EstrategiaMovimientoNave[] modosNave = {
			new MovimientoInercia(), new MovimientoDirecto(), new MovimientoRotacional() };
	private int modoActual;

	public PantallaJuego(SpaceNavigation game) {
		super(game);
		batch = game.getBatch();

		explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
		explosionSound.setVolume(1, 0.5f);
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("piano-loops.wav"));
		gameMusic.setLooping(true);
		gameMusic.setVolume(0.5f);

		txAsteroide = new Texture(Gdx.files.internal("aGreyMedium4.png"));
		txNave = new Texture(Gdx.files.internal("MainShip3.png"));

		modoActual = config.getModoNave() % modosNave.length;
		nave = new Nave4(Constantes.ANCHO / 2f - 22, 30, txNave,
				Gdx.audio.newSound(Gdx.files.internal("hurt.ogg")), modosNave[modoActual]);


		for (int i = 0; i < dificultad.getMaxAsteroides() / 2; i++) {
			asteroides.add(crearAsteroide());
		}
	}

	/** Usa el Builder para crear un asteroide acorde a la dificultad. */
	private Ball2 crearAsteroide() {
		float tam = TAM_BASE_ASTEROIDE * dificultad.escalaAleatoria();
		float vel = dificultad.getVelocidadBase() * MathUtils.random(0.9f, 1.1f);

		// aparece por arriba, izquierda o derecha (nunca encima de la nave)
		float x, y;
		switch (MathUtils.random(2)) {
		case 0:  x = MathUtils.random(0f, Constantes.ANCHO - tam); y = Constantes.ALTO - tam; break;
		case 1:  x = 0; y = MathUtils.random(Constantes.ALTO * 0.4f, Constantes.ALTO - tam); break;
		default: x = Constantes.ANCHO - tam; y = MathUtils.random(Constantes.ALTO * 0.4f, Constantes.ALTO - tam); break;
		}
		Vector2 dir = new Vector2(MathUtils.random(Constantes.ANCHO * 0.2f, Constantes.ANCHO * 0.8f) - x,
				MathUtils.random(0f, Constantes.ALTO * 0.6f) - y).nor();

		return new AsteroideBuilder()
				.posicion(x, y)
				.tamano(tam)
				.velocidad(dir.x * vel, dir.y * vel)
				.textura(txAsteroide)
				.estrategia(movRecto)
				.build();
	}

	@Override
	protected void procesarEntrada() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			pausado = !pausado;
			if (pausado) gameMusic.pause(); else gameMusic.play();
		}
		if (pausado) {
			if (Gdx.input.isKeyJustPressed(Input.Keys.M)) cambiarA(new PantallaMenu(game));
			return;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
			modoActual = (modoActual + 1) % modosNave.length;
			config.setModoNave(modoActual);
			nave.setEstrategia(modosNave[modoActual]);
		}
	}

	@Override
	protected void actualizar(float delta) {
		if (pausado) return;
		delta = Math.min(delta, 0.05f); // evita saltos si hay un tiron de FPS

		tiempo += delta;
		factorVelocidad = dificultad.factorVelocidad(tiempo);

		// puntaje por sobrevivir: +1 por segundo
		acumuladorScore += delta;
		if (acumuladorScore >= 1f) { acumuladorScore -= 1f; score++; }

		// aparicion de nuevos asteroides
		acumuladorSpawn += delta;
		if (acumuladorSpawn >= dificultad.getIntervaloSpawn()) {
			acumuladorSpawn = 0;
			if (asteroides.size() < dificultad.getMaxAsteroides()) asteroides.add(crearAsteroide());
		}

		// movimiento de asteroides
		for (Ball2 a : asteroides) {
			a.setFactorVelocidad(factorVelocidad);
			a.update(delta);
		}

		// colisiones entre asteroides
		for (int i = 0; i < asteroides.size(); i++) {
			for (int j = i + 1; j < asteroides.size(); j++) {
				asteroides.get(i).chocar(asteroides.get(j));
			}
		}

		// nave
		nave.actualizar(delta);
		for (int i = asteroides.size() - 1; i >= 0; i--) {
			if (nave.checkCollision(asteroides.get(i))) asteroides.remove(i);
		}

		if (nave.estaDestruido()) {
			config.registrarPuntaje(score);
			cambiarA(new PantallaGameOver(game, score));
		}
	}

	@Override
	protected void dibujar() {

		batch.begin();
		for (Ball2 a : asteroides) a.draw(batch);
		nave.dibujar(batch);
		dibujaEncabezado();

		if (pausado) {
			BitmapFont f = game.getFont();
			f.getData().setScale(3f);
			f.setColor(Color.YELLOW);
			f.draw(batch, "PAUSA", 0, 460, Constantes.ANCHO, Align.center, false);
			f.getData().setScale(1.6f);
			f.setColor(Color.WHITE);
			f.draw(batch, "P / ESC: continuar   |   M: menu principal", 0, 380, Constantes.ANCHO, Align.center, false);
		}
		batch.end();
	}

	private void dibujaEncabezado() {
		BitmapFont f = game.getFont();
		f.setColor(Color.WHITE);
		f.getData().setScale(1.6f);
		f.draw(batch, "Vidas: " + nave.getVidas(), 20, Constantes.ALTO - 15);
		f.draw(batch, "HighScore: " + config.getHighScore(), 0, Constantes.ALTO - 15, Constantes.ANCHO, Align.center, false);
		f.draw(batch, "Score: " + score, 0, Constantes.ALTO - 15, Constantes.ANCHO - 20, Align.right, false);

		f.getData().setScale(1.1f);
		f.setColor(Color.LIGHT_GRAY);
		f.draw(batch, "Dificultad: " + dificultad.getNombre()
				+ "  |  Movimiento: " + modosNave[modoActual].getNombre() + " (TAB)"
				+ "  |  Velocidad: x" + String.format("%.2f", factorVelocidad), 20, Constantes.ALTO - 50);
		f.setColor(Color.WHITE);
	}

	@Override
	public void show() {
		if (!pausado) gameMusic.play();
	}

	@Override
	public void dispose() {
		explosionSound.dispose();
		gameMusic.dispose();
		txAsteroide.dispose();
		txNave.dispose();
	}
}
