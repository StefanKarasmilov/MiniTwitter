package com.androidavanzado.minitwitter.data;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.androidavanzado.minitwitter.retrofit.response.Tweet;
import com.androidavanzado.minitwitter.userinterface.tweets.BottomModalTweetFragment;

import java.util.List;

public class TweetViewModel extends AndroidViewModel {

    private TweetRepositiory tweetRepositiory;
    private LiveData<List<Tweet>> tweets;
    private LiveData<List<Tweet>> favTweets;

    public TweetViewModel(@NonNull Application application) {
        super(application);
        tweetRepositiory = new TweetRepositiory();
        tweets = tweetRepositiory.getAllTweets();
    }

    public LiveData<List<Tweet>> getTweets() { return tweets; }

    public void openDialogTweetMenu(Context ctx, int idTweet){
        BottomModalTweetFragment dialogTweet = BottomModalTweetFragment.newInstance(idTweet);
        dialogTweet.show(((AppCompatActivity)ctx).getSupportFragmentManager(), "BottomModalTweetFragment");
    }

    public LiveData<List<Tweet>> getFavTweets() {
        favTweets = tweetRepositiory.getFavsTweets();
        return favTweets;
    }

    public LiveData<List<Tweet>> getNewTweets() {
        tweets = tweetRepositiory.getAllTweets();
        return tweets;
    }

    public LiveData<List<Tweet>> getNewFavTweets() {
        getNewTweets();
        return getFavTweets();
    }

    public void insertTweet(String mensaje){
        tweetRepositiory.createTweet(mensaje);
    }

    public void deleteTweet(int idTweet){
        tweetRepositiory.deleteTweet(idTweet);
    }

    public void likeTweet(int idTweet){
        tweetRepositiory.likTweet(idTweet);
    }
}
