package es.onirim.android;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import es.onirim.core.Carta;
import es.onirim.core.Carta.Color;
import es.onirim.core.Solitario;

public class GameActivity extends Activity {

	private Solitario solitario = null;
	private ImageView cartaSeleccionada = null;

	private static final int DIALOG_JUGAR = 0;
	private static final int DIALOG_VICTORIA = 1;
	private static final int DIALOG_DERROTA = 2;

	private static final int MAX_CARTAS_FILA = 40;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.game);

		solitario = new Solitario();
		pintarMano(solitario.getCartasMano());
		setNumCartas();
	}

	@Override
	public Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case DIALOG_JUGAR:
			dialog = buildDialogoJugar();
			break;
		case DIALOG_VICTORIA:
			dialog = buildDialogoVictoria();
			break;
		case DIALOG_DERROTA:
			dialog = buildDialogoDerrota();
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	private Dialog buildDialogoJugar() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.jugarDescartar)
				.setCancelable(true)
				.setPositiveButton(R.string.jugar,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								turnoJugar(cartaSeleccionada);
							}
						})
				.setNegativeButton(R.string.descartar,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								turnoDescartar(cartaSeleccionada);
							}
						});
		return builder.create();
	}

	private Dialog buildDialogoVictoria() {
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
		Toast toast = Toast.makeText(this, resourceId, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	private void showToast(String texto) {
		Toast toast = Toast.makeText(this, texto, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
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
				turnoJugar(v);
			}
		});

		vCarta.setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				cartaSeleccionada = (ImageView) v;
				showDialog(DIALOG_JUGAR);
				return true;
			}
		});

	}

	private void turnoJugar(View v) {
		int index = getIndexCarta(v.getId());
		Carta carta = solitario.cogerCartaMano(index);
		if (solitario.puedoInsertarCartaLaberinto(carta)) {
			solitario.insertarCartaLaberinto(carta);
			pintarUltimaCartaLaberinto(solitario.getUltimaCartaLaberinto(), solitario.getTamanoLaberinto());
			comprobarPuertaConseguida();
			robar();
		} else {
			showToast(R.string.noPuedesJugarCarta);
		}
	}

	private void comprobarPuertaConseguida() {
		if (solitario.puertaConseguida()) {
			solitario.resetNumSeguidasPuerta();
			Color color = solitario.getColorUltimaCartaLaberinto();
			Carta puerta = solitario.robarPuerta(color);
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
		ImageView puertaConseguida = (ImageView) findViewById(getIdSiguientePuerta());
		puertaConseguida.setImageResource(DrawableResolver.getDrawable(puerta));
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

	private void turnoDescartar(View v) {
		int index = getIndexCarta(v.getId());
		Carta carta = solitario.cogerCartaMano(index);
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

	private void pintarCartaDescartes(Carta carta, RelativeLayout descartes,
			int index) {
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
		while (!solitario.isManoCompleta()) {
			Carta cartaRobada = solitario.robarMazo();
			if (cartaRobada.isLaberinto()) {
				solitario.rellenarMano(cartaRobada);
				pintarMano(solitario.getCartasMano());
			} else {
				// TODO: realizar acciones para puertas y pesadillas
				showToast(cartaRobada + " al LIMBO");
				solitario.insertarLimbo(cartaRobada);
				// TODO: pintarLimbo?
			}
			comprobarDerrota();
		}
		if (!solitario.isLimboEmpty()) {
			solitario.insertarMazo(solitario.vaciarLimbo());
			solitario.barajarMazo();
			showToast(R.string.barajar);
		}
		setNumCartas();
	}

	private void comprobarDerrota() {
		if (solitario.getNumHabitacionesMazo()==0) {
			showDialog(DIALOG_DERROTA);
		}
	}

	private void setNumCartas() {
		Integer numCartasMazo = solitario.getNumCartasMazo();
		((TextView) findViewById(R.id.numCartas)).setText(numCartasMazo.toString());
	}
}