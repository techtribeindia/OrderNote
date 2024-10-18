package com.project.ordernote.ui.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.project.ordernote.R;
import com.project.ordernote.data.local.LocalDataManager;
import com.project.ordernote.data.model.AppData_Model;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.data.model.Users_Model;
import com.project.ordernote.data.model.VendorDetails_Model;
import com.project.ordernote.data.remote.OrderDetails_BulkDelete;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.BaseActivity;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.SessionManager;
import com.project.ordernote.viewmodel.AppData_ViewModel;
import com.project.ordernote.viewmodel.Buyers_ViewModel;
import com.project.ordernote.viewmodel.MenuItems_ViewModel;
import com.project.ordernote.viewmodel.UserDetailsViewModel;
import com.project.ordernote.viewmodel.VendorDetails_ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SplashScreenActivity extends BaseActivity {
        private static final int SPLASH_DISPLAY_LENGTH = 2000;
        private SessionManager sessionManager;

        private Buyers_ViewModel buyersViewModel;
        private MenuItems_ViewModel menuItemViewModel;
        private AppData_ViewModel appDataViewModel;
        private UserDetailsViewModel userDetailsViewModel;
        private VendorDetails_ViewModel  vendorDetailsViewModel;


        boolean gotbuyerData = false , gotMenuItemData = false , gotAppdata  = false , gotUserData = false , gotVendorData = false , checkIfNeedToDeleteData = false;
        String vendorkeyBeforeCallingUserData = "";

        private Observer<ApiResponseState_Enum<List<Buyers_Model>>> buyerModelListObserver;
        private Observer<ApiResponseState_Enum<List<MenuItems_Model>>> menuItemListObserver;
        private Observer<ApiResponseState_Enum<AppData_Model>> appDataModelObserver;
        private Observer<ApiResponseState_Enum<Users_Model>> userDetailsModelListObserver;
        private Observer<ApiResponseState_Enum<VendorDetails_Model>> vendorDetailsModelListObserver;


        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.splashscreen);




            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.setStatusBarColor(ContextCompat.getColor(this, R.color.white));
            }

            // Optional: Make status bar icons dark if you have a light status bar color
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                View decor = getWindow().getDecorView();
                decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
            }
            // Initialize SessionManager
            sessionManager = new SessionManager(this, Constants.USERPREF_NAME);
            vendorkeyBeforeCallingUserData = sessionManager.getVendorkey();

            GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
            int result = googleApiAvailability.isGooglePlayServicesAvailable(getApplicationContext());
            if (result != ConnectionResult.SUCCESS) {
                if (googleApiAvailability.isUserResolvableError(result)) {
                    googleApiAvailability.getErrorDialog(this, result, 2404).show();
                } else {
                    Toast.makeText(this, "Error - Google Play Services not available", Toast.LENGTH_SHORT).show();

                }
            }



            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                     // Check if the user is logged in
                    if (sessionManager.isLoggedIn()) {
                        // Redirect to DashboardScreen if the user is logged in
                        Log.d("SplashScreenActivity","Logged in");
                        // Initialize ViewModels
                        buyersViewModel = new ViewModelProvider(SplashScreenActivity.this).get(Buyers_ViewModel.class);
                        menuItemViewModel = new ViewModelProvider(SplashScreenActivity.this).get(MenuItems_ViewModel.class);
                        appDataViewModel = new ViewModelProvider(SplashScreenActivity.this).get(AppData_ViewModel.class);
                        userDetailsViewModel =  new ViewModelProvider(SplashScreenActivity.this).get(UserDetailsViewModel.class);
                        vendorDetailsViewModel = new ViewModelProvider(SplashScreenActivity.this).get(VendorDetails_ViewModel.class);
                         fetchInitialData();
                        // Fetch data
                        setObserver();
                        userDetailsViewModel.getUserDetailsFromViewModel().observeForever(userDetailsModelListObserver);
                        buyersViewModel.getBuyersListFromViewModel().observeForever(buyerModelListObserver);
                        menuItemViewModel.getMenuItemsFromViewModel().observeForever(menuItemListObserver);
                        appDataViewModel.getAppModelDataFromLiveModel().observeForever(appDataModelObserver);
                        vendorDetailsViewModel.getVendorItemsFromViewModel().observeForever(vendorDetailsModelListObserver);


                    } else {
                        Log.d("SplashScreenActivity","Not Logged in");

                        Intent intent = new Intent(SplashScreenActivity.this, LoginScreen.class);
                        SplashScreenActivity.this.startActivity(intent);
                        SplashScreenActivity.this.finish();
                    }


                }
            }, SPLASH_DISPLAY_LENGTH);





            NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.cancelAll();
            }

        }


        private void fetchInitialData() {
            userDetailsViewModel.getUserDetailsFromRepository(sessionManager.getUserMobileNumber());
            vendorDetailsViewModel.FetchVendorItemByVendorKeyFromRepository(sessionManager.getVendorkey());

            buyersViewModel.getBuyersListFromRepository(sessionManager.getVendorkey());
            menuItemViewModel.FetchMenuItemByVendorKeyFromRepository(sessionManager.getVendorkey());
            appDataViewModel.FetchAppDataFromRepositoryAndSaveInLocalDataManager();


         /*   // Observe to determine when data fetching is complete
            buyersViewModel.getBuyersListFromViewModel().observe(this, buyersResponse -> {
                // Check if data is fetched successfully and proceed
                if (buyersResponse.status == ApiResponseState_Enum.Status.SUCCESS) {

                    LocalDataManager.getInstance().setBuyers(buyersResponse.data);

                    checkAndProceed();
                }
            });

            menuItemViewModel.getMenuItemsFromViewModel().observe(this, menuResponse -> {
                // Check if data is fetched successfully and proceed
                if (menuResponse.status == ApiResponseState_Enum.Status.SUCCESS) {
                    LocalDataManager.getInstance().setMenuItems(menuResponse.data);
                    checkAndProceed();
                }
            });


          */



        }



        private void setObserver() {
            try{

                buyerModelListObserver = new Observer<ApiResponseState_Enum<List<Buyers_Model>>> () {
                    @Override
                    public void onChanged(@Nullable ApiResponseState_Enum<List<Buyers_Model>> buyersResponse) {
                        // Update your UI or perform any actions based on the updated data

                        //  Toast.makeText(requireActivity(), "sizze from observer: "+String.valueOf(itemDetailsList.size()), Toast.LENGTH_SHORT).show();
                        if (Objects.requireNonNull(buyersResponse).status == ApiResponseState_Enum.Status.SUCCESS) {

                            LocalDataManager.getInstance().setBuyers(buyersResponse.data);
                            gotbuyerData = true;
                            checkAndProceed();
                        }
                        else{
                            if (Objects.requireNonNull(buyersResponse).status != ApiResponseState_Enum.Status.LOADING) {

                                if (buyersResponse.message != null) {
                                    if (buyersResponse.message.equals(Constants.noDataAvailable)) {
                                        LocalDataManager.getInstance().setBuyers(new ArrayList<>());
                                        gotbuyerData = true;
                                        checkAndProceed();
                                    } else {

                                        showSnackbar(getCurrentFocus(), buyersResponse.message);
                                    }
                                } else {

                                    showSnackbar(getCurrentFocus(), buyersResponse.message);
                                }

                            }
                        }

                    }
                };


                menuItemListObserver = new Observer<ApiResponseState_Enum<List<MenuItems_Model>>> () {
                    @Override
                    public void onChanged(@Nullable ApiResponseState_Enum<List<MenuItems_Model>> menuItemResponse) {
                        // Update your UI or perform any actions based on the updated data

                        //  Toast.makeText(requireActivity(), "sizze from observer: "+String.valueOf(itemDetailsList.size()), Toast.LENGTH_SHORT).show();
                        if (Objects.requireNonNull(menuItemResponse).status == ApiResponseState_Enum.Status.SUCCESS) {

                            LocalDataManager.getInstance().setMenuItems(menuItemResponse.data);
                            gotMenuItemData = true;
                            checkAndProceed();
                        }

                    }
                };


                appDataModelObserver = new Observer<ApiResponseState_Enum<AppData_Model>> () {
                    @Override
                    public void onChanged(@Nullable ApiResponseState_Enum<AppData_Model> appData_model) {
                        // Update your UI or perform any actions based on the updated data

                        //  Toast.makeText(requireActivity(), "sizze from observer: "+String.valueOf(itemDetailsList.size()), Toast.LENGTH_SHORT).show();

                        if (Objects.requireNonNull(appData_model).status == ApiResponseState_Enum.Status.SUCCESS) {

                            LocalDataManager.getInstance().setAppData_model(Objects.requireNonNull(appData_model).data);
                            gotAppdata = true;
                            checkAndProceed();
                        }

                    }
                };


                userDetailsModelListObserver = new Observer<ApiResponseState_Enum<Users_Model>> () {
                    @Override
                    public void onChanged(@Nullable ApiResponseState_Enum<Users_Model> users_Model) {
                        // Update your UI or perform any actions based on the updated data

                        //  Toast.makeText(requireActivity(), "sizze from observer: "+String.valueOf(itemDetailsList.size()), Toast.LENGTH_SHORT).show();

                        if (Objects.requireNonNull(users_Model).status == ApiResponseState_Enum.Status.SUCCESS) {

                           // sessionManager.saveUserDataUsingModel(users_Model.data);
                            //gotUserData = true;

                            if(vendorkeyBeforeCallingUserData.equals(Objects.requireNonNull(users_Model).data.getVendorkey())) {

                                gotUserData = true;


                                checkAndProceed();
                            }
                            else{
                                gotUserData = false;
                                logoutFun();
                            }
                         }

                    }
                };

                vendorDetailsModelListObserver = new Observer<ApiResponseState_Enum<VendorDetails_Model>>() {
                    @Override
                    public void onChanged(ApiResponseState_Enum<VendorDetails_Model> vendorDetails_livedata) {

                        if (Objects.requireNonNull(vendorDetails_livedata).status == ApiResponseState_Enum.Status.SUCCESS) {

                            LocalDataManager.getInstance().setVendorDetails_model(Objects.requireNonNull(vendorDetails_livedata).data);
                            gotVendorData = true;

                            // Create input data for the worker
                            Data inputData = new Data.Builder()
                                    .putInt("orderExpiryDays", Objects.requireNonNull(vendorDetails_livedata).data.getOrderExpiryDays())
                                    .putInt("orderDeletionIntervalDays", Objects.requireNonNull(vendorDetails_livedata).data.getOrderdeletionintervaldays())
                                    .putLong("lastlyTriggeredOn", Objects.requireNonNull(vendorDetails_livedata).data.getOrderdeletiontriggeredon().toDate().getTime()) // Convert to millis
                                    .putString("vendorkey", Objects.requireNonNull(vendorDetails_livedata).data.getVendorkey())
                                    .build();

                            // Create a one-time work request
                            OneTimeWorkRequest workRequest = new OneTimeWorkRequest.Builder(OrderDetails_BulkDelete.class)
                                    .setInputData(inputData)
                                    .build();

                            // Enqueue the work request
                            WorkManager.getInstance(getApplicationContext()).enqueue(workRequest);


                            // OrderDetails_BulkDelete orderDetailsBulkDelete = new OrderDetails_BulkDelete();
                           // orderDetailsBulkDelete.checkIfWeNeedToTriggerDeleteFunction(Objects.requireNonNull(vendorDetails_livedata).data.getOrderExpiryDays() ,Objects.requireNonNull(vendorDetails_livedata).data.getOrderdeletionintervaldays() , Objects.requireNonNull(vendorDetails_livedata).data.getOrderdeletiontriggeredon() , Objects.requireNonNull(vendorDetails_livedata).data.getVendorkey() );
                            checkAndProceed();

                        }


                    }
                };

            }
            catch (Exception e){
                e.printStackTrace();
            }

        }


        private void showSnackbar(View view, String message) {
            Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
            snackbar.setAction("X", v -> snackbar.dismiss());
            snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent)); // optional: set the action color

            // Get the Snackbar's layout view
            View snackbarView = snackbar.getView();

            // Check if the parent is CoordinatorLayout
            if (snackbarView.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
                CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackbarView.getLayoutParams();
                params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                int marginInDp = (int) (30 * getResources().getDisplayMetrics().density); // Convert 30dp to pixels
                params.setMargins(0, marginInDp, 0, 0);
                snackbarView.setLayoutParams(params);
            } else if (snackbarView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
                // If it's a FrameLayout, handle it like before
                FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
                params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
                int marginInDp = (int) (30 * getResources().getDisplayMetrics().density); // Convert 30dp to pixels
                params.setMargins(0, marginInDp, 0, 0);
                snackbarView.setLayoutParams(params);
            }

            snackbar.show();
        }

    public void  logoutFun()
    {
        FirebaseMessaging.getInstance().unsubscribeFromTopic(sessionManager.getRole()+"_"+sessionManager.getVendorkey())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Unsubscribed";
                        if (!task.isSuccessful()) {
                            msg = "Unsubscribe failed";
                        }


                    }
                });

        Intent intent  = new Intent(SplashScreenActivity.this, LoginScreen.class);
         startActivity(intent);
         finish();
        sessionManager.logout();
    }
        @Override
        public void onDestroy() {
            super.onDestroy();
            try {
                if(buyersViewModel!=null){
                    buyersViewModel.getBuyersListFromViewModel().removeObserver(buyerModelListObserver);
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{
                if(menuItemViewModel!=null) {
                    menuItemViewModel.getMenuItemsFromViewModel().removeObserver(menuItemListObserver);
                }
            }
            catch (Exception e ) {
                e.printStackTrace();
            }
            try{
                if(appDataViewModel!=null) {
                    appDataViewModel.getAppModelDataFromLiveModel().removeObserver(appDataModelObserver);
                }
            }
            catch (Exception e ) {
                e.printStackTrace();
            }


        }
        private void checkAndProceed() {
            // Assuming both data fetches are done
            if(gotbuyerData  && gotMenuItemData && gotAppdata && gotUserData && gotVendorData){
                // Proceed to the next activity
                if(sessionManager.isLoggedIn()){
                 //   Toast.makeText(this, "Role: "+sessionManager.getRole(), Toast.LENGTH_SHORT).show();

                    if(sessionManager.getRole().equals(Constants.staff_role)||sessionManager.getRole().equals(Constants.admin_role)){
                     startActivity(new Intent(SplashScreenActivity.this, Dashboard.class));

                     SplashScreenActivity.this.finish();
                 }
                 else{

                     Toast.makeText(this, "Sorry, you don't have enough permission to access this app ", Toast.LENGTH_SHORT).show();

                  }
                }
                else{

                    Intent intent = new Intent(SplashScreenActivity.this, LoginScreen.class);
                    SplashScreenActivity.this.startActivity(intent);
                    SplashScreenActivity.this.finish();
                    Toast.makeText(this, "Sorry, you don't have enough permission , Please Login again ", Toast.LENGTH_SHORT).show();

                }

            }
        }
}