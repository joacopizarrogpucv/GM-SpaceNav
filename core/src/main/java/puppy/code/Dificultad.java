package puppy.code;

import com.badlogic.gdx.math.MathUtils;

/** Parametros configurables de cada dificultad (evita duplicar codigo). */
public enum Dificultad {
	//            nombre     escMin escMax velBase incVel maxAst intervalo
	NORMAL ("Normal",  1.0f, 1.0f, 120f, 0.000f, 6, 2.0f),
	DIFICIL("Dificil", 1.0f, 1.5f, 130f, 0.015f, 9, 1.4f);

	private static final float FACTOR_MAXIMO = 2.2f;

	private final String nombre;
	private final float escalaMin;
	private final float escalaMax;
	private final float velocidadBase;
	private final float incrementoVelocidad; // por segundo de partida
	private final int maxAsteroides;
	private final float intervaloSpawn;

	Dificultad(String nombre, float escalaMin, float escalaMax, float velocidadBase,
			float incrementoVelocidad, int maxAsteroides, float intervaloSpawn) {
		this.nombre = nombre;
		this.escalaMin = escalaMin;
		this.escalaMax = escalaMax;
		this.velocidadBase = velocidadBase;
		this.incrementoVelocidad = incrementoVelocidad;
		this.maxAsteroides = maxAsteroides;
		this.intervaloSpawn = intervaloSpawn;
	}

	/** Multiplicador de velocidad segun el tiempo jugado (acotado). */
	public float factorVelocidad(float tiempo) {
		return Math.min(1f + incrementoVelocidad * tiempo, FACTOR_MAXIMO);
	}

	public float escalaAleatoria() {
		return MathUtils.random(escalaMin, escalaMax);
	}

	public Dificultad siguiente() {
		return values()[(ordinal() + 1) % values().length];
	}

	public String getNombre() { return nombre; }
	public float getVelocidadBase() { return velocidadBase; }
	public int getMaxAsteroides() { return maxAsteroides; }
	public float getIntervaloSpawn() { return intervaloSpawn; }
}
