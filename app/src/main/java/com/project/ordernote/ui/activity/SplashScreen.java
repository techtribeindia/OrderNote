package com.project.ordernote.ui.activity;

import androidx.appcompat.app.AppCompatActivity;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.project.ordernote.R;
import com.project.ordernote.data.local.LocalDataManager;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.SessionManager;
import com.project.ordernote.viewmodel.Buyers_ViewModel;
import com.project.ordernote.viewmodel.MenuItems_ViewModel;


public class SplashScreen extends AppCompatActivity {
    private static final int SPLASH_DISPLAY_LENGTH = 2000;
    private SessionManager sessionManager;

    private Buyers_ViewModel buyersViewModel;
    private MenuItems_ViewModel menuItemViewModel;


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



        // Initialize ViewModels
        buyersViewModel = new ViewModelProvider(this).get(Buyers_ViewModel.class);
        menuItemViewModel = new ViewModelProvider(this).get(MenuItems_ViewModel.class);

        // Fetch data
        fetchInitialData();


/*

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent;
                // Check if the user is logged in
                if (sessionManager.isLoggedIn()) {
                    // Redirect to DashboardScreen if the user is logged in
                    intent = new Intent(SplashScreen.this, Dashboard.class);
                } else {
                    // Redirect to LoginScreen if the user is not logged in
                    intent = new Intent(SplashScreen.this, LoginScreen.class);
                }

                SplashScreen.this.startActivity(intent);
                SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

 */
    }


    private void fetchInitialData() {
        buyersViewModel.getBuyersListFromRepository("vendor_1");
        menuItemViewModel.fetchMenuItemsByVendorKey("vendor_1");





        // Observe to determine when data fetching is complete
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
    }

    private void checkAndProceed() {
        // Assuming both data fetches are done
        if (buyersViewModel.getBuyersListFromViewModel().getValue() != null &&
                menuItemViewModel.getMenuItemsFromViewModel().getValue() != null) {
            // Proceed to the next activity
            startActivity(new Intent(SplashScreen.this, Dashboard.class));
            finish();
        }

    }
}