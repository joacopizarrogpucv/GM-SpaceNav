package puppy.code;

import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;

/** Minijefe que cruza la pantalla y modifica la trayectoria de asteroides cercanos. */
public final class PlanetaErrante extends Planeta {

    private static final float RADIO = 48f;
    private static final float RADIO_ZONA = 210f;
    private static final float FUERZA_GRAVEDAD = 190f;
    private static final float VELOCIDAD = 155f;

    private final float velocidadHorizontal;

    public PlanetaErrante(boolean vieneDeIzquierda, float altura) {
        super(vieneDeIzquierda ? -RADIO : Constantes.ANCHO + RADIO,
              altura, RADIO, RADIO_ZONA, FUERZA_GRAVEDAD);
        velocidadHorizontal = vieneDeIzquierda ? VELOCIDAD : -VELOCIDAD;
    }

    public void actualizar(float delta) {
        setPosicion(getX() + velocidadHorizontal * delta, getY());
    }

    public boolean haSalido() {
        return (velocidadHorizontal > 0 && getX() > Constantes.ANCHO + RADIO)
            || (velocidadHorizontal < 0 && getX() < -RADIO);
    }

    @Override
    public void dibujar(ShapeRenderer shape) {
        super.dibujar(shape);
        // Anillo exterior que lo diferencia del jefe y anticipa su trayectoria.
        shape.begin(ShapeType.Line);
        shape.setColor(1f, 0.85f, 0.25f, 0.9f);
        shape.circle(getX(), getY(), getRadio() + 12f, 48);
        shape.end();
    }
}
