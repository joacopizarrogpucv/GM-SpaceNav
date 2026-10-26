package puppy.code;

import com.badlogic.gdx.math.MathUtils;

/** Parametros configurables de cada dificultad (evita duplicar codigo). */
public enum Dificultad {
	//         nombre     escMin escMax velBase incVel maxAst interv maxBalas escGrande probItem probCura intLaser velLaser astJefe topeJefe vidaJefe
	NORMAL ("Normal",  1.0f, 1.0f, 120f, 0.000f, 17, 1.00f,  5, 1.0f, 0.05f, 0.03f, 1.8f, 300f, 7, 9, 1),
	DIFICIL("Dificil", 1.0f, 1.5f, 130f, 0.015f, 21, 0.65f, 10, 1.3f, 0.27f, 0.03f, 1.1f, 380f, 8, 12, 2);

	private static final float FACTOR_MAXIMO = 2.2f;

	private final String nombre;
	private final float escalaMin;
	private final float escalaMax;
	private final float velocidadBase;
	private final float incrementoVelocidad; // por segundo de partida
	private final int maxAsteroides;
	private final float intervaloSpawn;
	private final int maxBalas;
	private final float escalaGrande;        // desde esta escala un asteroide puede llevar item
	private final float probabilidadItem;
	private final float probabilidadCura;    // asteroide verde: mucho mas raro que el dorado
	private final float intervaloLaser;      // jefe: segundos entre ataques
	private final float velocidadLaser;      // jefe: px/s
	private final int asteroidesJefe;        // asteroides maximos en pantalla durante el jefe nivel 1
	private final int topeAsteroidesJefe;    // tope al que sube con los niveles del jefe
	private final int multiplicadorVidaJefe; // vida base del jefe = 1000 x este valor x nivel

	Dificultad(String nombre, float escalaMin, float escalaMax, float velocidadBase,
			float incrementoVelocidad, int maxAsteroides, float intervaloSpawn, int maxBalas,
			float escalaGrande, float probabilidadItem, float probabilidadCura, float intervaloLaser, float velocidadLaser,
			int asteroidesJefe, int topeAsteroidesJefe, int multiplicadorVidaJefe) {
		this.nombre = nombre;
		this.escalaMin = escalaMin;
		this.escalaMax = escalaMax;
		this.velocidadBase = velocidadBase;
		this.incrementoVelocidad = incrementoVelocidad;
		this.maxAsteroides = maxAsteroides;
		this.intervaloSpawn = intervaloSpawn;
		this.maxBalas = maxBalas;
		this.escalaGrande = escalaGrande;
		this.probabilidadItem = probabilidadItem;
		this.probabilidadCura = probabilidadCura;
		this.intervaloLaser = intervaloLaser;
		this.velocidadLaser = velocidadLaser;
		this.asteroidesJefe = asteroidesJefe;
		this.topeAsteroidesJefe = topeAsteroidesJefe;
		this.multiplicadorVidaJefe = multiplicadorVidaJefe;
	}

	/** Multiplicador de velocidad segun el tiempo jugado (acotado). */
	public float factorVelocidad(float tiempo) {
		return Math.min(1f + incrementoVelocidad * tiempo, FACTOR_MAXIMO);
	}

	/** Cantidad maxima de asteroides: crece +1 cada 40 s (hasta +4). */
	public int maxAsteroidesEn(float tiempo) {
		return maxAsteroides + Math.min(4, (int) (tiempo / 40f));
	}

	/** Asteroides maximos mientras hay jefe: sube 1 por nivel del jefe hasta el tope. */
	public int maxAsteroidesJefe(int nivelJefe) {
		return Math.min(topeAsteroidesJefe, asteroidesJefe + nivelJefe - 1);
	}

	public float escalaAleatoria() {
		return MathUtils.random(escalaMin, escalaMax);
	}

	public boolean esGrande(float escala) {
		return escala >= escalaGrande;
	}

	/** Elige el tipo de un asteroide nuevo. El verde solo sale si la nave puede curarse. */
	public TipoAsteroide tipoAleatorio(float escala, boolean puedeCurar) {
		if (esGrande(escala) && MathUtils.randomBoolean(probabilidadItem)) return TipoAsteroide.DORADO;
		if (puedeCurar && MathUtils.randomBoolean(probabilidadCura)) return TipoAsteroide.VERDE;
		return TipoAsteroide.COMUN;
	}

	public Dificultad siguiente() {
		return values()[(ordinal() + 1) % values().length];
	}

	public String getNombre() { return nombre; }
	public float getVelocidadBase() { return velocidadBase; }
	public int getMaxAsteroides() { return maxAsteroides; }
	public float getIntervaloSpawn() { return intervaloSpawn; }
	public int getMaxBalas() { return maxBalas; }
	public float getProbabilidadItem() { return probabilidadItem; }
	public float getProbabilidadCura() { return probabilidadCura; }
	public float getIntervaloLaser() { return intervaloLaser; }
	public float getVelocidadLaser() { return velocidadLaser; }
	public int getMultiplicadorVidaJefe() { return multiplicadorVidaJefe; }
}
