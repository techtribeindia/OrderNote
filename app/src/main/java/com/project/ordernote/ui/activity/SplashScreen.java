package com.project.ordernote.ui.activity;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.project.ordernote.R;
import com.project.ordernote.data.local.LocalDataManager;
import com.project.ordernote.data.model.AppData_Model;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.model.ItemDetails_Model;
import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.SessionManager;
import com.project.ordernote.viewmodel.AppData_ViewModel;
import com.project.ordernote.viewmodel.Buyers_ViewModel;
import com.project.ordernote.viewmodel.MenuItems_ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class SplashScreen extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_LENGTH = 2000;
    private SessionManager sessionManager;

    private Buyers_ViewModel buyersViewModel;
    private MenuItems_ViewModel menuItemViewModel;
    private AppData_ViewModel appDataViewModel;


    boolean gotbuyerData = false , gotMenuItemData = false , gotAppdata  = false;


    private Observer<ApiResponseState_Enum<List<Buyers_Model>>> buyerModelListObserver;
    private Observer<ApiResponseState_Enum<List<MenuItems_Model>>> menuItemListObserver;
    private Observer<ApiResponseState_Enum<AppData_Model>> appDataModelObserver;


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
        sessionManager = new SessionManager(this);


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                 // Check if the user is logged in
                if (sessionManager.isLoggedIn()) {
                    // Redirect to DashboardScreen if the user is logged in
                    Log.d("SplashScreen","Logged in");
                    // Initialize ViewModels
                    buyersViewModel = new ViewModelProvider(SplashScreen.this).get(Buyers_ViewModel.class);
                    menuItemViewModel = new ViewModelProvider(SplashScreen.this).get(MenuItems_ViewModel.class);
                    appDataViewModel = new ViewModelProvider(SplashScreen.this).get(AppData_ViewModel.class);
                    fetchInitialData();
                    // Fetch data
                    setObserver();

                    buyersViewModel.getBuyersListFromViewModel().observeForever(buyerModelListObserver);
                    menuItemViewModel.getMenuItemsFromViewModel().observeForever(menuItemListObserver);
                    appDataViewModel.getAppModelDataFromLiveModel().observeForever(appDataModelObserver);


                } else {
                    Log.d("SplashScreen","Not Logged in");

                    Intent intent = new Intent(SplashScreen.this, LoginScreen.class);
                    SplashScreen.this.startActivity(intent);
                    SplashScreen.this.finish();
                }


            }
        }, SPLASH_DISPLAY_LENGTH);







    }


    private void fetchInitialData() {
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
                                    Toast.makeText(SplashScreen.this, "Error in fetching Buyer 1 Splash ", Toast.LENGTH_SHORT).show();

                                }
                            } else {
                                Toast.makeText(SplashScreen.this, "Error in fetching Buyer 2 Splash ", Toast.LENGTH_SHORT).show();

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

        }
        catch (Exception e){
            e.printStackTrace();
        }

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
        if(gotbuyerData  && gotMenuItemData && gotAppdata){
            // Proceed to the next activity
            if(sessionManager.isLoggedIn()){
             if(sessionManager.getRole().equals(Constants.staff_role)||sessionManager.getRole().equals(Constants.admin_role)){
                 startActivity(new Intent(SplashScreen.this, Dashboard.class));

                 SplashScreen.this.finish();
             }
             else{

                 Toast.makeText(this, "Sorry, you don't have enough permission to access this app ", Toast.LENGTH_SHORT).show();

              }
            }
            else{

                Toast.makeText(this, "Sorry, you don't have enough permission to access this app ", Toast.LENGTH_SHORT).show();

            }

        }


    }
}