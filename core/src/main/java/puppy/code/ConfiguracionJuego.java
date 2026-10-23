package puppy.code;

/**
 * PATRON SINGLETON.
 * Problema: la dificultad elegida en el menu, el modo de movimiento de la nave y el
 * puntaje maximo deben ser compartidos por TODAS las pantallas (menu, juego, game over).
 * Solucion: una unica instancia global con acceso controlado via getInstancia().
 */
public final class ConfiguracionJuego {

	private static final ConfiguracionJuego INSTANCIA = new ConfiguracionJuego();

	private Dificultad dificultad = Dificultad.NORMAL;
	private int highScore = 0;
	private int modoNave = 0;

	private ConfiguracionJuego() {}

	public static ConfiguracionJuego getInstancia() {
		return INSTANCIA;
	}

	public Dificultad getDificultad() { return dificultad; }
	public void setDificultad(Dificultad dificultad) { this.dificultad = dificultad; }

	public int getHighScore() { return highScore; }
	public void setHighScore(int highScore) { this.highScore = highScore; }

	public int getModoNave() { return modoNave; }
	public void setModoNave(int modoNave) { this.modoNave = modoNave; }

	/** Actualiza el record si el puntaje entregado lo supera. */
	public void registrarPuntaje(int puntaje) {
		if (puntaje > highScore) highScore = puntaje;
	}
}
