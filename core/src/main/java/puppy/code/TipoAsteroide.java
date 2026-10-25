package puppy.code;

/** Tipos de asteroide. El tipo define el tono del sprite y lo que ocurre al destruirlo con una bala. */
public enum TipoAsteroide {
	COMUN (1.0f, 1.0f, 1.0f,  0f),   // sin efecto, no desaparece
	DORADO(1.0f, 0.85f, 0.2f, 14f),  // suelta un item que recarga toda la municion; desaparece a los 14 s
	VERDE (0.3f, 1.0f, 0.4f,  10f);  // cura 1 vida; desaparece a los 10 s

	private final float r, g, b;
	private final float vidaUtil;   // segundos antes de desaparecer (0 = permanente)

	TipoAsteroide(float r, float g, float b, float vidaUtil) {
		this.r = r; this.g = g; this.b = b;
		this.vidaUtil = vidaUtil;
	}

	public float getVidaUtil() { return vidaUtil; }

	public float getR() { return r; }
	public float getG() { return g; }
	public float getB() { return b; }
}
