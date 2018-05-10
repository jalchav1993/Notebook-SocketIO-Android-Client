package edu.utep.cs.cs4330.notebookio;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import edu.utep.cs.cs4330.notebookio.fileIo.NotebookRTS;

public class LoginActivity extends AppCompatActivity {
    private final static String LOGIN_FIELDS_PREFS = "login_fields_prefs";
    private final static String LOGIN_USERNAME_FIELD = "login_username_prefs";
    private final static String LOGIN_PASSWORD_FIELD = "login_password_prefs";
    private EditText editEmail, editPassword;
    private Button signIn, cancel;
    private Switch register;
    private CheckBox rememberPrefs;
    @Override
    public void onCreate(Bundle savedInstanceStat){
        super.onCreate(savedInstanceStat);
        setContentView(R.layout.activity_login);
        editEmail = findViewById(R.id.edit_email);
        editPassword = findViewById(R.id.edit_password);
        signIn= findViewById(R.id.sign_In);
        cancel = findViewById(R.id.cancel);
        rememberPrefs = findViewById(R.id.remember_prefs);
        register = findViewById(R.id.register_user);
        SharedPreferences pref = getSharedPreferences(LOGIN_FIELDS_PREFS,MODE_PRIVATE);
        String emailPrefs = pref.getString(LOGIN_USERNAME_FIELD, null);
        String passwordPrefs = pref.getString(LOGIN_PASSWORD_FIELD, null);

        if (emailPrefs != null || passwordPrefs != null) {
            editEmail.setText(emailPrefs);
            editPassword.setText(passwordPrefs);
            rememberPrefs.setChecked(true);
        }
        signIn.setOnClickListener((view) ->{
            String email, password;
            Socket socket;
            JSONObject json = new JSONObject();
            NotebookRTS notebookRTS = (NotebookRTS) getApplication();
            socket = notebookRTS.getSocket();
            email = editEmail.getText().toString();
            password = editPassword.getText().toString();
            try {
                json.put("Email", email);
                json.put("Password", password);
                socket.on("signin-request-accepted", singInAccepted);
                socket.on("signin-request-denied", singInDenied);
                socket.emit("sign-in-request", json.toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }


        });
        cancel.setOnClickListener((view) ->{
            this.finish();
        });
    }
    private Emitter.Listener singInAccepted = new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this,"sign in accepted", Toast.LENGTH_SHORT).show();
                    Intent result = new Intent();
                    result.putExtra("space", "local");
                    setResult(RESULT_OK, result);
                    finish();
                }
            });
        }
    };
    private Emitter.Listener singInDenied= new Emitter.Listener() {
        @Override
        public void call(final Object... args) {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(LoginActivity.this,"sign in denied", Toast.LENGTH_SHORT).show();
                }
            });
        }
    };
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    public void rememberPrefs(View view){
        String email = editEmail.getText().toString(), password = editPassword.getText().toString();
        //if )
        if(((CheckBox) view).isChecked()){
            if(email.length() > 5 && password.length() > 5){
                getSharedPreferences(LOGIN_FIELDS_PREFS,MODE_PRIVATE)
                        .edit()
                        .putString(LOGIN_USERNAME_FIELD, editEmail.getText().toString())
                        .putString(LOGIN_PASSWORD_FIELD, editPassword.getText().toString())
                        .apply();
            }else{
                ((CheckBox) view).setChecked(false);
                Toast.makeText(this, "email and password must be at least  6 characters",Toast.LENGTH_SHORT).show();
            }
        }else{
            getSharedPreferences(LOGIN_FIELDS_PREFS,MODE_PRIVATE)
                    .edit()
                    .putString(LOGIN_USERNAME_FIELD, null)
                    .putString(LOGIN_PASSWORD_FIELD, null)
                    .apply();
        }

    }
    public void startRegisterActivity(View view){
        startActivity(new Intent("edu.utep.cs.cs4330.notebookio.RegisterActivity"));
    }
    public void onResume() {
        super.onResume();
        // someone navigated back to this?
        register.setChecked(false);
    }
}
