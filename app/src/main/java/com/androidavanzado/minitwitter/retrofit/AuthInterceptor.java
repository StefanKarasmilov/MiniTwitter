package com.androidavanzado.minitwitter.retrofit;

import com.androidavanzado.minitwitter.common.Constantes;
import com.androidavanzado.minitwitter.common.SharedPreferencesManager;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class AuthInterceptor implements Interceptor {


    @Override
    public Response intercept(Chain chain) throws IOException {
        String token = SharedPreferencesManager.getSomeStringValue(Constantes.PREF_TOKEN);
        // Añadimos una cabecera
        Request request = chain.request()
                .newBuilder()
                .addHeader("Authorization", "Bearer " + token)
                .build();
        return chain.proceed(request);
    }
}
