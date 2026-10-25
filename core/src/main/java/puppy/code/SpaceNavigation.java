package puppy.code;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class SpaceNavigation extends Game {

	private SpriteBatch batch;
	private BitmapFont font;
	private ShapeRenderer shape;   // compartido por todas las pantallas
	private Fondo fondo;           // fondo de estrellas compartido

	@Override
	public void create() {
		batch = new SpriteBatch();
		font = new BitmapFont();      // Arial por defecto
		font.getData().setScale(2f);
		shape = new ShapeRenderer();
		fondo = new Fondo();
		setScreen(new PantallaMenu(this));
	}

	@Override
	public void render() {
		super.render(); // importante!
	}

	@Override
	public void dispose() {
		if (getScreen() != null) getScreen().dispose();
		batch.dispose();
		font.dispose();
		shape.dispose();
	}

	public SpriteBatch getBatch() { return batch; }
	public BitmapFont getFont() { return font; }
	public ShapeRenderer getShape() { return shape; }
	public Fondo getFondo() { return fondo; }
}
