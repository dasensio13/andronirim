package es.onirim.android;

import java.util.HashMap;
import java.util.Map;

import android.content.res.Resources;
import android.util.Log;
import es.onirim.core.Carta;
import es.onirim.core.Carta.Color;
import es.onirim.core.Carta.Simbolo;

public class StringCartaResolver {

	private static final String TAG = "StringCartaResolver";

	private static Map<Color, Integer> puertas;
	private static Map<Color, Integer> lunas;
	private static Map<Color, Integer> llaves;
	private static Map<Color, Integer> soles;
	private static Map<Simbolo, Map<Color, Integer>> cartas;
	private static Map<Simbolo, Integer> simbolos;

	private Resources resources = null;

	public StringCartaResolver(Resources resources) {
		this.resources = resources;
	}

	static {
		initPuertas();
		initCartas();
		initSimbolos();
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
		puertas.put(Color.ROJO, R.string.roja);
		puertas.put(Color.AZUL, R.string.azul);
		puertas.put(Color.VERDE, R.string.verde);
		puertas.put(Color.MARRON, R.string.marron);
	}

	private static void initSoles() {
		soles = new HashMap<Color, Integer>();
		soles.put(Color.ROJO, R.string.rojo);
		soles.put(Color.AZUL, R.string.azul);
		soles.put(Color.VERDE, R.string.verde);
		soles.put(Color.MARRON, R.string.marron);
	}

	private static void initLlaves() {
		llaves = new HashMap<Color, Integer>();
		llaves.put(Color.ROJO, R.string.roja);
		llaves.put(Color.AZUL, R.string.azul);
		llaves.put(Color.VERDE, R.string.verde);
		llaves.put(Color.MARRON, R.string.marron);
	}

	private static void initLunas() {
		lunas = new HashMap<Color, Integer>();
		lunas.put(Color.ROJO, R.string.roja);
		lunas.put(Color.AZUL, R.string.azul);
		lunas.put(Color.VERDE, R.string.verde);
		lunas.put(Color.MARRON, R.string.marron);
	}

	private static void initSimbolos() {
		simbolos = new HashMap<Simbolo, Integer>();
		simbolos.put(Simbolo.LUNA, R.string.luna);
		simbolos.put(Simbolo.LLAVE, R.string.llave);
		simbolos.put(Simbolo.SOL, R.string.sol);
	}

	public String getString(Carta carta) {
		Log.d(TAG, carta!=null?carta.toString():"Carta nula");
		String result = "";
		if (carta!=null) {
			switch (carta.getTipo()) {
			case PESADILLA:
				result = resources.getString(R.string.pesadilla);
				break;
			case PUERTA:
				result = getStringPuerta(carta.getColor());
				break;
			case LABERINTO:
				result = getStringLaberinto(carta.getColor(), carta.getSimbolo());
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

	private String getStringLaberinto(Color color, Simbolo simbolo) {
		StringBuilder str = new StringBuilder()
			.append(resources.getString(simbolos.get(simbolo)))
			.append(" ")
			.append(resources.getString(cartas.get(simbolo).get(color)));
		return str.toString();
	}

	private String getStringPuerta(Color color) {
		StringBuilder str = new StringBuilder()
			.append(resources.getString(R.string.puerta))
			.append(" ")
			.append(resources.getString(puertas.get(color)));
		return str.toString();
	}
}
