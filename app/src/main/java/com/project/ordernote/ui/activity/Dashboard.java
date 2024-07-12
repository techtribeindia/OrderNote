package com.project.ordernote.ui.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.Menu;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.firebase.FirebaseApp;
import com.project.ordernote.R;
import com.project.ordernote.databinding.ActivityDashboardBinding;
import com.project.ordernote.ui.fragment.AddOrdersFragment;
import com.project.ordernote.ui.fragment.BuyersFragment;
import com.project.ordernote.ui.fragment.OrdersListFragment;
import com.project.ordernote.ui.fragment.ReportsFragment;
import com.project.ordernote.ui.fragment.SettingsFragment;
import com.project.ordernote.viewmodel.Dashboard_ViewModel;

public class Dashboard extends AppCompatActivity {
    ActivityDashboardBinding activityDashboardBinding;
     private Dashboard_ViewModel dashboardViewModel;


    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




        activityDashboardBinding = ActivityDashboardBinding.inflate(getLayoutInflater());
        setContentView(activityDashboardBinding.getRoot());
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
        if (savedInstanceState == null) {
            activityDashboardBinding.bottomNavigationView.setSelectedItemId(R.id.orderslist);
        }
        activityDashboardBinding.bottomNavigationView.setOnItemSelectedListener(item -> {

            Fragment selectedFragment = new Fragment();
            int id = item.getItemId();


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
            return true;
        });

        dashboardViewModel.getSelectedFragment().observe(this, fragment -> {
            if (fragment != null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.frame_layout, fragment)
                        .commit();
            }
        });



        activityDashboardBinding.addOrderFabButton.setOnClickListener(v -> {
            BottomNavigationItemView orderlist =activityDashboardBinding.bottomNavigationView. findViewById( R.id.orderslist);
            orderlist.setSelected(false);
            orderlist.clearAnimation();
            orderlist.clearFocus();

            dashboardViewModel.setSelectedFragment(new AddOrdersFragment());

           // activityDashboardBinding.bottomNavigationView.clearChildFocus(activityDashboardBinding.bottomNavigationView.getFocusedChild());
          //  activityDashboardBinding.bottomNavigationView.setActivated(false);
          //  activityDashboardBinding.bottomNavigationView.clearFocus();
            //activityDashboardBinding.bottomNavigationView.setSelected(false);
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