package com.project.ordernote.ui.activity;

import static com.project.ordernote.utils.Constants.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
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
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
import com.project.ordernote.R;
import com.project.ordernote.databinding.ActivityDashboardBinding;
import com.project.ordernote.ui.fragment.CreateOrderFragment;
import com.project.ordernote.ui.fragment.BuyersFragment;
import com.project.ordernote.ui.fragment.OrdersListFragment;
import com.project.ordernote.ui.fragment.ReportsFragment;
import com.project.ordernote.ui.fragment.SettingsFragment;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.SessionManager;
import com.project.ordernote.viewmodel.Dashboard_ViewModel;

public class Dashboard extends AppCompatActivity {
    ActivityDashboardBinding activityDashboardBinding;
     private Dashboard_ViewModel dashboardViewModel;

    private SessionManager sessionManager;

    @SuppressLint({"NonConstantResourceId", "UseCompatLoadingForColorStateLists"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(this);




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



        FirebaseMessaging.getInstance().subscribeToTopic(sessionManager.getRole()+"_"+sessionManager.getVendorkey())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        String msg = "Subscribed";
                        if (!task.isSuccessful()) {
                            msg = "Subscribe failed";
                        }
                        Log.d(TAG, msg);
                        Toast.makeText(Dashboard.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });


        dashboardViewModel = new ViewModelProvider(this).get(Dashboard_ViewModel.class);

        activityDashboardBinding.bottomNavigationView.setOnItemSelectedListener(item -> {
            if(sessionManager.getRole().equals(Constants.staff_role)||sessionManager.getRole().equals(Constants.admin_role)){
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
            }
            else{

                    Toast.makeText(this, "Sorry, you don't have enough permission to access this app ", Toast.LENGTH_SHORT).show();

                return false;
            }

        });

        activityDashboardBinding.addOrderFabButton.setOnClickListener(v -> {

            try {
                if(sessionManager.getRole().equals(Constants.admin_role)) {

                    Drawable drawable = activityDashboardBinding.addOrderFabButton.getDrawable();
                    Drawable wrappedDrawable = DrawableCompat.wrap(drawable);
                    DrawableCompat.setTint(wrappedDrawable, ContextCompat.getColor(Dashboard.this, R.color.red));


                    for (int i = 0; i < activityDashboardBinding.bottomNavigationView.getMenu().size(); i++) {
                        activityDashboardBinding.bottomNavigationView.getMenu().getItem(i).setChecked(false);
                    }
                    activityDashboardBinding.bottomNavigationView.setSelectedItemId(R.id.addOrders);

                    dashboardViewModel.setSelectedFragment(new CreateOrderFragment());
                }
                else{
                    showSnackbar(v,"Sorry you don't have permission to access place order screen");

                }
            }
            catch (Exception e){
                e.printStackTrace();
            }

        });

        if (savedInstanceState == null) {
            if(sessionManager.getRole().equals(Constants.staff_role)){
                activityDashboardBinding.bottomNavigationView.setSelectedItemId(R.id.orderslist);

            }
            else if(sessionManager.getRole().equals(Constants.admin_role)){
                activityDashboardBinding.bottomNavigationView.setSelectedItemId(R.id.addOrders);
                activityDashboardBinding.addOrderFabButton.performClick();
            }
            else{
                Toast.makeText(this, "Sorry, you don't have enough permission to access this app ", Toast.LENGTH_SHORT).show();
            }

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


    private void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);

        snackbar.setAnchorView(activityDashboardBinding.addOrderFabButton); // Set the FAB as the anchor view



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

    public FloatingActionButton getFabButton() {
        return activityDashboardBinding.addOrderFabButton;
    }

    private void replaceFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout, fragment);
        fragmentTransaction.commit();
    }



}



