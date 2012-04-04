package es.onirim.android;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import es.onirim.core.Carta;
import es.onirim.core.Carta.Color;
import es.onirim.core.Carta.Simbolo;
import es.onirim.core.Carta.Tipo;
import es.onirim.core.Solitario;

public class GameActivity extends OptionsMenuActivity {

	private Solitario solitario = null;
	private StringCartaResolver stringCartaResolver = null;

	private static final String DRAWABLE_CARTA = "DRAWABLE_CARTA";
	private static final String DRAWABLE_LLAVE = "DRAWABLE_LLAVE";
	private static final String INDEX_CARTA = "INDEX_CARTA";
	private static final String SOLITARIO = "SOLITARIO";
	private static final String LOG = "LOG";

	private static final int DIALOG_JUGAR = 0;
	private static final int DIALOG_VICTORIA = 1;
	private static final int DIALOG_DERROTA = 2;
	private static final int DIALOG_PESADILLA = 3;
	private static final int DIALOG_PUERTA = 4;

	private static final int MAX_CARTAS_FILA = 40;

	private static final String TAG = "GameActivity";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.game);

		stringCartaResolver = new StringCartaResolver(getResources());

		Map<String, Object> config = (Map<String, Object>)getLastNonConfigurationInstance();
		if (config==null) {
			solitario = new Solitario();
		} else {
			solitario = (Solitario)config.get(SOLITARIO);
			pintarPuertasObtenidas();
			pintarLaberintoCompleto();
			pintarDescartesCompleto();
			CharSequence log = (CharSequence)config.get(LOG);
			TextView text = (TextView)findViewById(R.id.mensaje);
			text.setText(log);
		}
		pintarMano(solitario.getCartasMano());
		setNumCartas();
	}

	@Override
	public Object onRetainNonConfigurationInstance() {
		Map<String, Object> config = new HashMap<String, Object>();
		config.put(SOLITARIO, solitario);
		TextView logMessage = (TextView)findViewById(R.id.mensaje);
		config.put(LOG, logMessage.getText());
	    return config;
	}

	@Override
	public Dialog onCreateDialog(int id, Bundle bundle) {
		Dialog dialog = null;
		switch (id) {
		case DIALOG_JUGAR:
			dialog = buildDialogoJugar(bundle);
			break;
		case DIALOG_VICTORIA:
			dialog = buildDialogoVictoria(bundle);
			break;
		case DIALOG_DERROTA:
			dialog = buildDialogoDerrota();
			break;
		case DIALOG_PESADILLA:
			dialog = buildDialogoPesadilla(bundle);
			break;
		case DIALOG_PUERTA:
			dialog = buildDialogoPuerta(bundle);
			break;
		default:
			dialog = super.onCreateDialog(id);
		}
		return dialog;
	}

	private Dialog buildDialogoJugar(final Bundle bundle) {
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.carta_dialog, null);

		ImageView image = (ImageView) layout.findViewById(R.id.carta_dialog);
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

	private Dialog buildDialogoPesadilla(final Bundle bundle) {
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.carta_dialog, null);

		ImageView image = (ImageView) layout.findViewById(R.id.carta_dialog);
		image.setImageResource(bundle.getInt(DRAWABLE_CARTA));

		AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
		builder.setView(layout)
				.setTitle(R.string.title_dialog_pesadilla)
				.setPositiveButton(R.string.descartar_mano,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// TODO
								turnoRobar();
								removeDialog(id);
							}
						})
				.setNegativeButton(R.string.descartar_mazo,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// TODO
								turnoRobar();
								removeDialog(id);
							}
						})
				.setNeutralButton(R.string.descartar_llave,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// TODO
								turnoRobar();
								removeDialog(id);
							}
						});
		// TODO: descartar puerta
		return builder.create();
	}

	private Dialog buildDialogoPuerta(final Bundle bundle) {
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.obtener_puerta_dialog, null);

		ImageView image = (ImageView) layout.findViewById(R.id.puerta_obtener);
		image.setImageResource(bundle.getInt(DRAWABLE_CARTA));
		image = (ImageView) layout.findViewById(R.id.llave_obtener);
		image.setImageResource(bundle.getInt(DRAWABLE_LLAVE));

		AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
		builder.setView(layout)
				.setTitle(R.string.title_dialog_puerta)
				.setPositiveButton(R.string.si,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// TODO
								turnoRobar();
								removeDialog(id);
							}
						})
				.setNegativeButton(R.string.no,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								// TODO
								turnoRobar();
								removeDialog(id);
							}
						});
		return builder.create();
	}

	private Dialog buildDialogoVictoria(Bundle bundle) {
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.carta_dialog, null);

		ImageView image = (ImageView) layout.findViewById(R.id.carta_dialog);
		image.setImageResource(bundle.getInt(DRAWABLE_CARTA));

		AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
		builder.setView(layout)
				.setTitle(R.string.victoria)
				.setPositiveButton(R.string.aceptar,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								finish();
							}
						});
		return builder.create();
	}

	private Dialog buildDialogoDerrota() {
		Context mContext = getApplicationContext();
		LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.carta_dialog, null);

		ImageView image = (ImageView) layout.findViewById(R.id.carta_dialog);
		image.setImageResource(R.drawable.pesadilla);

		AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
		builder.setView(layout)
				.setTitle(R.string.derrota)
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
		String texto = getResources().getText(resourceId).toString();
		Toast toast = Toast.makeText(this, texto, Toast.LENGTH_SHORT);
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.show();
	}

	private void showToastPuerta(Carta puerta) {
		LayoutInflater inflater = getLayoutInflater();
		View layout = inflater.inflate(R.layout.puerta_obtenida, null);

		TextView text = (TextView) layout.findViewById(R.id.puerta_obtenida);
		text.setText(stringCartaResolver.getString(puerta) + " " + getText(R.string.conseguida));
		text.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getResources().getDrawable(DrawableResolver.getDrawable(puerta)));

		Toast toast = new Toast(getApplicationContext());
		toast.setGravity(Gravity.CENTER, 0, 0);
		toast.setDuration(Toast.LENGTH_SHORT);
		toast.setView(layout);
		toast.show();
	}

	private void pintarMano(List<Carta> cartas) {
		int i = 0;
		for (Carta carta : cartas) {
			pintarCartaMano(carta, i, false);
			i++;
		}
	}

	private void pintarCartaMano(Carta carta, int index, boolean anim) {
		ImageView vCarta = (ImageView) findViewById(getIdCarta(index));
		cambiarCarta(carta, vCarta, anim);
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

	private void cambiarCarta(final Carta carta, final ImageView vCarta, boolean animado) {
		if (animado) {
			Animation anim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.carta_va);
			AnimationListener aListener = new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {}
				@Override
				public void onAnimationRepeat(Animation animation) {}
				@Override
				public void onAnimationEnd(Animation animation) {
					vCarta.setImageResource(DrawableResolver.getDrawable(carta));
					Animation anim = AnimationUtils.loadAnimation(GameActivity.this, R.anim.carta_viene);
					vCarta.startAnimation(anim);
				}
			};
			anim.setAnimationListener(aListener);
			vCarta.startAnimation(anim);
		} else {
			vCarta.setImageResource(DrawableResolver.getDrawable(carta));
		}
	}

	private void turnoJugar(int index) {
		Carta carta = solitario.getCartaMano(index);
		addLog(getText(R.string.jugada) + " " + stringCartaResolver.getString(carta));
		if (solitario.puedoInsertarCartaLaberinto(carta)) {
			solitario.cogerCartaMano(index);
			solitario.insertarCartaLaberinto(carta);
			pintarUltimaCartaLaberinto(solitario.getUltimaCartaLaberinto(), solitario.getTamanoLaberinto());
			comprobarPuertaConseguida();
			turnoRobar();
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
				comprobarVictoria(puerta);
			}
		}
	}

	private void comprobarVictoria(Carta puerta) {
		if (solitario.isVictoria()) {
			Bundle bundle = new Bundle();
			bundle.putInt(DRAWABLE_CARTA, DrawableResolver.getDrawable(puerta));
			showDialog(DIALOG_VICTORIA, bundle);
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
		turnoRobar();
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

	private void pintarCartaLaberinto(Carta carta, RelativeLayout laberinto, int index) {
		ImageView cartaLaberinto = (ImageView) getLayoutInflater().inflate(R.layout.carta, null);
		cartaLaberinto.setImageResource(DrawableResolver.getDrawable(carta));
		LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
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

	private void turnoRobar() {
		if (!solitario.isFinal() && !solitario.isManoCompleta()) {
			Carta cartaRobada = solitario.robarMazo();
			addLog(getText(R.string.robar) + " " + stringCartaResolver.getString(cartaRobada));
			if (cartaRobada==null) {
				Log.w(TAG, "carta robada nula");
				turnoRobar();
			} else if (cartaRobada.isLaberinto()) {
				int posicion = solitario.rellenarMano(cartaRobada);
				pintarCartaMano(cartaRobada, posicion, true);
				turnoRobar();
			} else if (cartaRobada.isPesadilla()) {
				// TODO: realizar acciones para pesadilla
				Bundle bundle = new Bundle();
				bundle.putInt(DRAWABLE_CARTA, DrawableResolver.getDrawable(cartaRobada));
				showDialog(DIALOG_PESADILLA, bundle);
			} else if (cartaRobada.isPuerta()){
				// TODO: realizar acciones para puerta
				if (solitario.tieneLlaveMano(cartaRobada.getColor())) {
					Bundle bundle = new Bundle();
					bundle.putInt(DRAWABLE_CARTA, DrawableResolver.getDrawable(cartaRobada));
					bundle.putInt(DRAWABLE_LLAVE, DrawableResolver.getDrawable(new Carta(Tipo.LABERINTO, cartaRobada.getColor(), Simbolo.LLAVE)));
					showDialog(DIALOG_PUERTA, bundle);
					solitario.insertarLimbo(cartaRobada); //TODO
				} else {
					addLog(stringCartaResolver.getString(cartaRobada) + " " + getResources().getText(R.string.alLimbo));
					solitario.insertarLimbo(cartaRobada);
					turnoRobar();
				}
				// TODO: ¿pintarLimbo?
			}
			comprobarDerrota();
			setNumCartas();
		} else {
			comprobarDerrota();
			turnoBarajar();
			setNumCartas();
		}
	}

	private void turnoBarajar() {
		if (!solitario.isLimboEmpty()) {
			solitario.insertarMazo(solitario.vaciarLimbo());
			solitario.barajarMazo();
			addLog(R.string.barajar);
		}
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

	private int getIdCarta(int index) {
		switch (index) {
		case 0:
			return R.id.carta1;
		case 1:
			return R.id.carta2;
		case 2:
			return R.id.carta3;
		case 3:
			return R.id.carta4;
		case 4:
			return R.id.carta5;
		default:
			break;
		}
		return 0;
	}

	private void addLog(int idTexto) {
		addLog(getResources().getString(idTexto));
	}

	private void addLog(String logText) {
		Log.d(TAG, logText);
		TextView text = (TextView)findViewById(R.id.mensaje);
		text.setText(logText + "\n" + text.getText());
	}
}