package dasensio.android.onirim;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.ImageView;

public class AndronirimActivity extends OptionsMenuActivity implements OnClickListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splashview);

        final ImageView portada = (ImageView)findViewById(R.id.portada);

        portada.setOnClickListener(this);
    }

    public void onClick(View v) {
    	Intent intent = new Intent(AndronirimActivity.this, GameActivity.class);
        startActivity(intent);
    }
}