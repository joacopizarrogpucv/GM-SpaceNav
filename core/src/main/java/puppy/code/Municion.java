package puppy.code;

import java.util.ArrayList;

/**
 * Municion limitada de la nave. Cada bala disparada se recarga de forma independiente
 * tras TIEMPO_RECARGA segundos. Un item puede recargarlas todas de golpe.
 */
public class Municion {

	public static final float TIEMPO_RECARGA = 5f;

	private final int maximo;
	private int actual;
	private final ArrayList<Float> recargas = new ArrayList<>(); // tiempo restante de cada bala gastada

	public Municion(int maximo) {
		this.maximo = maximo;
		this.actual = maximo;
	}

	/** Intenta gastar una bala. Devuelve false si no queda municion. */
	public boolean disparar() {
		if (actual <= 0) return false;
		actual--;
		recargas.add(TIEMPO_RECARGA);
		return true;
	}

	public void actualizar(float delta) {
		for (int i = recargas.size() - 1; i >= 0; i--) {
			float t = recargas.get(i) - delta;
			if (t <= 0) {
				recargas.remove(i);
				actual++;
			} else {
				recargas.set(i, t);
			}
		}
	}

	public void recargarTodo() {
		actual = maximo;
		recargas.clear();
	}

	/** Segundos que faltan para recuperar la proxima bala (0 si esta llena). */
	public float getTiempoSiguiente() {
		float min = 0f;
		for (float t : recargas) {
			if (min == 0f || t < min) min = t;
		}
		return min;
	}

	public int getActual() { return actual; }
	public int getMaximo() { return maximo; }
}
