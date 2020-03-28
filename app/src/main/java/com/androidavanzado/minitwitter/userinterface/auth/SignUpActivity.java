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
import com.androidavanzado.minitwitter.retrofit.request.RequestSignup;
import com.androidavanzado.minitwitter.retrofit.response.ResponseAuth;
import com.androidavanzado.minitwitter.retrofit.MiniTwitterClient;
import com.androidavanzado.minitwitter.retrofit.MiniTwitterService;
import com.androidavanzado.minitwitter.userinterface.DashboardActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SignUpActivity extends AppCompatActivity implements View.OnClickListener {

    Button btnSingUp;
    TextView tvGoLogin;
    EditText etUsername, etEmail, etPassword;
    MiniTwitterClient miniTwitterClient;
    MiniTwitterService miniTwitterService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

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

    private void findViews() {
        btnSingUp = findViewById(R.id.buttonSignUp);
        tvGoLogin = findViewById(R.id.textViewGoLogin);
        etUsername = findViewById(R.id.editTextUsername);
        etEmail = findViewById(R.id.editTextEmail);
        etPassword = findViewById(R.id.editTextPassword);
    }

    private void events() {
        btnSingUp.setOnClickListener(this);
        tvGoLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();

        switch(id){

            case R.id.buttonSignUp:
                goToSignUp();
                break;
            case R.id.textViewGoLogin:
                goToLogin();
                break;

        }
    }

    // Método recoge los datos del registro y lo tramita redirigiendo el flujo hacie otro activity
    private void goToSignUp() {
        String username = etUsername.getText().toString();
        String email = etEmail.getText().toString();
        String password = etPassword.getText().toString();

        if(username.isEmpty()){
            etUsername.setError("El nombre de usuario es requerido");
        }else if(email.isEmpty()){
            etEmail.setError("El email es requerido");
        }else if(password.isEmpty() || password.length() < 4){
            etPassword.setError("La contraseña es requerida y debe tener al menos 4 caracteres");
        }else{
            // Este cuergo es el encargado de redirigir el flujo del programa
            String code = "UDEMYANDROID";
            RequestSignup requestSignup = new RequestSignup(username, email, password, code);
            Call<ResponseAuth> call = miniTwitterService.doSignUp(requestSignup);

            call.enqueue(new Callback<ResponseAuth>() {
                @Override
                public void onResponse(Call<ResponseAuth> call, Response<ResponseAuth> response) {
                    if(response.isSuccessful()){

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

                        Intent i = new Intent(SignUpActivity.this, DashboardActivity.class);
                        startActivity(i);
                        Toast.makeText(SignUpActivity.this, "Registro existoso", Toast.LENGTH_LONG) .show();
                        finish();
                    }else{
                        Toast.makeText(SignUpActivity.this, "Algo ha ido mal, revise los datos", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<ResponseAuth> call, Throwable t) {
                    Toast.makeText(SignUpActivity.this, "Error en la conexión. Inténtelo de nuevo", Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void goToLogin() {
        Intent i = new Intent(SignUpActivity.this, MainActivity.class);
        startActivity(i);
        finish();
    }
}
