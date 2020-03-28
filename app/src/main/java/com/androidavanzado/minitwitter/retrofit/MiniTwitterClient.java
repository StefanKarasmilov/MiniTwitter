package com.androidavanzado.minitwitter.retrofit;

import com.androidavanzado.minitwitter.common.Constantes;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MiniTwitterClient {

    private static MiniTwitterClient instance = null;
    private MiniTwitterService miniTwitterService;
    private Retrofit retrofit;

    // Constructor encargado de crear la instancia
    public MiniTwitterClient(){
        retrofit = new Retrofit.Builder()
                .baseUrl(Constantes.API_MINITWITTER_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        miniTwitterService = retrofit.create(MiniTwitterService.class);
    }

    // Patrón Singleto.
    // Consiste en que se cargue solo una vez la instancia y así no consumir muchos recursos
    public static MiniTwitterClient getInstance(){
        if(instance == null){
            instance = new MiniTwitterClient();
        }
        return instance;
    }

    public MiniTwitterService getMiniTwitterService(){
        return miniTwitterService;
    }

}
