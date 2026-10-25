package puppy.code;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** Utilidad para dibujar texto con sombra y que se lea bien sobre cualquier fondo. */
public final class Texto {

	private Texto() {}

	/** Dibuja el texto con el color actual de la fuente y una sombra oscura detras. */
	public static void sombra(SpriteBatch batch, BitmapFont f, String texto, float x, float y,
			float ancho, int alineacion) {
		Color c = f.getColor();
		float r = c.r, g = c.g, b = c.b, a = c.a;
		f.setColor(0f, 0f, 0f, a * 0.75f);
		f.draw(batch, texto, x + 2f, y - 2f, ancho, alineacion, false);
		f.setColor(r, g, b, a);
		f.draw(batch, texto, x, y, ancho, alineacion, false);
	}
}
