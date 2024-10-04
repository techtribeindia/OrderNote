package com.project.ordernote.ui.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;


import com.google.firebase.messaging.FirebaseMessaging;
import com.project.ordernote.R;
import com.project.ordernote.databinding.FragmentSettingsBinding;
import com.project.ordernote.ui.activity.LoginScreen;
import com.project.ordernote.utils.AlertDialogUtil;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.SessionManager;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    FragmentSettingsBinding binding;
    private SessionManager sessionManager;

    public SettingsFragment() {
        // Required empty public constructor
    }

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireActivity());
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentSettingsBinding.inflate(inflater, container, false);
        if(!Objects.equals(sessionManager.getRole(), Constants.admin_role))
        {
            binding.managemenuLayout.setVisibility(View.GONE);
        }
        if(sessionManager.getVUserName() != null)
        {
            binding.userName.setText(sessionManager.getVUserName());
        }
        else {
            binding.userName.setText(" ----- ");
        }

        if(sessionManager.getUserMobileNumber() != null)
        {
            binding.userMobileNumber.setText(sessionManager.getUserMobileNumber());
        }
        else {
            binding.userMobileNumber.setText(" ----- ");
        }

        if(sessionManager.getRole() != null)
        {
            binding.userRole.setText(sessionManager.getRole());
        }
        else {
            binding.userRole.setText(" ----- ");
        }
        try {
            PackageInfo pInfo = requireContext().getPackageManager().getPackageInfo(requireContext().getPackageName(), 0);
            String version = pInfo.versionName;
            binding.appversion.setText("Version: "+version);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        binding.logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                AlertDialogUtil.showCustomDialog(
                        requireActivity(),
                        "Logout",
                        "Do you want to logout from the device .", "Yes", "No", "RED",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Handle positive button click

                                logoutFun();


                            }
                        },
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Handle negative button click
                                dialog.dismiss();


                            }
                        },
                        new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialogInterface) {
                                dialogInterface.dismiss();

                            }
                        }
                );

            }
        });
        binding.dateWiseOrderScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DateWiseOrderScreenFragment dateWiseOrderScreenFragment = DateWiseOrderScreenFragment.newInstance();
                dateWiseOrderScreenFragment.show(getParentFragmentManager(),"");
            }
        });
        binding.manageMenuScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!sessionManager.getRole().equalsIgnoreCase(Constants.admin_role))
                {
                    showSnackbar(view,"Only admin have access to edit");
                    return;
                }
                ManageMenuScreenFragment manageMenuScreenFragment = ManageMenuScreenFragment.newInstance();
                manageMenuScreenFragment.show(getParentFragmentManager(),"");
            }
        });

        return binding.getRoot();
    }
    private void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("X", v -> snackbar.dismiss());
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent)); // optional: set the action color

        // Get the Snackbar's layout view
        View snackbarView = snackbar.getView();

        // Check if the parent of the snackbar view is a CoordinatorLayout
        if (snackbarView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            int marginInDp = (int) (30 * getResources().getDisplayMetrics().density); // Convert 20dp to pixels
            params.setMargins(0, marginInDp, 0, 0);
            snackbarView.setLayoutParams(params);
        } else if (snackbarView.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackbarView.getLayoutParams();
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            int marginInDp = (int) (30 * getResources().getDisplayMetrics().density); // Convert 20dp to pixels
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

    Intent intent  = new Intent(requireActivity(), LoginScreen.class);
    requireActivity().startActivity(intent);
    requireActivity().finish();
    sessionManager.logout();
}
}