package com.androidavanzado.minitwitter.data;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;


import com.androidavanzado.minitwitter.retrofit.request.RequestUserProfile;
import com.androidavanzado.minitwitter.retrofit.response.ResponseUserProfile;

public class ProfileViewModel extends AndroidViewModel {

    public ProfileRepositiory profileRepositiory;
    public LiveData<ResponseUserProfile> userProfile;
    public LiveData<String> photoProfile;

    public ProfileViewModel(@NonNull Application application){
        super(application);
        profileRepositiory = new ProfileRepositiory();
        userProfile = profileRepositiory.getProfile();
        photoProfile = profileRepositiory.getPhotoProfile();
    }

    public void updateProfile(RequestUserProfile requestUserProfile){
        profileRepositiory.updateProfile(requestUserProfile);
    }

    public void uploadPhoto(String photo){
        profileRepositiory.uploadPhoto(photo);
    }

}
