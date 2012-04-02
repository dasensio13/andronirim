package es.onirim.android;

import java.util.List;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;
import es.onirim.core.Carta;
import es.onirim.core.Carta.Color;
import es.onirim.core.Solitario;

public class GameActivity extends OptionsMenuActivity {

	private Solitario solitario = null;
	private StringCartaResolver stringCartaResolver = null;

	private static final String DRAWABLE_CARTA = "DRAWABLE_CARTA";
	private static final String INDEX_CARTA = "INDEX_CARTA";

	private static final int DIALOG_JUGAR = 0;
	private static final int DIALOG_VICTORIA = 1;
	private static final int DIALOG_DERROTA = 2;

	private static final int MAX_CARTAS_FILA = 40;

	private static final String TAG = "GameActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);

		stringCartaResolver = new StringCartaResolver(getResources());

		Solitario lastSolitario = (Solitario) getLastNonConfigurationInstance();
		if (lastSolitario==null) {
			solitario = new Solitario();
		} else {
			solitario = lastSolitario;
			pintarPuertasObtenidas();
			pintarLaberintoCompleto();
			pintarDescartesCompleto();
		}
		pintarMano(solitario.getCartasMano());
		setNumCartas();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		//TODO: mantener el log
	    return solitario;
	}

	@Override
	public Dialog onCreateDialog(int id, Bundle bundle) {
		Dialog dialog = null;
		switch (id) {
		case DIALOG_JUGAR:
			dialog = buildDialogoJugar(bundle);
			break;
		case DIALOG_VICTORIA:
			dialog = buildDialogoVictoria();
			break;
		case DIALOG_DERROTA:
			dialog = buildDialogoDerrota();
			break;
		default:
			dialog = super.onCreateDialog(id);
		}
		return dialog;
	}

	private Dialog buildDialogoJugar(final Bundle bundle) {
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.jugar, null);

		ImageView image = (ImageView) layout.findViewById(R.id.dialog_jugar_carta);
		image.setImageResource(bundle.getInt(DRAWABLE_CARTA));

		AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
		builder.setView(layout)
			.setTitle(R.string.jugarDescartar)
			.setCancelable(true)
			.setPositiveButton(R.string.jugar,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								turnoJugar(bundle.getInt(INDEX_CARTA));
								removeDialog(DIALOG_JUGAR);
							}
						})
				.setNegativeButton(R.string.descartar,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								turnoDescartar(bundle.getInt(INDEX_CARTA));
								removeDialog(DIALOG_JUGAR);
							}
						});
		return builder.create();
	}

	private Dialog buildDialogoVictoria() {
		//TODO: mostrar ultima puerta conseguida en el dialogo
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.victoria)
				.setCancelable(false)
				.setPositiveButton(R.string.aceptar,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						});
		return builder.create();
	}

	private Dialog buildDialogoDerrota() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.derrota)
				.setCancelable(false)
				.setPositiveButton(R.string.aceptar,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						});
		return builder.create();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.carta, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		// AdapterContextMenuInfo info = (AdapterContextMenuInfo)
		// item.getMenuInfo();
		switch (item.getItemId()) {
		case R.id.jugar:
			showToast(R.string.jugar);
			return true;
		case R.id.descartar:
			showToast(R.string.descartar);
			return true;
		default:
			return super.onContextItemSelected(item);
		}
	}

	private void showToast(int resourceId) {
		showToast(getResources().getText(resourceId).toString());
	}

	private void showToast(String texto) {
		Toast toast = Toast.makeText(this, texto, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	private void showToastPuerta(Carta puerta) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.puerta, null);

		ImageView image = (ImageView) layout.findViewById(R.id.toast_puerta);
		image.setImageResource(DrawableResolver.getDrawable(puerta));

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	private void pintarMano(List<Carta> cartas) {
		ImageView vCarta = (ImageView) findViewById(R.id.carta1);
		pintarCartaMano(cartas.get(0), vCarta);

		vCarta = (ImageView) findViewById(R.id.carta2);
		pintarCartaMano(cartas.get(1), vCarta);

		vCarta = (ImageView) findViewById(R.id.carta3);
		pintarCartaMano(cartas.get(2), vCarta);

		vCarta = (ImageView) findViewById(R.id.carta4);
		pintarCartaMano(cartas.get(3), vCarta);

		vCarta = (ImageView) findViewById(R.id.carta5);
		pintarCartaMano(cartas.get(4), vCarta);
	}

	private void pintarCartaMano(Carta carta, ImageView vCarta) {
		vCarta.setImageResource(DrawableResolver.getDrawable(carta));
		vCarta.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				turnoJugar(getIndexCarta(v.getId()));
			}
		});

		vCarta.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				Bundle bundle = new Bundle();
				int index = getIndexCarta(v.getId());
				Carta carta = solitario.getCartaMano(index);
				bundle.putInt(DRAWABLE_CARTA, DrawableResolver.getDrawable(carta));
				bundle.putInt(INDEX_CARTA, index);
				showDialog(DIALOG_JUGAR, bundle);
				return true;
			}
		});
	}

	private void turnoJugar(int index) {
		Carta carta = solitario.getCartaMano(index);
		addLog(getText(R.string.jugada) + " " + stringCartaResolver.getString(carta));
		if (solitario.puedoInsertarCartaLaberinto(carta)) {
			solitario.cogerCartaMano(index);
			solitario.insertarCartaLaberinto(carta);
			pintarUltimaCartaLaberinto(solitario.getUltimaCartaLaberinto(), solitario.getTamanoLaberinto());
			comprobarPuertaConseguida();
			robar();
		} else {
			addLog(R.string.noPuedesJugarCarta);
			showToast(R.string.noPuedesJugarCarta);
		}
	}

	private void comprobarPuertaConseguida() {
		if (solitario.puertaConseguida()) {
			solitario.resetNumSeguidasPuerta();
			Color color = solitario.getColorUltimaCartaLaberinto();
			Carta puerta = solitario.robarPuerta(color);
			addLog(stringCartaResolver.getString(puerta) + " " + getText(R.string.conseguida));
			if (puerta!=null) {
				solitario.insertarPuerta(puerta);
				pintarPuertaConseguida(puerta);
				comprobarVictoria();
			}
		}
	}

	private void comprobarVictoria() {
		if (solitario.isVictoria()) {
			showDialog(DIALOG_VICTORIA);
		}
	}

	private void pintarPuertaConseguida(Carta puerta) {
		if (!solitario.isFinal()) {
			showToastPuerta(puerta);
		}
		pintarPuerta(getIdSiguientePuerta(), DrawableResolver.getDrawable(puerta));
	}

	private void pintarPuerta(int idView, int idImagen) {
		ImageView puerta = (ImageView) findViewById(idView);
		puerta.setImageResource(idImagen);
	}

	private int getIdSiguientePuerta() {
		switch (solitario.getNumPuertas()) {
		case 1:
			return R.id.puerta1;
		case 2:
			return R.id.puerta2;
		case 3:
			return R.id.puerta3;
		case 4:
			return R.id.puerta4;
		case 5:
			return R.id.puerta5;
		case 6:
			return R.id.puerta6;
		case 7:
			return R.id.puerta7;
		case 8:
			return R.id.puerta8;
		default:
			break;
		}
		return 0;
	}

	private void turnoDescartar(int index) {
		Carta carta = solitario.cogerCartaMano(index);
		addLog(getText(R.string.descartada) + " " + stringCartaResolver.getString(carta));
		//TODO: descarte de llaves
		solitario.descartar(carta);
		pintarUltimaCartaDescartes(solitario.getUltimaCartaDescartes(), solitario.getTamanoDescartes());
		robar();
	}

	private void pintarUltimaCartaLaberinto(Carta carta, int tamanoLaberinto) {
		RelativeLayout layoutLaberinto = null;
		int i = tamanoLaberinto - 1;
		if (i < MAX_CARTAS_FILA) {
			layoutLaberinto = (RelativeLayout) findViewById(R.id.laberinto1);
		} else {
			layoutLaberinto = (RelativeLayout) findViewById(R.id.laberinto2);
			i = i - MAX_CARTAS_FILA;
		}
		pintarCartaLaberinto(carta, layoutLaberinto, i);
	}

	private void pintarCartaLaberinto(Carta carta, RelativeLayout laberinto,
			int index) {
		ImageView cartaLaberinto = (ImageView) getLayoutInflater().inflate(
				R.layout.carta, null);
		cartaLaberinto.setImageResource(DrawableResolver.getDrawable(carta));
		LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		params.setMargins(10 * index, 0, 0, 0);
		laberinto.addView(cartaLaberinto, params);
	}

	private void pintarUltimaCartaDescartes(Carta carta, int tamanoDescartes) {
		RelativeLayout descartes = (RelativeLayout) findViewById(R.id.descartes);
		int i = tamanoDescartes - 1;
		pintarCartaDescartes(carta, descartes, i);
	}

	private void pintarCartaDescartes(Carta carta, RelativeLayout descartes, int index) {
		ImageView cartaDescarets = (ImageView) getLayoutInflater().inflate(
				R.layout.carta, null);
		cartaDescarets.setImageResource(DrawableResolver.getDrawable(carta));
		LayoutParams params = new RelativeLayout.LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		params.setMargins(4 * index, 0, 0, 0);
		descartes.addView(cartaDescarets, params);
	}

	private int getIndexCarta(int id) {
		switch (id) {
		case R.id.carta1:
			return 0;
		case R.id.carta2:
			return 1;
		case R.id.carta3:
			return 2;
		case R.id.carta4:
			return 3;
		case R.id.carta5:
			return 4;
		default:
			return 0;
		}
	}

	private void robar() {
		while (!solitario.isFinal()&& !solitario.isManoCompleta()) {
			Carta cartaRobada = solitario.robarMazo();
			addLog(getText(R.string.robar) + " " + stringCartaResolver.getString(cartaRobada));
			if (cartaRobada==null) {
				Log.w(TAG, "carta robada nula");
			} else if (cartaRobada.isLaberinto()) {
				solitario.rellenarMano(cartaRobada);
				pintarMano(solitario.getCartasMano());
			} else {
				// TODO: realizar acciones para puertas y pesadillas
				addLog(stringCartaResolver.getString(cartaRobada) + " " + getResources().getText(R.string.alLimbo));
				solitario.insertarLimbo(cartaRobada);
				// TODO: pintarLimbo?
			}
			comprobarDerrota();
		}
		if (!solitario.isLimboEmpty()) {
			solitario.insertarMazo(solitario.vaciarLimbo());
			solitario.barajarMazo();
			addLog(R.string.barajar);
		}
		setNumCartas();
	}

	private void comprobarDerrota() {
		if (solitario.isDerrota()) {
			showDialog(DIALOG_DERROTA);
		}
	}

	private void setNumCartas() {
		Integer numCartasMazo = solitario.getNumCartasMazo();
		((TextView) findViewById(R.id.numCartas)).setText(numCartasMazo.toString());
	}

	private void pintarLaberintoCompleto() {
		List<Carta> laberinto = solitario.getCartasLaberinto();
		RelativeLayout layoutLaberinto = (RelativeLayout) findViewById(R.id.laberinto1);
		int index = 0;
		for (Carta carta : laberinto) {
			pintarCartaLaberinto(carta, layoutLaberinto, index);			
			if (index >= MAX_CARTAS_FILA) {
				layoutLaberinto = (RelativeLayout) findViewById(R.id.laberinto2);
			}
			index++;
		}
	}

	private void pintarDescartesCompleto() {
		List<Carta> descartes = solitario.getDescartes();
		RelativeLayout layoutDescartes = (RelativeLayout) findViewById(R.id.descartes);
		int index = 0;
		for (Carta carta : descartes) {
			pintarCartaDescartes(carta, layoutDescartes, index);
			index++;
		}
	}

	private void pintarPuertasObtenidas() {
		List<Carta> puertas = solitario.getPuertasObtenidas();
		int index = 0;
		for (Carta carta : puertas) {
			pintarPuerta(getIdPuerta(index), DrawableResolver.getDrawable(carta));
			index++;
		}
	}

	private int getIdPuerta(int index) {
		switch (index) {
		case 0:
			return R.id.puerta1;
		case 1:
			return R.id.puerta2;
		case 2:
			return R.id.puerta3;
		case 3:
			return R.id.puerta4;
		case 4:
			return R.id.puerta5;
		case 5:
			return R.id.puerta6;
		case 6:
			return R.id.puerta7;
		case 7:
			return R.id.puerta8;
		default:
			break;
		}
		return 0;
	}

	private void addLog(int idTexto) {
		addLog(getResources().getString(idTexto));
	}

	private void addLog(String logText) {
		TextView text = (TextView)findViewById(R.id.mensaje);
		ScrollView scroll = (ScrollView)findViewById(R.id.scroll);
		text.setText(text.getText() + "\n" + logText);
		scroll.fullScroll(View.FOCUS_DOWN);
	}
}