package com.androidavanzado.minitwitter.retrofit;

import com.androidavanzado.minitwitter.retrofit.request.RequestCreateTweet;
import com.androidavanzado.minitwitter.retrofit.request.RequestUserProfile;
import com.androidavanzado.minitwitter.retrofit.response.ResponseUserProfile;
import com.androidavanzado.minitwitter.retrofit.response.Tweet;
import com.androidavanzado.minitwitter.retrofit.response.TweetDeleted;
import com.androidavanzado.minitwitter.retrofit.response.ResponseUploadPhoto;

import java.util.List;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Part;

public interface AuthTwitterService {

    // Tweets
    @GET("tweets/all")
    Call<List<Tweet>> getAllTweets();

    @POST("tweets/create")
    Call<Tweet> createTweet(@Body RequestCreateTweet requestCreateTweet);

    // Método para like
    @POST("tweets/like/{idTweet}")
    Call<Tweet> likeTweet(@Path("idTweet") int idTweet);

    @DELETE("tweets/{idTweet}")
    Call<TweetDeleted> deleteTweet(@Path("idTweet") int idTweet);


    // Users
    @GET("users/profile")
    Call<ResponseUserProfile> getProfile();

    @PUT("users/profile")
    Call<ResponseUserProfile> updateProfile(@Body RequestUserProfile requestUserProfile);

    @Multipart
    @POST("users/uploadprofilephoto")
    Call<ResponseUploadPhoto> uploadProfilePhoto(@Part("file\"; filename=\"photo.jpeg\" ")RequestBody file);
}
