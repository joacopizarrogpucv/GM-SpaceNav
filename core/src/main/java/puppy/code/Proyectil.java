package puppy.code;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

/**
 * Abstraccion de proyectiles no basados en sprites (lava y laseres de jefe).
 * Centraliza posicion, velocidad y chequeo de limites; cada subclase conserva
 * sus reglas especificas de colision, vida y representacion.
 */
public abstract class Proyectil {
    protected float x, y, vx, vy;

    protected Proyectil(float x, float y, float vx, float vy) {
        this.x = x; this.y = y;
        this.vx = vx; this.vy = vy;
    }

    protected final void avanzar(float delta) {
        x += vx * delta;
        y += vy * delta;
    }

    protected final boolean fueraDePantalla(float margen) {
        return x < -margen || x > Constantes.ANCHO + margen
                || y < -margen || y > Constantes.ALTO + margen;
    }

    public final float getX() { return x; }
    public final float getY() { return y; }

    public abstract void update(float delta);
    public abstract void dibujar(ShapeRenderer shape);
    public abstract boolean haTerminado();
}
