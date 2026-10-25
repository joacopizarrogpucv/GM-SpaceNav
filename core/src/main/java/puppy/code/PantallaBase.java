package puppy.code;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;

/**
 * CLASE ABSTRACTA + PATRON TEMPLATE METHOD.
 * Problema: Menu, Juego y GameOver repetian el mismo ciclo (limpiar pantalla, camara,
 * proyeccion del batch, logica, dibujo).
 * Solucion: render() es el metodo plantilla (final) que fija el orden de los pasos;
 * las subclases solo implementan los pasos variables:
 * procesarEntrada() -> actualizar() -> dibujar().
 */
public abstract class PantallaBase implements Screen {

	protected final SpaceNavigation game;
	protected final OrthographicCamera camera;
	protected final Viewport viewport;
	private boolean terminada = false;

	public PantallaBase(SpaceNavigation game) {
		this.game = game;
		this.camera = new OrthographicCamera();
		this.viewport = new FitViewport(Constantes.ANCHO, Constantes.ALTO, camera);
	}

	// ----- metodo plantilla -----
	@Override
	public final void render(float delta) {
		procesarEntrada();
		if (!terminada) actualizar(delta);
		if (terminada) return;               // la pantalla ya fue reemplazada
		ScreenUtils.clear(0.01f, 0.01f, 0.06f, 1);
		camera.update();
		game.getBatch().setProjectionMatrix(camera.combined);
		dibujar();
	}

	// ----- pasos que definen las subclases -----
	protected abstract void procesarEntrada();
	protected abstract void actualizar(float delta);
	protected abstract void dibujar();

	/** Activa transparencias y asigna la camara al ShapeRenderer compartido (antes de dibujar formas). */
	protected void prepararShape() {
		Gdx.gl.glEnable(GL20.GL_BLEND);
		Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
		game.getShape().setProjectionMatrix(camera.combined);
	}

	/** Cambia de pantalla y libera los recursos de la actual. */
	protected void cambiarA(PantallaBase siguiente) {
		terminada = true;
		game.setScreen(siguiente);
		dispose();
	}

	@Override public void resize(int width, int height) { viewport.update(width, height, true); }
	@Override public void show() {}
	@Override public void pause() {}
	@Override public void resume() {}
	@Override public void hide() {}
	@Override public void dispose() {}
}
