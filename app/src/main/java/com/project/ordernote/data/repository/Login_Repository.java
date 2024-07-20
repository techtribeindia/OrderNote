package com.project.ordernote.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.firestore.FirebaseFirestore;
import com.project.ordernote.data.model.Users_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.utils.SessionManager;

public class Login_Repository {
    private final FirestoreService firestoreService;
    private final SessionManager sessionManager;

    public Login_Repository(Context context) {
        firestoreService = new FirestoreService();
        sessionManager = new SessionManager(context);
    }

    public LiveData<Users_Model> loginUser(String mobileNumber, String password) {
        MutableLiveData<Users_Model> loginResult = new MutableLiveData<>();

        firestoreService.userDetailsFetch(mobileNumber, password, (isSuccess, message, userDocument) -> {

            if (isSuccess && userDocument != null) {
                // Process data
                sessionManager.setLogin(true);
                sessionManager.saveUserData(userDocument);
                loginResult.setValue(new Users_Model(true, message));
            } else {
                loginResult.setValue(new Users_Model(false, message));
            }
        });

        return loginResult;
    }
}