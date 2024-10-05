package com.project.ordernote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.ordernote.data.model.AppData_Model;
import com.project.ordernote.data.model.Users_Model;
import com.project.ordernote.data.repository.UserDetails_Repository;
import com.project.ordernote.utils.ApiResponseState_Enum;

public class UserDetailsViewModel extends AndroidViewModel {
    private final UserDetails_Repository loginRepository;
    private MutableLiveData<ApiResponseState_Enum<Users_Model>> userDetailsModelLiveData;


    public UserDetailsViewModel(@NonNull Application application) {
        super(application);
        loginRepository = new UserDetails_Repository(application);
    }

    public LiveData<Users_Model> loginUser(String mobileNumber, String password) {
        return loginRepository.loginUser(mobileNumber, password);
    }

    public LiveData<ApiResponseState_Enum<Users_Model>> getUserDetailsFromViewModel() {


        return userDetailsModelLiveData;
    }

    public void getUserDetailsFromRepository(String userMobileNumber) {
        userDetailsModelLiveData = (loginRepository.getUserDetails(userMobileNumber));
    }
}
