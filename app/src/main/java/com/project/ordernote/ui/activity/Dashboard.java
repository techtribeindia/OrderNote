package com.project.ordernote.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.project.ordernote.R;
import com.project.ordernote.databinding.ActivityDashboardBinding;
import com.project.ordernote.ui.fragment.CreateOrderFragment;
import com.project.ordernote.ui.fragment.BuyersFragment;
import com.project.ordernote.ui.fragment.OrdersListFragment;
import com.project.ordernote.ui.fragment.ReportsFragment;
import com.project.ordernote.ui.fragment.SettingsFragment;
import com.project.ordernote.viewmodel.Dashboard_ViewModel;

public class Dashboard extends AppCompatActivity {
    ActivityDashboardBinding activityDashboardBinding;
     private Dashboard_ViewModel dashboardViewModel;


    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForColorStateLists"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(activityDashboardBinding.getRoot());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(this, R.color.reddishgrey));
        }

        // Optional: Make status bar icons dark if you have a light status bar color
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }

      //  replaceFragment(new OrdersListFragment());
      /*  activityDashboardBinding.bottomNavigationView.setBackground(null);

        activityDashboardBinding.bottomNavigationView.setOnItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.orderslist) {
                replaceFragment(new OrdersListFragment());
            } else if (id == R.id.buyerslist) {
                replaceFragment(new BuyersFragment());
            } else if (id == R.id.settings) {
                replaceFragment(new SettingsFragment());
            } else if (id == R.id.reports) {
                replaceFragment(new ReportsFragment());
            } else {
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
            }
            return true;
        });


       */


         dashboardViewModel = new ViewModelProvider(this).get(Dashboard_ViewModel.class);

        activityDashboardBinding.bottomNavigationView.setOnItemSelectedListener(item -> {

            Fragment selectedFragment = new Fragment();
            int id = item.getItemId();
                //aDDED BY ARUN USING GIT

            if (id == R.id.orderslist) {
                selectedFragment = (new OrdersListFragment());
            } else if (id == R.id.buyerslist) {
                selectedFragment = (new BuyersFragment());
            } else if (id == R.id.settings) {
                selectedFragment = (new SettingsFragment());
            } else if (id == R.id.reports) {
                selectedFragment = (new ReportsFragment());
            } else {
                selectedFragment = null;
                throw new IllegalStateException("Unexpected value: " + item.getItemId());
            }
            dashboardViewModel.setSelectedFragment(selectedFragment);
          //  activityDashboardBinding.addOrderFabButton.setBackgroundTintList(getResources().getColorStateList(R.color.darkgrey));
            Drawable drawable = activityDashboardBinding.addOrderFabButton.getDrawable();
            Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(Dashboard.this, R.color.darkgrey));

            return true;
        });


        activityDashboardBinding.addOrderFabButton.setOnClickListener(v -> {

            try {
                Drawable drawable = activityDashboardBinding.addOrderFabButton.getDrawable();
                Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
                DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(Dashboard.this, R.color.red));


                for (int i = 0; i < activityDashboardBinding.bottomNavigationView.getMenu().size(); i++) {
                    activityDashboardBinding.bottomNavigationView.getMenu().getItem(i).setChecked(false);
                }
                activityDashboardBinding.bottomNavigationView.setSelectedItemId(R.id.addOrders);

                dashboardViewModel.setSelectedFragment(new CreateOrderFragment());

            }
            catch (Exception e){
                e.printStackTrace();
            }

        });

        if (savedInstanceState == null) {
            activityDashboardBinding.bottomNavigationView.setSelectedItemId(R.id.addOrders);
            activityDashboardBinding.addOrderFabButton.performClick();
        }
        dashboardViewModel.getSelectedFragment().observe(this, fragment -> {
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .commit();
            }
        });


        // Set the default fragment


    }


    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }



}
