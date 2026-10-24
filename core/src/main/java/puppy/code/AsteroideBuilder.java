package puppy.code;

import com.badlogic.gdx.graphics.Texture;

/**
 * PATRON BUILDER.
 * Problema: un asteroide tiene muchos parametros (posicion, tamano, velocidad, textura,
 * estrategia de movimiento, tipo) y varios son opcionales; un constructor con 8 argumentos es ilegible.
 * Solucion: construir el objeto paso a paso con una interfaz fluida y validar en build().
 */
public class AsteroideBuilder {

	private float x, y;
	private float tamano = 50f;
	private float vx, vy;
	private Texture textura;
	private EstrategiaMovimiento estrategia = new MovimientoRecto(); // valor por defecto
	private TipoAsteroide tipo = TipoAsteroide.COMUN;

	public AsteroideBuilder posicion(float x, float y) { this.x = x; this.y = y; return this; }
	public AsteroideBuilder tamano(float tamano) { this.tamano = tamano; return this; }
	public AsteroideBuilder velocidad(float vx, float vy) { this.vx = vx; this.vy = vy; return this; }
	public AsteroideBuilder textura(Texture textura) { this.textura = textura; return this; }
	public AsteroideBuilder estrategia(EstrategiaMovimiento e) { this.estrategia = e; return this; }

	public AsteroideBuilder tipo(TipoAsteroide tipo) { this.tipo = tipo; return this; }

	public Ball2 build() {
		if (textura == null) throw new IllegalStateException("El asteroide necesita una textura");
		if (tamano <= 0) throw new IllegalStateException("Tamano invalido");
		if (tipo == null) throw new IllegalStateException("El asteroide necesita un tipo");
		return new Ball2(this);
	}

	float getX() { return x; }
	float getY() { return y; }
	float getTamano() { return tamano; }
	float getVx() { return vx; }
	float getVy() { return vy; }
	Texture getTextura() { return textura; }
	EstrategiaMovimiento getEstrategia() { return estrategia; }
	TipoAsteroide getTipo() { return tipo; }
}
