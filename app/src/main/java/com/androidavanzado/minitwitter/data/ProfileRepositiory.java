package com.androidavanzado.minitwitter.data;

import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.androidavanzado.minitwitter.common.Constantes;
import com.androidavanzado.minitwitter.common.MyApp;
import com.androidavanzado.minitwitter.common.SharedPreferencesManager;
import com.androidavanzado.minitwitter.retrofit.AuthTwitterClient;
import com.androidavanzado.minitwitter.retrofit.AuthTwitterService;
import com.androidavanzado.minitwitter.retrofit.request.RequestCreateTweet;
import com.androidavanzado.minitwitter.retrofit.request.RequestUserProfile;
import com.androidavanzado.minitwitter.retrofit.response.Like;
import com.androidavanzado.minitwitter.retrofit.response.ResponseUploadPhoto;
import com.androidavanzado.minitwitter.retrofit.response.ResponseUserProfile;
import com.androidavanzado.minitwitter.retrofit.response.Tweet;
import com.androidavanzado.minitwitter.retrofit.response.TweetDeleted;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// En esta clase se implementan todos los métodos que va a hacer la API
public class ProfileRepositiory {

    AuthTwitterService authTwitterService;
    AuthTwitterClient authTwitterClient;
    MutableLiveData<ResponseUserProfile> userProfile;
    MutableLiveData<String> photoProfile;

    ProfileRepositiory(){
        authTwitterClient = AuthTwitterClient.getInstance();
        authTwitterService = authTwitterClient.getAuthTwitterService();
        userProfile = getProfile();
        if(photoProfile == null) {
            photoProfile = new MutableLiveData<>();
        }
    }

    public MutableLiveData<String> getPhotoProfile(){
        return photoProfile;
    }

    // Método para obtener los datos del perfil
    public MutableLiveData<ResponseUserProfile> getProfile(){
        if(userProfile == null){
            userProfile = new MutableLiveData<>();
        }

        Call<ResponseUserProfile> call = authTwitterService.getProfile();
        call.enqueue(new Callback<ResponseUserProfile>() {
            @Override
            public void onResponse(Call<ResponseUserProfile> call, Response<ResponseUserProfile> response) {
                if(response.isSuccessful()){
                    userProfile.setValue(response.body());
                }else{
                    Toast.makeText(MyApp.getContext(), "Algo ha ido mal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseUserProfile> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });

        return userProfile;
    }

    // Método para actualizar los datos del perfil
    public void updateProfile(RequestUserProfile requestUserProfile){
        Call<ResponseUserProfile> call = authTwitterService.updateProfile(requestUserProfile);

        call.enqueue(new Callback<ResponseUserProfile>() {
            @Override
            public void onResponse(Call<ResponseUserProfile> call, Response<ResponseUserProfile> response) {
                if(response.isSuccessful()){
                    userProfile.setValue(response.body());
                }else{
                    Toast.makeText(MyApp.getContext(), "Algo ha ido mal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseUserProfile> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void uploadPhoto(String photoPath){
        File file = new File(photoPath);
        MediaType mediaType = MediaType.parse("photo/jpg");
        RequestBody requestBody = RequestBody.create(mediaType, file);

        Call<ResponseUploadPhoto> call = authTwitterService.uploadProfilePhoto(requestBody);
        call.enqueue(new Callback<ResponseUploadPhoto>() {
            @Override
            public void onResponse(Call<ResponseUploadPhoto> call, Response<ResponseUploadPhoto> response) {
                if(response.isSuccessful()){
                    SharedPreferencesManager.setSomeStringValue(Constantes.PREF_PHOTOURL, response.body().getFilename());
                    photoProfile.setValue(response.body().getFilename());
                }else{
                    Toast.makeText(MyApp.getContext(), "Algo ha ido mal", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseUploadPhoto> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexión", Toast.LENGTH_SHORT).show();
            }
        });
    }

}
