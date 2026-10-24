package puppy.code;

/** Tipos de asteroide. El tipo define el tono del sprite y lo que ocurre al destruirlo con una bala. */
public enum TipoAsteroide {
	COMUN (1.0f, 1.0f, 1.0f),   // sin efecto
	DORADO(1.0f, 0.85f, 0.2f),  // suelta un item que recarga toda la municion
	VERDE (0.3f, 1.0f, 0.4f);   // cura 1 vida

	private final float r, g, b;

	TipoAsteroide(float r, float g, float b) {
		this.r = r; this.g = g; this.b = b;
	}

	public float getR() { return r; }
	public float getG() { return g; }
	public float getB() { return b; }
}
