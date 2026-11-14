package puppy.code;

import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;

/**
 * BUILDER aplicado a un segundo dominio: nave del jugador.
 * Centraliza los datos obligatorios (textura, sonido y estrategia) y permite
 * configurar la posicion con metodos encadenables, sin tocar la logica de Nave4.
 */
public final class NaveBuilder {
    private float x, y;
    private Texture textura;
    private Sound sonidoHerido;
    private EstrategiaMovimiento<Nave4> estrategia;

    public NaveBuilder posicion(float x, float y) {
        this.x = x; this.y = y;
        return this;
    }
    public NaveBuilder textura(Texture textura) {
        this.textura = textura;
        return this;
    }
    public NaveBuilder sonidoHerido(Sound sonido) {
        this.sonidoHerido = sonido;
        return this;
    }
    public NaveBuilder estrategia(EstrategiaMovimiento<Nave4> estrategia) {
        this.estrategia = estrategia;
        return this;
    }
    public Nave4 build() {
        if (textura == null) throw new IllegalStateException("La nave requiere textura");
        if (sonidoHerido == null) throw new IllegalStateException("La nave requiere sonido de impacto");
        if (estrategia == null) throw new IllegalStateException("La nave requiere una estrategia");
        return new Nave4(x, y, textura, sonidoHerido, estrategia);
    }
}
