package com.androidavanzado.minitwitter.retrofit;

import com.androidavanzado.minitwitter.retrofit.request.RequestLogin;
import com.androidavanzado.minitwitter.retrofit.request.RequestSignup;
import com.androidavanzado.minitwitter.retrofit.response.ResponseAuth;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface MiniTwitterService {

    // En el post se pone la ruta
    @POST("auth/login")
    // Devuelve un Call<ResponseAuth> por que es una llamada asincrona y va por un hilo independiente
    Call<ResponseAuth> doLogin(@Body RequestLogin requestLogin);

    @POST("auth/signup")
    Call<ResponseAuth> doSignUp(@Body RequestSignup requestSignup);



}
