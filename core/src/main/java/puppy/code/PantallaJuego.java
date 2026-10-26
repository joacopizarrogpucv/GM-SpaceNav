package puppy.code;

import java.util.ArrayList;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Align;

public class PantallaJuego extends PantallaBase {

	private static final float TAM_BASE_ASTEROIDE = 50f;
	private static final int DANO_BALA = 50;                 // dano de cada bala al jefe
	private static final float INTERVALO_ITEM_JEFE = 15f;    // el jefe "suelta" un item cada X segundos
	private static final float DURACION_MULTIPLICADOR = 30f; // el efecto x2 dura como maximo 30 s
	private static final String ARCHIVO_MUSICA_JEFE = "jefe-musica.ogg";  // cambiar por tu propio archivo si quieres

	private final ConfiguracionJuego config = ConfiguracionJuego.getInstancia();
	private final Dificultad dificultad = config.getDificultad();
	private final SpriteBatch batch;

	// recursos
	private final Sound explosionSound;
	private final Sound soundBala;
	private final Sound hurtSound;
	private final Music gameMusic;
	private Music bossMusic = null;   // opcional: si el archivo no existe, el jefe no tiene musica propia
	private final Texture txAsteroide;
	private final Texture txNave;
	private final Texture txBala;
	private final ShapeRenderer shape;   // compartido, lo crea y libera SpaceNavigation

	// estado de la partida
	private int score = 0;
	private float tiempo = 0f;
	private float acumuladorSpawn = 0f;
	private float factorVelocidad = 1f;
	private boolean pausado = false;
	private String mensaje = "";
	private String mensaje2 = "";    // segunda linea opcional bajo el mensaje principal
	private float mensajeTimer = 0f;

	private final Nave4 nave;
	private final Municion municion;
	private final ArrayList<Ball2> asteroides = new ArrayList<>();
	private final ArrayList<Bullet> balas = new ArrayList<>();
	private final ArrayList<ItemRecarga> items = new ArrayList<>();
	private final ArrayList<ItemMultiplicador> multiplicadores = new ArrayList<>();
	private float multiplicadorRestante = 0f;   // > 0 mientras el puntaje vale x2
	private final ArrayList<LaserJefe> lasers = new ArrayList<>();
	private final ArrayList<Lava> lavas = new ArrayList<>();   // lava del planeta errante
	private int jefesDerrotados = 0;
	private int asteroidesDestruidos = 0;

	// jefe y cometa (nunca coexisten)
	private JefeEstrella jefe = null;
	private int jefesAparecidos = 0;
	private float proximoJefe;
	private float acumuladorItemJefe = 0f;
	private Cometa cometa = null;
	private float proximoCometa;

	// Minijefe: planeta movil con campo gravitatorio (no coexiste con jefe ni cometa).
	private PlanetaErrante planetaErrante = null;
	private MovimientoGravitatorio movGravedadPlaneta = null;
	private float proximoPlaneta;

	// estrategias
	private final EstrategiaMovimiento movRecto = new MovimientoRecto();
	private MovimientoGravitatorio movGravedad = null;   // existe solo mientras hay jefe
	private final EstrategiaMovimientoNave[] modosNave = {
			new MovimientoInercia(), new MovimientoDirecto(), new MovimientoRotacional() };
	private int modoActual;

	public PantallaJuego(SpaceNavigation game) {
		super(game);
		batch = game.getBatch();
		shape = game.getShape();

		explosionSound = Gdx.audio.newSound(Gdx.files.internal("explosion.ogg"));
		explosionSound.setVolume(1, 0.5f);
		soundBala = Gdx.audio.newSound(Gdx.files.internal("pop-sound.mp3"));
		hurtSound = Gdx.audio.newSound(Gdx.files.internal("hurt.ogg"));
		gameMusic = Gdx.audio.newMusic(Gdx.files.internal("piano-loops.ogg"));
		gameMusic.setLooping(true);
		gameMusic.setVolume(0.5f);
		if (Gdx.files.internal(ARCHIVO_MUSICA_JEFE).exists()) {
			bossMusic = Gdx.audio.newMusic(Gdx.files.internal(ARCHIVO_MUSICA_JEFE));
			bossMusic.setLooping(true);
			bossMusic.setVolume(0.6f);
		}

		txAsteroide = new Texture(Gdx.files.internal("aGreyMedium4.png"));
		txNave = new Texture(Gdx.files.internal("MainShip3.png"));
		txBala = new Texture(Gdx.files.internal("Rocket2.png"));

		modoActual = config.getModoNave() % modosNave.length;
		nave = new Nave4(Constantes.ANCHO / 2f - 22, 30, txNave, hurtSound, modosNave[modoActual]);
		municion = new Municion(dificultad.getMaxBalas());

		proximoJefe = MathUtils.random(120f, 180f); // Primer jefe entre 2 y 3 min
		proximoCometa = MathUtils.random(15f, 30f); // Primer cometa entre 15 y 30 s
		proximoPlaneta = MathUtils.random(35f, 55f); // Primer planeta entre 35 y 55 s

		for (int i = 0; i < dificultad.getMaxAsteroides() / 2; i++) {
			asteroides.add(crearAsteroide());
		}
	}

	/** Usa el Builder para crear un asteroide acorde a la dificultad. */
	private Ball2 crearAsteroide() {
		boolean enJefe = (jefe != null);
		float escala = dificultad.escalaAleatoria();
		float tam = TAM_BASE_ASTEROIDE * escala;
		float vel = dificultad.getVelocidadBase() * MathUtils.random(0.9f, 1.1f);
		TipoAsteroide tipo = dificultad.tipoAleatorio(escala, nave.getVidas() < Nave4.MAX_VIDAS);

		// normal: arriba / izquierda / derecha. Con jefe: solo por los costados, abajo del jefe
		float x, y;
		int borde = enJefe ? 1 + MathUtils.random(1) : MathUtils.random(2);
		if (borde == 0) {
			x = MathUtils.random(0f, Constantes.ANCHO - tam);
			y = Constantes.ALTO - tam;
		} else {
			x = (borde == 1) ? 0 : Constantes.ANCHO - tam;
			y = enJefe ? MathUtils.random(Constantes.ALTO * 0.1f, Constantes.ALTO * 0.45f)
					: MathUtils.random(Constantes.ALTO * 0.4f, Constantes.ALTO - tam);
		}
		Vector2 dir = new Vector2(MathUtils.random(Constantes.ANCHO * 0.2f, Constantes.ANCHO * 0.8f) - x,
				MathUtils.random(0f, Constantes.ALTO * 0.6f) - y).nor();

		return new AsteroideBuilder()
				.posicion(x, y)
				.tamano(tam)
				.velocidad(dir.x * vel, dir.y * vel)
				.textura(txAsteroide)
				.estrategia(movRecto)
				.tipo(tipo)
				.build();
	}

	// ------------------------------------------------------------------ entrada
	@Override
	protected void procesarEntrada() {
		if (Gdx.input.isKeyJustPressed(Input.Keys.P) || Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) {
			pausado = !pausado;
			if (pausado) musicaActiva().pause(); else musicaActiva().play();
		}
		if (pausado) {
			if (Gdx.input.isKeyJustPressed(Input.Keys.M)) cambiarA(new PantallaMenu(game));
			return;
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.TAB)) {
			modoActual = (modoActual + 1) % modosNave.length;
			config.setModoNave(modoActual);
			nave.setEstrategia(modosNave[modoActual]);
		}
		if (Gdx.input.isKeyJustPressed(Input.Keys.SPACE) && municion.disparar()) {
			balas.add(nave.crearBala(txBala));
			soundBala.play();
		}
		if (Constantes.TECLAS_DEBUG && jefe == null && cometa == null) {
			if (Gdx.input.isKeyJustPressed(Input.Keys.B)) iniciarJefe();
			if (Gdx.input.isKeyJustPressed(Input.Keys.V) && planetaErrante == null) cometa = new Cometa(true);   // cometa de prueba con multiplicador
			if (Gdx.input.isKeyJustPressed(Input.Keys.C) && planetaErrante == null) iniciarPlanetaErrante(); // planeta errante de prueba
		}
	}

	// ------------------------------------------------------------------ logica
	@Override
	protected void actualizar(float delta) {
		if (pausado) return;
		delta = Math.min(delta, 0.05f); // evita saltos si hay un tiron de FPS
		game.getFondo().update(delta);

		tiempo += delta;
		if (mensajeTimer > 0) mensajeTimer -= delta;
		if (multiplicadorRestante > 0) multiplicadorRestante = Math.max(0f, multiplicadorRestante - delta);
		factorVelocidad = dificultad.factorVelocidad(tiempo);
		municion.actualizar(delta);

		// el puntaje solo sube al destruir asteroides (+10) o derrotar al jefe, no por tiempo
		actualizarSpawnAsteroides(delta);
		actualizarPlanetaErrante(delta);
		actualizarLavas(delta);
		actualizarBalas(delta);
		actualizarAsteroides(delta);
		actualizarItems(delta);
		actualizarMultiplicadores(delta);
		actualizarJefe(delta);
		actualizarCometa(delta);

		nave.actualizar(delta);
		if (planetaErrante != null) {
			nave.checkCollision(planetaErrante.getX(), planetaErrante.getY(), planetaErrante.getRadio());
		}
		for (int i = asteroides.size() - 1; i >= 0; i--) {
			if (nave.checkCollision(asteroides.get(i))) asteroides.remove(i);
		}
		for (int i = lasers.size() - 1; i >= 0; i--) {
			LaserJefe l = lasers.get(i);
			if (nave.checkCollision(l.getX(), l.getY(), 4f)) lasers.remove(i);
		}
		for (int i = lavas.size() - 1; i >= 0; i--) {
			Lava lv = lavas.get(i);
			if (nave.checkCollision(lv.getX(), lv.getY(), lv.getRadio())) lavas.remove(i);
		}

		if (nave.estaDestruido()) {
			boolean esRecord = score > config.getHighScore();
			config.registrarPuntaje(score);
			cambiarA(new PantallaGameOver(game, score, esRecord, tiempo, jefesDerrotados, asteroidesDestruidos));
		}
	}

	private void actualizarSpawnAsteroides(float delta) {
		float intervalo = dificultad.getIntervaloSpawn();
		int tope = dificultad.maxAsteroidesEn(tiempo);
		if (jefe != null) {            // durante el jefe aparecen muy pocos
			intervalo *= 2f;
			tope = dificultad.maxAsteroidesJefe(jefe.getNivel());   // sube con el nivel del jefe
		}
		acumuladorSpawn += delta;
		if (acumuladorSpawn >= intervalo) {
			acumuladorSpawn = 0;
			if (asteroides.size() < tope) asteroides.add(crearAsteroide());
		}
	}

	private void actualizarBalas(float delta) {
		for (int i = balas.size() - 1; i >= 0; i--) {
			Bullet b = balas.get(i);
			b.update(delta);
			if (b.isDestroyed()) { balas.remove(i); continue; }
			// El planeta desvia las balas en lugar de destruirlas.
			if (planetaErrante != null && b.rebotarEnPlaneta(
					planetaErrante.getX(), planetaErrante.getY(), planetaErrante.getRadio() + 12f)) continue;

			if (jefe != null && jefe.esVulnerable()
					&& b.checkCollision(jefe.getX(), jefe.getY(), jefe.getRadioImpacto())) {
				if (jefe.recibirDano(DANO_BALA)) derrotarJefe();
			} else {
				for (int j = asteroides.size() - 1; j >= 0; j--) {
					if (b.checkCollision(asteroides.get(j))) {
						destruirAsteroide(j, true);
						sumarPuntos(10);
						break;
					}
				}
			}
			if (b.isDestroyed()) balas.remove(i);
		}
	}

	private void actualizarAsteroides(float delta) {
		for (Ball2 a : asteroides) {
			a.setFactorVelocidad(factorVelocidad);
			// Strategy: la misma estrategia gravitatoria funciona con jefe o planeta errante.
			if (jefe != null && jefe.contieneEnZona(a)) a.setEstrategia(movGravedad);
			else if (planetaErrante != null && planetaErrante.contieneEnZona(a))
				a.setEstrategia(movGravedadPlaneta);
			else a.setEstrategia(movRecto);
			a.update(delta);
		}
		// los asteroides dorados y verdes desaparecen si no se cazan a tiempo
		for (int i = asteroides.size() - 1; i >= 0; i--) {
			if (asteroides.get(i).isExpirado()) asteroides.remove(i);
		}
		for (int i = 0; i < asteroides.size(); i++) {
			for (int j = i + 1; j < asteroides.size(); j++) {
				asteroides.get(i).chocar(asteroides.get(j));
			}
		}
	}

	private void actualizarItems(float delta) {
		for (int i = items.size() - 1; i >= 0; i--) {
			ItemRecarga it = items.get(i);
			it.update(delta);
			if (it.isExpirado()) {
				items.remove(i);
			} else if (it.colisionaCon(nave.getCentroX(), nave.getCentroY(), 22f)) {
				municion.recargarTodo();
				items.remove(i);
				soundBala.play();
				mostrarMensaje("MUNICION RECARGADA!", 1.5f);
			}
		}
	}

	private void actualizarMultiplicadores(float delta) {
		for (int i = multiplicadores.size() - 1; i >= 0; i--) {
			ItemMultiplicador m = multiplicadores.get(i);
			m.update(delta);
			if (m.isExpirado()) {
				multiplicadores.remove(i);
			} else if (m.colisionaCon(nave.getCentroX(), nave.getCentroY(), 22f)) {
				multiplicadores.remove(i);
				multiplicadorRestante = DURACION_MULTIPLICADOR;   // recoger otro reinicia a 30 s, no se acumula
				mostrarMensaje("PUNTAJE x2 POR " + (int) DURACION_MULTIPLICADOR + " SEGUNDOS!", 2.5f);
			}
		}
	}

	/** Suma puntos aplicando el multiplicador x2 si esta activo. */
	private void sumarPuntos(int base) {
		score += (multiplicadorRestante > 0) ? base * 2 : base;
	}

	private void actualizarJefe(float delta) {
		if (jefe == null) {
			if (cometa == null && tiempo >= proximoJefe) iniciarJefe();
			return;
		}
		lasers.addAll(jefe.actualizar(delta, nave.getCentroX(), nave.getCentroY()));
		for (int i = lasers.size() - 1; i >= 0; i--) {
			LaserJefe l = lasers.get(i);
			l.update(delta);
			if (l.isDestruido()) lasers.remove(i);
		}
		if (jefe.esVulnerable()) {
			acumuladorItemJefe += delta;
			if (acumuladorItemJefe >= INTERVALO_ITEM_JEFE) {
				acumuladorItemJefe = 0;
				items.add(new ItemRecarga(MathUtils.random(100f, Constantes.ANCHO - 100f),
						MathUtils.random(200f, 450f), txBala));
			}
		}
	}

	private void actualizarCometa(float delta) {
		if (cometa == null) {
			if (jefe == null && planetaErrante == null && tiempo >= proximoCometa && proximoJefe - tiempo > 8f) cometa = new Cometa();
			return;
		}
		cometa.update(delta);
		if (cometa.estaActivo()) {
			for (int j = asteroides.size() - 1; j >= 0; j--) {
				Ball2 a = asteroides.get(j);
				if (cometa.choca(a.getCentroX(), a.getCentroY(), a.getRadio())) destruirAsteroide(j, false);
			}
			if (cometa.choca(nave.getCentroX(), nave.getCentroY(), 18f)) nave.matar();
		}
		if (cometa.haTerminado()) {
			if (cometa.portaMultiplicador()) {   // lo suelta al terminar de cruzar
				multiplicadores.add(new ItemMultiplicador(MathUtils.random(150f, Constantes.ANCHO - 150f),
						MathUtils.random(150f, 450f)));
				mostrarMensaje("EL COMETA SOLTO UN x2!", 2.5f);
			}
			cometa = null;
			proximoCometa = tiempo + MathUtils.random(45f, 75f);
		}
	}

	/** Planeta itinerante: cruza de lado a lado e interfiere con los asteroides y la nave. */
	private void iniciarPlanetaErrante() {
		planetaErrante = new PlanetaErrante(MathUtils.randomBoolean(),
				MathUtils.random(190f, Constantes.ALTO - 190f));
		movGravedadPlaneta = new MovimientoGravitatorio(planetaErrante);
		mostrarMensaje("PLANETA ERRANTE! CUIDADO CON LA LAVA", 2f);
	}

	private void actualizarPlanetaErrante(float delta) {
		if (planetaErrante == null) {
			if (jefe == null && cometa == null && tiempo >= proximoPlaneta
					&& proximoJefe - tiempo > 12f) iniciarPlanetaErrante();
			return;
		}
		lavas.addAll(planetaErrante.actualizar(delta, nave.getCentroX(), nave.getCentroY()));
		if (planetaErrante.haSalido()) {
			planetaErrante = null;
			movGravedadPlaneta = null;
			proximoPlaneta = tiempo + MathUtils.random(40f, 65f);
		}
	}

	/** La lava destruye asteroides al tocarlos (y se consume); a la nave la dana en actualizar(). */
	private void actualizarLavas(float delta) {
		for (int i = lavas.size() - 1; i >= 0; i--) {
			Lava l = lavas.get(i);
			l.update(delta);
			if (l.isExpirada()) { lavas.remove(i); continue; }
			for (int j = asteroides.size() - 1; j >= 0; j--) {
				Ball2 a = asteroides.get(j);
				if (l.choca(a.getCentroX(), a.getCentroY(), a.getRadio())) {
					destruirAsteroide(j, false);
					lavas.remove(i);
					break;
				}
			}
		}
	}

	private void iniciarJefe() {
		// Un minijefe termina su visita si llega el jefe principal.
		lavas.clear();
		planetaErrante = null;
		movGravedadPlaneta = null;
		proximoPlaneta = tiempo + MathUtils.random(40f, 65f);
		jefesAparecidos++;
		jefe = new JefeEstrella(jefesAparecidos, dificultad);
		movGravedad = new MovimientoGravitatorio(jefe);
		asteroides.clear();              // desaparecen los asteroides
		lasers.clear();
		proximoJefe = Float.MAX_VALUE;
		acumuladorItemJefe = 0f;
		acumuladorSpawn = 0f;
		cambiarMusica(bossMusic);
		mostrarMensaje(jefesAparecidos > 1 ? "DERROTA AL JEFE  -  NIVEL " + jefesAparecidos : "DERROTA AL JEFE",
				"QUE LA FUERZA TE ACOMPANE", 4f);
	}

	private void derrotarJefe() {
		jefesDerrotados++;
		sumarPuntos(jefe.getPuntaje());
		explosionSound.play();
		items.add(new ItemRecarga(jefe.getX(), jefe.getY() - 60f, txBala));
		mostrarMensaje("JEFE DERROTADO!  +" + jefe.getPuntaje() + " puntos", 3.5f);
		lasers.clear();
		jefe = null;
		movGravedad = null;
		proximoJefe = tiempo + MathUtils.random(120f, 180f);
		cambiarMusica(gameMusic);
		proximoCometa = Math.max(proximoCometa, tiempo + MathUtils.random(20f, 35f));
	}

	/**
	 * Elimina un asteroide. El dorado suelta un item de recarga. El verde cura 1 vida,
	 * pero solo si lo destruye una bala del jugador (no el cometa).
	 */
	private void destruirAsteroide(int indice, boolean porJugador) {
		Ball2 a = asteroides.remove(indice);
		explosionSound.play();
		if (porJugador) asteroidesDestruidos++;
		if (a.getTipo() == TipoAsteroide.DORADO) {
			items.add(new ItemRecarga(a.getCentroX(), a.getCentroY(), txBala));
		} else if (a.getTipo() == TipoAsteroide.VERDE && porJugador) {
			if (nave.curar()) mostrarMensaje("+1 VIDA!", 1.5f);
		}
	}

	/** Musica que corresponde al momento actual: la del jefe mientras hay jefe (si existe). */
	private Music musicaActiva() {
		return (jefe != null && bossMusic != null) ? bossMusic : gameMusic;
	}

	/** Detiene la musica actual y arranca la indicada (si el juego no esta en pausa). */
	private void cambiarMusica(Music nueva) {
		if (nueva == null) return;                 // sin musica de jefe: sigue la normal
		Music otra = (nueva == gameMusic) ? bossMusic : gameMusic;
		if (otra != null) otra.pause();
		if (nueva == bossMusic) bossMusic.stop();   // la del jefe siempre empieza desde el principio
		if (!pausado) nueva.play();
	}

	private void mostrarMensaje(String texto, float segundos) {
		mostrarMensaje(texto, "", segundos);
	}

	private void mostrarMensaje(String texto, String texto2, float segundos) {
		mensaje = texto;
		mensaje2 = texto2;
		mensajeTimer = segundos;
	}

	// ------------------------------------------------------------------ dibujo
	private void activarBlend() {
		prepararShape();
	}

	@Override
	protected void dibujar() {
		// 1) fondo y cuerpos celestes
		activarBlend();
		game.getFondo().dibujar(shape);
		if (jefe != null) jefe.dibujar(shape);
		if (planetaErrante != null) planetaErrante.dibujar(shape);
		shape.begin(ShapeType.Filled);
		if (cometa != null && cometa.enAviso()) cometa.dibujarAviso(shape);
		for (ItemRecarga it : items) it.dibujarFondo(shape);
		for (ItemMultiplicador m : multiplicadores) m.dibujarFondo(shape);
		shape.end();

		// 2) sprites del juego
		batch.begin();
		for (ItemRecarga it : items) it.dibujar(batch);
		for (ItemMultiplicador m : multiplicadores) m.dibujarTexto(batch, game.getFont());
		for (Bullet b : balas) b.draw(batch);
		for (Ball2 a : asteroides) a.draw(batch);
		nave.dibujar(batch);
		batch.end();

		// 3) efectos frontales y formas del HUD
		activarBlend();
		shape.begin(ShapeType.Filled);
		for (LaserJefe l : lasers) l.dibujar(shape);
		for (Lava l : lavas) l.dibujar(shape);
		if (cometa != null && cometa.estaActivo()) cometa.dibujar(shape);
		if (pausado) {
			shape.setColor(0f, 0f, 0f, 0.5f);
			shape.rect(0, 0, Constantes.ANCHO, Constantes.ALTO);
		}
		dibujaFormasHud();
		shape.end();

		// 4) textos
		batch.begin();
		dibujaTextosHud();
		dibujaMensajes();
		batch.end();
	}

	private void corazon(float cx, float cy, float s, float r, float g, float b, float a) {
		shape.setColor(r, g, b, a);
		shape.circle(cx - s * 0.5f, cy + s * 0.25f, s * 0.52f, 16);
		shape.circle(cx + s * 0.5f, cy + s * 0.25f, s * 0.52f, 16);
		shape.triangle(cx - s * 0.98f, cy + s * 0.12f, cx + s * 0.98f, cy + s * 0.12f, cx, cy - s * 0.98f);
		if (a > 0.5f) {
			shape.setColor(1f, 1f, 1f, 0.35f);
			shape.circle(cx - s * 0.55f, cy + s * 0.4f, s * 0.15f, 8);
		}
	}

	private void dibujaFormasHud() {
		// vidas como corazones
		for (int i = 0; i < Nave4.MAX_VIDAS; i++) {
			float cx = 40f + i * 38f;
			float cy = Constantes.ALTO - 32f;
			if (i < nave.getVidas()) corazon(cx, cy, 13f, 0.95f, 0.15f, 0.25f, 1f);
			else corazon(cx, cy, 13f, 1f, 1f, 1f, 0.12f);
		}

		// municion como cartuchos y barra de recarga
		float bx = 24f, by = 22f;
		for (int i = 0; i < municion.getMaximo(); i++) {
			boolean lista = i < municion.getActual();
			if (lista) shape.setColor(0.2f, 0.9f, 1f, 1f); else shape.setColor(1f, 1f, 1f, 0.14f);
			shape.rect(bx + i * 20f, by, 12f, 28f);
			if (lista) {
				shape.setColor(1f, 1f, 1f, 0.45f);
				shape.rect(bx + i * 20f + 2f, by + 18f, 3f, 8f);
			}
		}
		if (municion.getActual() < municion.getMaximo()) {
			float p = 1f - municion.getTiempoSiguiente() / Municion.TIEMPO_RECARGA;
			float w = municion.getMaximo() * 20f - 8f;
			shape.setColor(1f, 1f, 1f, 0.12f);
			shape.rect(bx, by - 10f, w, 4f);
			shape.setColor(0.2f, 0.9f, 1f, 0.9f);
			shape.rect(bx, by - 10f, w * p, 4f);
		}

		// barra del multiplicador x2
		if (multiplicadorRestante > 0) {
			float x = Constantes.ANCHO - 224f, y = Constantes.ALTO - 92f;
			shape.setColor(1f, 1f, 1f, 0.12f);
			shape.rect(x, y, 200f, 8f);
			shape.setColor(1f, 0.75f, 0.1f, 1f);
			shape.rect(x, y, 200f * multiplicadorRestante / DURACION_MULTIPLICADOR, 8f);
		}

		// barra de vida del jefe
		if (jefe != null) {
			float ancho = 520f;
			float x = Constantes.ANCHO / 2f - ancho / 2f;
			float y = Constantes.ALTO - 112f;
			float p = jefe.getVida() / (float) jefe.getVidaMax();
			shape.setColor(0f, 0f, 0f, 0.6f);
			shape.rect(x - 4f, y - 4f, ancho + 8f, 26f);
			shape.setColor(0.35f, 0.05f, 0.05f, 1f);
			shape.rect(x, y, ancho, 18f);
			float pulso = (p < 0.25f) ? 0.75f + 0.25f * MathUtils.sin(tiempo * 10f) : 1f;
			shape.setColor(0.9f * pulso, 0.12f, 0.12f, 1f);
			shape.rect(x, y, ancho * p, 18f);
			shape.setColor(1f, 0.5f, 0.5f, 0.35f);
			shape.rect(x, y + 10f, ancho * p, 6f);
			shape.setColor(0f, 0f, 0f, 0.5f);
			for (int q = 1; q <= 3; q++) shape.rect(x + ancho * q / 4f - 1f, y, 2f, 18f);
		}

		// bandas detras de los mensajes grandes
		if (mensajeTimer > 0) {
			shape.setColor(0f, 0f, 0f, Math.min(1f, mensajeTimer) * 0.35f);
			if (mensaje2.isEmpty()) shape.rect(0, 362f, Constantes.ANCHO, 52f);
			else shape.rect(0, 312f, Constantes.ANCHO, 110f);
		}
		if (cometa != null && cometa.enAviso()) {
			shape.setColor(0.3f, 0.05f, 0f, 0.4f);
			shape.rect(0, 304f, Constantes.ANCHO, 44f);
		}
	}

	private void dibujaTextosHud() {
		BitmapFont f = game.getFont();

		// puntaje (arriba a la derecha)
		f.getData().setScale(0.9f);
		f.setColor(0.7f, 0.8f, 1f, 1f);
		Texto.sombra(batch, f, "PUNTAJE", 0, Constantes.ALTO - 12f, Constantes.ANCHO - 24f, Align.right);
		f.getData().setScale(2.2f);
		f.setColor(Color.WHITE);
		Texto.sombra(batch, f, String.valueOf(score), 0, Constantes.ALTO - 28f, Constantes.ANCHO - 24f, Align.right);
		if (multiplicadorRestante > 0) {
			f.getData().setScale(1.1f);
			f.setColor(1f, 0.75f, 0.1f, 1f);
			Texto.sombra(batch, f, "x2   " + (int) Math.ceil(multiplicadorRestante) + "s",
					0, Constantes.ALTO - 66f, Constantes.ANCHO - 24f, Align.right);
		}

		// record (centro)
		f.getData().setScale(1.1f);
		f.setColor(1f, 0.85f, 0.3f, 1f);
		Texto.sombra(batch, f, "RECORD  " + config.getHighScore(), 0, Constantes.ALTO - 16f, Constantes.ANCHO, Align.center);

		// informacion de la partida (bajo los corazones)
		f.getData().setScale(1f);
		f.setColor(Color.LIGHT_GRAY);
		Texto.sombra(batch, f, dificultad.getNombre() + "  |  " + modosNave[modoActual].getNombre()
				+ " (TAB)  |  Vel x" + String.format("%.2f", factorVelocidad), 24f, Constantes.ALTO - 62f, 0, Align.left);

		// municion
		f.setColor(Color.CYAN);
		String balasTxt = "BALAS  " + municion.getActual() + "/" + municion.getMaximo();
		if (municion.getActual() < municion.getMaximo()) {
			balasTxt += "    +1 en " + String.format("%.1f", municion.getTiempoSiguiente()) + "s";
		}
		Texto.sombra(batch, f, balasTxt, 24f, 80f, 0, Align.left);

		if (planetaErrante != null) {
			f.getData().setScale(1.2f);
			f.setColor(1f, 0.6f, 0.2f, 1f);
			Texto.sombra(batch, f, "PLANETA ERRANTE: CUIDADO CON LA LAVA", 0, Constantes.ALTO - 90f, Constantes.ANCHO, Align.center);
		}

		if (jefe != null) {
			f.getData().setScale(1.1f);
			f.setColor(Color.WHITE);
			Texto.sombra(batch, f, "ESTRELLA DE LA MUERTE   Nv." + jefe.getNivel(),
					0, Constantes.ALTO - 72f, Constantes.ANCHO, Align.center);
			f.getData().setScale(0.95f);
			Texto.sombra(batch, f, jefe.getVida() + " / " + jefe.getVidaMax(),
					0, Constantes.ALTO - 96f, Constantes.ANCHO, Align.center);
		}
		f.setColor(Color.WHITE);
	}

	private void dibujaMensajes() {
		BitmapFont f = game.getFont();
		if (mensajeTimer > 0) {
			float a = Math.min(1f, mensajeTimer);
			f.getData().setScale(1.8f);
			f.setColor(1f, 0.9f, 0.2f, a);
			Texto.sombra(batch, f, mensaje, 0, 400f, Constantes.ANCHO, Align.center);
			if (!mensaje2.isEmpty()) {
				f.getData().setScale(1.4f);
				f.setColor(0.6f, 0.9f, 1f, a);
				Texto.sombra(batch, f, mensaje2, 0, 340f, Constantes.ANCHO, Align.center);
			}
		}
		if (cometa != null && cometa.enAviso()) {
			f.getData().setScale(1.8f);
			f.setColor(cometa.getR(), cometa.getG(), cometa.getB(), 1f);
			Texto.sombra(batch, f, "AHI VIENE EL COMETA!", 0, 340f, Constantes.ANCHO, Align.center);
		}
		if (pausado) {
			f.getData().setScale(3f);
			f.setColor(Color.YELLOW);
			Texto.sombra(batch, f, "PAUSA", 0, 460f, Constantes.ANCHO, Align.center);
			f.getData().setScale(1.6f);
			f.setColor(Color.WHITE);
			Texto.sombra(batch, f, "P / ESC: continuar   |   M: menu principal", 0, 380f, Constantes.ANCHO, Align.center);
		}
		f.setColor(Color.WHITE);
	}

	@Override
	public void show() {
		if (!pausado) musicaActiva().play();
	}

	@Override
	public void dispose() {
		explosionSound.dispose();
		soundBala.dispose();
		hurtSound.dispose();
		gameMusic.dispose();
		if (bossMusic != null) bossMusic.dispose();
		txAsteroide.dispose();
		txNave.dispose();
		txBala.dispose();
	}
}
