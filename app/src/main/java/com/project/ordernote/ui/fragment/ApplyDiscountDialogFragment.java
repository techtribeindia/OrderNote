package com.project.ordernote.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.project.ordernote.R;
import com.project.ordernote.databinding.FragmentAddMenuItemInCartBinding;
import com.project.ordernote.databinding.FragmentApplyDiscountDialogBinding;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ApplyDiscountDialogFragment extends DialogFragment {

    FragmentApplyDiscountDialogBinding binding;

    private OrderDetails_ViewModel ordersViewModel;



    public ApplyDiscountDialogFragment() {
        // Required empty public constructor
    }



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Removes default background

        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentApplyDiscountDialogBinding.inflate(inflater, container, false);

        return binding.getRoot();
       // return inflater.inflate(R.layout.fragment_apply_discount_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        try {
            ordersViewModel = new ViewModelProvider(requireActivity()).get(OrderDetails_ViewModel.class);
        } catch (Exception e) {
            e.printStackTrace();
        }

        binding.applyDiscountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{

                    String discountValue = binding.discountValueEdittext.getText().toString();
                    String numericPart = extractNumericValue(discountValue);
                    // Format the numeric value
                    double formatteddicountValue = Double.parseDouble(formatToTwoDecimalPlaces(numericPart));

                    if(formatteddicountValue > 0){
                        if(ordersViewModel.getSubTotalPrice()>= formatteddicountValue){
                            ordersViewModel.setDisountValue(formatteddicountValue);
                            dismiss();
                        }
                        else{
                            showSnackbar(requireView(), "Discount value should be  greater than or equals to subtotal value");

                        }

                    }
                    else{
                        showSnackbar(requireView(), "Please enter discount value before applying");

                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        binding.closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dismiss();
            }
        });


    }

    private static String extractNumericValue(String input) {
        // Regular expression to match the numeric part, including decimal point if present
        Pattern pattern = Pattern.compile("(\\d+\\.?\\d*)");
        Matcher matcher = pattern.matcher(input);
        if (matcher.find()) {
            return matcher.group(1);
        } else {
            return "0"; // Return "0" if no numeric part is found
        }
    }

    private static String formatToTwoDecimalPlaces(String value) {
        double numericValue = Double.parseDouble(value);
        DecimalFormat decimalFormat = new DecimalFormat("#.00");
        return decimalFormat.format(numericValue);
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
}