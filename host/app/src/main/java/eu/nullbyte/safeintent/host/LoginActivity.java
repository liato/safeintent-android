package eu.nullbyte.safeintent.host;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;


public class LoginActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            ((EditText)findViewById(R.id.edt_username)).setText(extras.getString("username", ""));
            ((EditText)findViewById(R.id.edt_password)).setText(extras.getString("password", ""));
        }
    }
}
