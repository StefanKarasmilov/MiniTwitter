package com.androidavanzado.minitwitter.retrofit;

import com.androidavanzado.minitwitter.common.Constantes;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AuthTwitterClient {

    private static AuthTwitterClient instance = null;
    private AuthTwitterService miniTwitterService;
    private Retrofit retrofit;

    // Constructor encargado de crear la instancia
    public AuthTwitterClient(){
        // Incluir en la cabecera de la petición el TOKEN que autoriza al usuario
        OkHttpClient.Builder okHttpClientBuilder = new OkHttpClient.Builder();
        okHttpClientBuilder.addInterceptor(new AuthInterceptor());
        OkHttpClient client = okHttpClientBuilder.build();

        retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.API_MINITWITTER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();

        miniTwitterService = retrofit.create(AuthTwitterService.class);
    }

    // Patrón Singleto.
    // Consiste en que se cargue solo una vez la instancia y así no consumir muchos recursos
    public static AuthTwitterClient getInstance(){
        if(instance == null){
            instance = new AuthTwitterClient();
        }
        return instance;
    }

    public AuthTwitterService getAuthTwitterService(){
        return miniTwitterService;
    }

}
