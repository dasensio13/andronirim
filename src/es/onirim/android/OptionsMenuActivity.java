package es.onirim.android;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

public class OptionsMenuActivity extends Activity {

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.options, menu);
		return true;
	}

	@Override
	public Dialog onCreateDialog(int id) {
		Dialog dialog = null;
		switch (id) {
		case R.id.creditos:
			dialog = buildDialogo(R.string.detalle_creditos);
			break;
		case R.id.reglas:
			dialog = buildDialogo(R.string.detalle_reglas);
			//TODO: completar reglas
			break;
		case R.id.instrucciones:
			dialog = buildDialogo(R.string.detalle_instrucciones);
			break;
		default:
			dialog = null;
		}
		return dialog;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.creditos:
		case R.id.reglas:
		case R.id.instrucciones:
			showDialog(item.getItemId());
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	protected Dialog buildDialogo(int idTexto) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		//TODO: mejorar con layout propio, incluso con tabs
		builder.setMessage(idTexto)
				.setCancelable(true)
				.setPositiveButton(R.string.aceptar,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								dialog.dismiss();
							}
						});
		return builder.create();
	}
}