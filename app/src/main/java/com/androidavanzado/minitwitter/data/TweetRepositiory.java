package com.androidavanzado.minitwitter.data;

import android.widget.Toast;

import androidx.lifecycle.MutableLiveData;

import com.androidavanzado.minitwitter.common.Constantes;
import com.androidavanzado.minitwitter.common.MyApp;
import com.androidavanzado.minitwitter.common.SharedPreferencesManager;
import com.androidavanzado.minitwitter.retrofit.AuthTwitterClient;
import com.androidavanzado.minitwitter.retrofit.AuthTwitterService;
import com.androidavanzado.minitwitter.retrofit.request.RequestCreateTweet;
import com.androidavanzado.minitwitter.retrofit.response.Like;
import com.androidavanzado.minitwitter.retrofit.response.Tweet;
import com.androidavanzado.minitwitter.retrofit.response.TweetDeleted;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

// En esta clase se implementan todos los métodos que va a hacer la API
public class TweetRepositiory {

    AuthTwitterService authTwitterService;
    AuthTwitterClient authTwitterClient;
    MutableLiveData<List<Tweet>> allTweets;
    MutableLiveData<List<Tweet>> favTweets;
    String userName;

    TweetRepositiory(){
        authTwitterClient = AuthTwitterClient.getInstance();
        authTwitterService = authTwitterClient.getAuthTwitterService();
        allTweets = getAllTweets();
        userName = SharedPreferencesManager.getSomeStringValue(Constantes.PREF_USERNAME);
    }

    // Método que nos permite obtener el estado de todos los tweets
    public MutableLiveData<List<Tweet>> getAllTweets(){
        if(allTweets == null){
            allTweets = new MutableLiveData<>();
        }

        Call<List<Tweet>> call = authTwitterService.getAllTweets();
        call.enqueue(new Callback<List<Tweet>>() {
            @Override
            public void onResponse(Call<List<Tweet>> call, Response<List<Tweet>> response) {
                if(response.isSuccessful()){
                    allTweets.setValue(response.body());

                }else{
                    Toast.makeText(MyApp.getContext(), "Algo ha ido mal", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<List<Tweet>> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexión", Toast.LENGTH_LONG).show();
            }
        });

        return allTweets;
    }

    public MutableLiveData<List<Tweet>> getFavsTweets(){
        if(favTweets == null){
            favTweets = new MutableLiveData<>();
        }

        List<Tweet> newFavList = new ArrayList<>();
        Iterator itTweets = allTweets.getValue().iterator();

        while(itTweets.hasNext()){
            Tweet current = (Tweet) itTweets.next();
            Iterator itLike = current.getLikes().iterator();
            boolean enc = false;
            while(itLike.hasNext() && !enc){
                Like like = (Like) itLike.next();
                if(like.getUsername().equals(userName)){
                    enc = true;
                    newFavList.add(current);
                }
            }
        }

        favTweets.setValue(newFavList);

        return favTweets;
    }

    // Este método nos va a permitir crear un nuevo tweet y refrescar
    public void createTweet(String mensaje){

        RequestCreateTweet requestCreateTweet = new RequestCreateTweet(mensaje);

        Call<Tweet> call = authTwitterService.createTweet(requestCreateTweet);

        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                if(response.isSuccessful()){
                    List<Tweet> listaClonada = new ArrayList<>();
                    // Añadimos en primer lugar el nuevo tweet que nos llega del server
                    listaClonada.add(response.body());
                    for(int i = 0; i < allTweets.getValue().size(); i++){
                        listaClonada.add(new Tweet(allTweets.getValue().get(i)));
                    }
                    allTweets.setValue(listaClonada);
                }else{
                    Toast.makeText(MyApp.getContext(), "Algo ha ido mal, inténtelo de nuevo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexión, inténtelo de nuevo", Toast.LENGTH_SHORT).show();
            }
        });

    }

    // Método para borrar tweet
    public void deleteTweet(final int idTweet){
        Call<TweetDeleted> call = authTwitterService.deleteTweet(idTweet);

        call.enqueue(new Callback<TweetDeleted>() {
            @Override
            public void onResponse(Call<TweetDeleted> call, Response<TweetDeleted> response) {
                if(response.isSuccessful()){
                    List<Tweet> clonedTweets = new ArrayList<>();
                    for(int i = 0; i < allTweets.getValue().size(); i++){
                        if(allTweets.getValue().get(i).getId() != idTweet){
                            clonedTweets.add(new Tweet(allTweets.getValue().get(i)));
                        }
                    }
                    // indicamos a los observadores de que hay una nueva lista
                    allTweets.setValue(clonedTweets);
                    getFavsTweets();
                }else{
                    Toast.makeText(MyApp.getContext(), "Algo ha ido mal, inténtelo de nuevo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<TweetDeleted> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexión, inténtelo de nuevo", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // Método para like tweet
    public void likTweet(final int idTweet){

        Call<Tweet> call = authTwitterService.likeTweet(idTweet);

        call.enqueue(new Callback<Tweet>() {
            @Override
            public void onResponse(Call<Tweet> call, Response<Tweet> response) {
                if(response.isSuccessful()) {
                    List<Tweet> listaClonada = new ArrayList<>();
                    // Añadimos en primer lugar el nuevo tweet que nos llega del server

                    for (int i = 0; i < allTweets.getValue().size(); i++) {
                        if (allTweets.getValue().get(i).getId() == idTweet) {
                            /* Si hemos encontrado en la lista original el elemento sobre el que
                            * hemos hecho like, introducimos el elemento que ha llegado del servidor */
                            listaClonada.add(response.body());
                        } else{
                            listaClonada.add(new Tweet(allTweets.getValue().get(i)));
                        }
                    }
                    allTweets.setValue(listaClonada);

                    getFavsTweets();
                }else{
                    Toast.makeText(MyApp.getContext(), "Algo ha ido mal, inténtelo de nuevo", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Tweet> call, Throwable t) {
                Toast.makeText(MyApp.getContext(), "Error en la conexión, inténtelo de nuevo", Toast.LENGTH_SHORT).show();
            }
        });

    }

}
