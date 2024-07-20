package com.project.ordernote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.project.ordernote.data.model.Users_Model;
import com.project.ordernote.data.repository.Login_Repository;

public class LoginViewModel extends AndroidViewModel {
    private final Login_Repository loginRepository;


    public LoginViewModel(@NonNull Application application) {
        super(application);
        loginRepository = new Login_Repository(application);
    }

    public LiveData<Users_Model> loginUser(String mobileNumber, String password) {
        return loginRepository.loginUser(mobileNumber, password);
    }
}
