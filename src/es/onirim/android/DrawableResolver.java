package es.onirim.android;

import java.util.HashMap;
import java.util.Map;

import android.util.Log;

import es.onirim.core.Carta;
import es.onirim.core.Carta.Color;
import es.onirim.core.Carta.Simbolo;

public class DrawableResolver {

	private static final String TAG = "DrawableResolver";

	private static Map<Color, Integer> puertas;
	private static Map<Color, Integer> lunas;
	private static Map<Color, Integer> llaves;
	private static Map<Color, Integer> soles;
	private static Map<Simbolo, Map<Color, Integer>> cartas;

	static {
		initPuertas();
		initCartas();
	}

	private static void initCartas() {
		initSoles();
		initLlaves();
		initLunas();

		cartas = new HashMap<Simbolo, Map<Color, Integer>>();
		cartas.put(Simbolo.SOL, soles);
		cartas.put(Simbolo.LUNA, lunas);
		cartas.put(Simbolo.LLAVE, llaves);
	}

	private static void initPuertas() {
		puertas = new HashMap<Color, Integer>();
		puertas.put(Color.ROJO, R.drawable.puerta_rojo);
		puertas.put(Color.AZUL, R.drawable.puerta_azul);
		puertas.put(Color.VERDE, R.drawable.puerta_verde);
		puertas.put(Color.MARRON, R.drawable.puerta_marron);
	}

	private static void initSoles() {
		soles = new HashMap<Color, Integer>();
		soles.put(Color.ROJO, R.drawable.sol_rojo);
		soles.put(Color.AZUL, R.drawable.sol_azul);
		soles.put(Color.VERDE, R.drawable.sol_verde);
		soles.put(Color.MARRON, R.drawable.sol_marron);
	}

	private static void initLlaves() {
		llaves = new HashMap<Color, Integer>();
		llaves.put(Color.ROJO, R.drawable.llave_rojo);
		llaves.put(Color.AZUL, R.drawable.llave_azul);
		llaves.put(Color.VERDE, R.drawable.llave_verde);
		llaves.put(Color.MARRON, R.drawable.llave_marron);
	}

	private static void initLunas() {
		lunas = new HashMap<Color, Integer>();
		lunas.put(Color.ROJO, R.drawable.luna_rojo);
		lunas.put(Color.AZUL, R.drawable.luna_azul);
		lunas.put(Color.VERDE, R.drawable.luna_verde);
		lunas.put(Color.MARRON, R.drawable.luna_marron);
	}

	public static int getDrawable(Carta carta) {
		Log.d(TAG, carta!=null?carta.toString():"Carta nula");
		int result = R.drawable.back;
		if (carta!=null) {
			switch (carta.getTipo()) {
			case PESADILLA:
				result = R.drawable.pesadilla;
				break;
			case PUERTA:
				result = getDrawablePuerta(carta.getColor());
				break;
			case LABERINTO:
				result = getDrawableLaberinto(carta.getColor(), carta.getSimbolo());
				break;
			default:
				Log.d(TAG, "Tipo de carta desconocido " + carta);
				break;
			}
		} else {
			Log.e(TAG, "Carta vacia");
		}
		return result;
	}

	private static int getDrawableLaberinto(Color color, Simbolo simbolo) {
		return cartas.get(simbolo).get(color);
	}

	private static int getDrawablePuerta(Color color) {
		return puertas.get(color);
	}
}
