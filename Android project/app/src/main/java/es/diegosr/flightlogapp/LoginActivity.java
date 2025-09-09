package es.diegosr.flightlogapp;

import android.content.Intent;
import android.database.Cursor;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import es.diegosr.flightlogapp.pojos.User;

public class LoginActivity extends AppCompatActivity {
    TextInputEditText loginUsername, loginPassword;
    Button loginButton;
    BDAdapter bdAdapter;
    User localUser;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bdAdapter = new BDAdapter(this);
        if(!((cursor = bdAdapter.getLocalUser()) != null && cursor.moveToFirst())){
            setContentView(R.layout.activity_login);
            loginUsername = findViewById(R.id.loginUsername);
            loginPassword = findViewById(R.id.loginPassword);
            loginButton = findViewById(R.id.loginButton);
            if(!((cursor = bdAdapter.getUser()) != null && cursor.moveToFirst())) {
                bdAdapter.rellenarDatos();
            }

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(loginUsername.getText().toString().isEmpty()) {
                        loginUsername.setError(getString(R.string.loginUsernameError));
                    }
                    else if(loginPassword.getText().toString().isEmpty()) {
                        loginPassword.setError(getString(R.string.loginPasswordError));
                    }
                    else if(!loginUsername.getText().toString().isEmpty() && !loginPassword.getText().toString().isEmpty()) {
                        localUser = bdAdapter.login(loginUsername.getText().toString(), loginPassword.getText().toString());

                        if(localUser != null) {
                            bdAdapter.addLocalUser(localUser);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Snackbar.make(findViewById(R.id.loginCoordinator), getString(R.string.loginError), Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }
                }
            });
        }
        else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setContentView(R.layout.activity_login);
        loginUsername = findViewById(R.id.loginUsername);
        loginPassword = findViewById(R.id.loginPassword);
        loginButton = findViewById(R.id.loginButton);
        if(!((cursor = bdAdapter.getUser()) != null && cursor.moveToFirst())) {
            bdAdapter.rellenarDatos();
        }

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(loginUsername.getText().toString().isEmpty()) {
                    loginUsername.setError(getString(R.string.loginUsernameError));
                }
                else if(loginPassword.getText().toString().isEmpty()) {
                    loginPassword.setError(getString(R.string.loginPasswordError));
                }
                else if(!loginUsername.getText().toString().isEmpty() && !loginPassword.getText().toString().isEmpty()) {
                    localUser = bdAdapter.login(loginUsername.getText().toString(), loginPassword.getText().toString());

                    if(localUser != null) {
                        bdAdapter.addLocalUser(localUser);
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                    }
                    else {
                        Snackbar.make(findViewById(R.id.loginCoordinator), getString(R.string.loginError), Snackbar.LENGTH_SHORT)
                                .show();
                    }
                }
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        bdAdapter = new BDAdapter(this);
        if(!((cursor = bdAdapter.getLocalUser()) != null && cursor.moveToFirst())){
            setContentView(R.layout.activity_login);
            loginUsername = findViewById(R.id.loginUsername);
            loginPassword = findViewById(R.id.loginPassword);
            loginButton = findViewById(R.id.loginButton);
            if(!((cursor = bdAdapter.getUser()) != null && cursor.moveToFirst())) {
                bdAdapter.rellenarDatos();
            }

            loginButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(loginUsername.getText().toString().isEmpty()) {
                        loginUsername.setError(getString(R.string.loginUsernameError));
                    }
                    else if(loginPassword.getText().toString().isEmpty()) {
                        loginPassword.setError(getString(R.string.loginPasswordError));
                    }
                    else if(!loginUsername.getText().toString().isEmpty() && !loginPassword.getText().toString().isEmpty()) {
                        localUser = bdAdapter.login(loginUsername.getText().toString(), loginPassword.getText().toString());

                        if(localUser != null) {
                            bdAdapter.addLocalUser(localUser);
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                        }
                        else {
                            Snackbar.make(findViewById(R.id.loginCoordinator), getString(R.string.loginError), Snackbar.LENGTH_SHORT)
                                    .show();
                        }
                    }
                }
            });
        }
        else {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
        }
    }
}
