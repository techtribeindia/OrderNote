package com.project.ordernote.data.repository;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.project.ordernote.data.model.Users_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.SessionManager;

public class UserDetails_Repository {
    private final FirestoreService firestoreService;
    private final SessionManager sessionManager;

    public UserDetails_Repository(Context context) {
        firestoreService = new FirestoreService();
        sessionManager = new SessionManager(context, Constants.USERPREF_NAME);
    }

    public LiveData<Users_Model> loginUser(String mobileNumber, String password) {
        MutableLiveData<Users_Model> loginResult = new MutableLiveData<>();

        firestoreService.userDetailsFetchAndCheckUserStatus(mobileNumber, password, (isSuccess, message, userDocument) -> {

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

    public MutableLiveData<ApiResponseState_Enum<Users_Model>> getUserDetails(String userMobileNumber) {

        MutableLiveData<ApiResponseState_Enum<Users_Model>> livedata = new MutableLiveData<>();
        livedata.postValue(ApiResponseState_Enum.loading(null));

        firestoreService.getUserDetails(userMobileNumber, (isSuccess, message, userDocument) -> {

            if (isSuccess && userDocument != null) {
                // Process data
                 sessionManager.saveUserData(userDocument);
                 Users_Model usersModel = userDocument.toObject(Users_Model.class);

                livedata.postValue(ApiResponseState_Enum.successwithmessage(usersModel, "There is Orders for the selected status"));
            } else {

            }
        });

        return livedata;
    }
}