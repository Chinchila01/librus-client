package pl.librus.client.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import com.afollestad.materialdialogs.MaterialDialog;

import org.jdeferred.AlwaysCallback;
import org.jdeferred.FailCallback;
import org.jdeferred.Promise;
import org.jdeferred.android.AndroidDoneCallback;
import org.jdeferred.android.AndroidExecutionScope;

import pl.librus.client.R;
import pl.librus.client.api.APIClient;
import pl.librus.client.api.RegistrationIntentService;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "librus-schedule-debug";
    private MaterialDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);

        setContentView(R.layout.activity_login);

        final EditText passwordInput = (EditText) findViewById(R.id.password_input);
        final EditText usernameInput = (EditText) findViewById(R.id.username_input);
        final Button loginButton = (Button) findViewById(R.id.login_btn);
        final ProgressBar progress = (ProgressBar) findViewById(R.id.progressBar);
        progress.setVisibility(View.INVISIBLE);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                APIClient.login(usernameInput.getText().toString(), passwordInput.getText().toString(), getApplicationContext()).done(new AndroidDoneCallback<String>() {
                    @Override
                    public AndroidExecutionScope getExecutionScope() {
                        return AndroidExecutionScope.UI;
                    }

                    @Override
                    public void onDone(String result) {
                        Intent intent1 = new Intent(LoginActivity.this, MainActivity.class);
                        Intent intent2 = new Intent(getApplicationContext(), RegistrationIntentService.class);
                        startService(intent2);
                        startActivity(intent1);
                        finish();
                    }
                }).fail(new FailCallback<Integer>() {
                    @Override
                    public void onFail(Integer result) {
                        String message = result == 401 ? "Nieprawidłowe hasło, spróbuj ponownie" : "Wystąpił niespodziewany błąd " + result;
                        Snackbar snackbar = Snackbar
                                .make(findViewById(R.id.coordinator), message, Snackbar.LENGTH_SHORT);

                        snackbar.show();
                        Log.d(TAG, "onUpdateComplete: login failure, code " + (int) result);
                    }
                }).always(new AlwaysCallback<String, Integer>() {
                    @Override
                    public void onAlways(Promise.State state, String resolved, Integer rejected) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                progress.setVisibility(View.INVISIBLE);
                            }
                        });
                    }
                });
                progress.setVisibility(View.VISIBLE);
            }
        });

    }
}
