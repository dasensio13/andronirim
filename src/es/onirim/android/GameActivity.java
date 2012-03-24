package es.onirim.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;
import es.onirim.core.Carta;
import es.onirim.core.Carta.Color;
import es.onirim.core.Laberinto;
import es.onirim.core.Mano;
import es.onirim.core.PilaDescartes;
import es.onirim.core.Solitario;

public class GameActivity extends Activity {

	private Solitario solitario = null;
	private ImageView cartaSeleccionada = null;

	private static final int DIALOG_JUGAR = 0;
	private static final int MAX_CARTAS_FILA = 40;
	

	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.game);

        solitario = new Solitario();
        pintarMano(solitario.getMano());
        setNumCartas();
    }

	@Override
	public Dialog onCreateDialog(int id) {
	    Dialog dialog = null;
	    switch(id) {
	    case DIALOG_JUGAR:
	    	dialog = buildDialogoJugar();
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
		       .setPositiveButton(R.string.jugar, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   turnoJugar(cartaSeleccionada);
		           }
		       })
		       .setNegativeButton(R.string.descartar, new DialogInterface.OnClickListener() {
		           public void onClick(DialogInterface dialog, int id) {
		        	   turnoDescartar(cartaSeleccionada);
		           }
		       });
		return builder.create();
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.carta, menu);
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo) item.getMenuInfo();
	    switch (item.getItemId()) {
	        case R.id.jugar:
	            Toast.makeText(this, R.string.jugar, Toast.LENGTH_SHORT).show();
	            return true;
	        case R.id.descartar:
	        	Toast.makeText(this, R.string.descartar, Toast.LENGTH_SHORT).show();
	            return true;
	        default:
	            return super.onContextItemSelected(item);
	    }
	}

	private void pintarMano(Mano mano) {
		ImageView vCarta = (ImageView) findViewById(R.id.carta1);
		//TODO registerForContextMenu(vCarta);
		pintarCartaMano(mano.getCarta(0), vCarta);

		vCarta = (ImageView) findViewById(R.id.carta2);
		pintarCartaMano(mano.getCarta(1), vCarta);

		vCarta = (ImageView) findViewById(R.id.carta3);
		pintarCartaMano(mano.getCarta(2), vCarta);

		vCarta = (ImageView) findViewById(R.id.carta4);
		pintarCartaMano(mano.getCarta(3), vCarta);

		vCarta = (ImageView) findViewById(R.id.carta5);
		pintarCartaMano(mano.getCarta(4), vCarta);
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
				cartaSeleccionada = (ImageView)v;
				showDialog(DIALOG_JUGAR);
				return true;
			}
		});

	}

	private void turnoJugar(View v) {
		int index = getIndexCarta(v.getId());
		Carta carta = solitario.getMano().cogerCarta(index);
		if (solitario.getLaberinto().puedoInsertarCarta(carta)) {
			solitario.getLaberinto().insertar(carta);
			pintarUltimaCartaLaberinto(solitario.getLaberinto());
			comprobarPuertaConseguida();
			robar();
		} else {
			Toast.makeText(GameActivity.this, R.string.noPuedesJugarCarta, Toast.LENGTH_SHORT).show();
		}
	}

	private void comprobarPuertaConseguida() {
		if (solitario.getLaberinto().puertaConseguida()) {
			solitario.getLaberinto().resetNumSeguidas();
			Color color = solitario.getLaberinto().getUltimaCarta().getColor();
			Carta puerta = solitario.robarPuerta(color);
			solitario.getPuertas().insertarPuerta(puerta);
			pintarPuertaConseguida(puerta);
			comprobarVictoria();
		}
	}

	private void comprobarVictoria() {
		if (solitario.getPuertas().isVictoria()) {
			//TODO: Dialogo y al cerrar volver atrás
			Toast.makeText(this, R.string.victoria, Toast.LENGTH_LONG).show();
		}
	}

	private void pintarPuertaConseguida(Carta puerta) {
		ImageView puertaConseguida = (ImageView)findViewById(getIdSiguientePuerta());
		puertaConseguida.setImageResource(DrawableResolver.getDrawable(puerta));
	}

	private int getIdSiguientePuerta() {
		switch (solitario.getPuertas().numPuertas()) {
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
		Carta carta = solitario.getMano().cogerCarta(index);
		solitario.getDescartes().insertar(carta);
		pintarUltimaCartaDescartes(solitario.getDescartes());
		robar();
	}

	private void pintarUltimaCartaLaberinto(Laberinto laberinto) {
		RelativeLayout layoutLaberinto = null;
		int i = laberinto.getLaberinto().size()-1;
		if (i<MAX_CARTAS_FILA) {
			layoutLaberinto = (RelativeLayout)findViewById(R.id.laberinto1);
		} else {
			layoutLaberinto = (RelativeLayout)findViewById(R.id.laberinto2);
			i = i-MAX_CARTAS_FILA;
		}
		Carta carta = laberinto.getUltimaCarta();
		pintarCartaLaberinto(carta, layoutLaberinto, i);
	}

	private void pintarCartaLaberinto(Carta carta, RelativeLayout laberinto, int index) {
		ImageView cartaLaberinto = (ImageView)getLayoutInflater().inflate(R.layout.carta, null);
		cartaLaberinto.setImageResource(DrawableResolver.getDrawable(carta));
		LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		params.setMargins(10*index, 0, 0, 0);
		laberinto.addView(cartaLaberinto, params);
	}

	private void pintarUltimaCartaDescartes(PilaDescartes pilaDescartes) {
		RelativeLayout descartes = (RelativeLayout)findViewById(R.id.descartes);
		int i = pilaDescartes.getPila().size()-1;
		Carta carta = pilaDescartes.getUltimaCarta();
		pintarCartaDescartes(carta, descartes, i);
	}

	private void pintarCartaDescartes(Carta carta, RelativeLayout descartes, int index) {
		ImageView cartaDescarets = (ImageView)getLayoutInflater().inflate(R.layout.carta, null);
		cartaDescarets.setImageResource(DrawableResolver.getDrawable(carta));
		LayoutParams params = new RelativeLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.FILL_PARENT);
		params.setMargins(4*index, 0, 0, 0);
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
		while (!solitario.getMano().isCompleta()) {
			Carta cartaRobada = solitario.getMazo().robar();
			if (cartaRobada.isLaberinto()) {
				solitario.getMano().rellenarMano(cartaRobada);
				pintarMano(solitario.getMano());
			} else {
				//TODO: realizar acciones para puertas y pesadillas
				Toast.makeText(GameActivity.this, cartaRobada + " al Limbo", Toast.LENGTH_SHORT).show();
				solitario.getLimbo().insertar(cartaRobada);
				//TODO: pintarLimbo
			}
		}
		if (!solitario.getLimbo().isEmpty()) {
			solitario.getMazo().insertar(solitario.getLimbo().vaciar());
			solitario.getMazo().barajar();
			Toast.makeText(GameActivity.this, R.string.barajar, Toast.LENGTH_SHORT).show();
		}
		setNumCartas();
	}

	private void setNumCartas() {
		Integer numCartasMazo = solitario.getMazo().getNumCartas();
		((TextView)findViewById(R.id.numCartas)).setText(numCartasMazo.toString());
	}

	private void pintarLaberinto(Laberinto laberinto) {
		RelativeLayout laberinto1 = (RelativeLayout)findViewById(R.id.laberinto1);
		int i = 0;
		for (Carta carta : laberinto.getLaberinto()) {
			pintarCartaLaberinto(carta, laberinto1, i);
			i++;
		}
	}

}