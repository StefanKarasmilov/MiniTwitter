package com.androidavanzado.minitwitter.userinterface.auth;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.androidavanzado.minitwitter.R;
import com.androidavanzado.minitwitter.common.Constantes;
import com.androidavanzado.minitwitter.common.SharedPreferencesManager;
import com.androidavanzado.minitwitter.retrofit.request.RequestLogin;
import com.androidavanzado.minitwitter.retrofit.response.ResponseAuth;
import com.androidavanzado.minitwitter.retrofit.MiniTwitterClient;
import com.androidavanzado.minitwitter.retrofit.MiniTwitterService;
import com.androidavanzado.minitwitter.userinterface.DashboardActivity;

import retrofit2.*;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnLogin;
    TextView tvGoSingUp;
    EditText etEmail, etPassword;
    MiniTwitterClient miniTwitterClient;
    MiniTwitterService miniTwitterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Método que oculta el Toolbar
        getSupportActionBar().hide();

        retrofitInit();
        findViews();
        events();

    }

    private void retrofitInit() {
        miniTwitterClient = MiniTwitterClient.getInstance();
        miniTwitterService = miniTwitterClient.getMiniTwitterService();
    }

    // Método encargado de relacionar las variables con la UI
    private void findViews() {
        btnLogin = findViewById(R.id.buttonLogin);
        tvGoSingUp = findViewById(R.id.textViewGoSignUp);
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
    }

    // Método encargado de gestionar los eventos
    private void events() {
        btnLogin.setOnClickListener(this);
        tvGoSingUp.setOnClickListener(this);
    }

    // Método que gestiona los eventos clic
    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id){

            case R.id.buttonLogin:
                goToLogin();
                break;
            case R.id.textViewGoSignUp:
                goToSingUp();
                break;

        }
    }

    // Método que se encarga de loguear al usuario
    private void goToLogin() {
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if(email.isEmpty()){
            etEmail.setError("El email es requerido");
        }else if(password.isEmpty()){
            etPassword.setError("La contraseña es requerida");
        }else{
            // Este cuergo es el encargado de redirigir el flujo del programa
            RequestLogin requestLogin = new RequestLogin(email, password);
            Call<ResponseAuth> call = miniTwitterService.doLogin(requestLogin);

            call.enqueue(new Callback<ResponseAuth>() {
                @Override
                public void onResponse(Call<ResponseAuth> call, Response<ResponseAuth> response) {
                    if(response.isSuccessful()){
                        Toast.makeText(MainActivity.this, "Sesión iniciada correctamente", Toast.LENGTH_LONG).show();
                        //Invocación para poder guardar clave y valor
                        SharedPreferencesManager
                                .setSomeStringValue(Constantes.PREF_TOKEN, response.body().getToken());
                        SharedPreferencesManager
                                .setSomeStringValue(Constantes.PREF_USERNAME, response.body().getUsername());
                        SharedPreferencesManager
                                .setSomeStringValue(Constantes.PREF_EMAIL, response.body().getEmail());
                        SharedPreferencesManager
                                .setSomeStringValue(Constantes.PREF_PHOTOURL, response.body().getPhotoUrl());
                        SharedPreferencesManager
                                .setSomeStringValue(Constantes.PREF_CREATED, response.body().getCreated());
                        SharedPreferencesManager
                                .setSomeBooleanValue(Constantes.PREF_ACTIVE, response.body().getActive());

                            Intent i = new Intent(MainActivity.this, DashboardActivity.class);
                            startActivity(i);
                            // Destruimos este Activity para que no se pueda volver.
                            finish();
                    }else{
                        Toast.makeText(MainActivity.this, "Algo fue mal, revise sus datos de acceso", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseAuth> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Problemas de conexión. Inténtelo de nuevo", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    // Método que nos envia a la activity para hacer la registración
    private void goToSingUp() {
        Intent i = new Intent(MainActivity.this, SignUpActivity.class);
        startActivity(i);
        finish();
    }
}
