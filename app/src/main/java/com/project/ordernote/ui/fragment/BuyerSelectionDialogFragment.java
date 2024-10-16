package com.project.ordernote.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.project.ordernote.R;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.databinding.FragmentBuyerSelectionDialogBinding;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.viewmodel.Buyers_ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class BuyerSelectionDialogFragment extends DialogFragment {

    private FragmentBuyerSelectionDialogBinding binding;
    private Buyers_ViewModel buyerViewModel;
    private ArrayAdapter<String> buyerAdapter;
    Handler mHandler;
    private Buyers_Model buyersModel = new Buyers_Model();
    String address  = "";
    String selectedBuyerPositionString = "";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBuyerSelectionDialogBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
           /* Window window = getDialog().getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                // Set the dialog width to match the parent minus the margin
                int marginHorizontal = (int) getResources().getDimension(R.dimen.dialog_margin_horizontal);
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                window.setAttributes(params);
                window.setLayout(params.width - marginHorizontal * 2, WindowManager.LayoutParams.WRAP_CONTENT);
            }

            */
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Removes default background

        }
    }
    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }

    private void sendHandlerMessage(String bundlestr, String buyerKey ) {
        //Log.e(Constants.TAG, "createBillDetails in cartaItem 1");

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("fromadapter", bundlestr);
        bundle.putString("buyerkey", buyerKey);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        buyerViewModel = new ViewModelProvider(requireActivity()).get(Buyers_ViewModel.class);
             try{
                binding.buyerMobileNoTextview.setText(String.valueOf(Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getMobileno()));
                if(!buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getAddress1().equals("")){
                     address = Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getAddress1());
                }
                if(!buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getAddress2().equals("")){
                    if(!address.equals("")){
                        address = address +" , " +'\n'+Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getAddress2());
                    }
                    else{
                        address = Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getAddress2());

                    }
                }

                if(!buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getPincode().equals("")){
                    if(!address.equals("")){
                        address = address +" - " +'\n'+Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getPincode());
                    }
                    else{
                        address = Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getPincode());

                    }
                }
                binding.buyerAddressTextview.setText(String.valueOf(address));
                   // binding.buyerAddressTextview.setText(String.valueOf (Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getAddress1() +" , "+'\n'+Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getAddress2()+" - "+""+Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getPincode()+" . "));



            }
            catch (Exception e){
                e.printStackTrace();
            }
        // Observe the buyer list LiveData
        buyerViewModel.getBuyersListFromViewModel().observe(getViewLifecycleOwner(), buyerList -> {
            if (buyerList.data != null) {
                List<String> buyerNames = new ArrayList<>();
                for (Buyers_Model buyer : buyerList.data) {
                    buyerNames.add(buyer.getName());
                }
                buyerAdapter = new ArrayAdapter<>(requireContext(), android.R.layout.simple_dropdown_item_1line, buyerNames);
                binding.autoCompleteTextViewBuyer.setAdapter(buyerAdapter);
            }
        });

        buyerViewModel.getSelectedBuyersDetailsFromViewModel().observe(getViewLifecycleOwner(), buyerList -> {
           try {
               if (buyerList.getName() != null) {


                 //  binding.buyerAddressTextview.setText(String.valueOf (Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getAddress1() +" , "+'\n'+Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getAddress2()+" - "+""+Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getPincode()+" . "));

                    address = "";
                   try{
                       binding.buyerMobileNoTextview.setText(String.valueOf(Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getMobileno()));
                       binding.autoCompleteTextViewBuyer.setText(String.valueOf(Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getName()));
                       if(!buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getAddress1().equals("")){
                           address = Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getAddress1());
                       }
                       if(!buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getAddress2().equals("")){
                           if(!address.equals("")){
                               address = address +" , " +'\n'+Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getAddress2());
                           }
                           else{
                               address = Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getAddress2());

                           }
                       }

                       if(!buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getPincode().equals("")){
                           if(!address.equals("")){
                               address = address +" - " +'\n'+Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getPincode());
                           }
                           else{
                               address = Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getPincode());

                           }
                       }
                       binding.buyerAddressTextview.setText(String.valueOf(address));
                       // binding.buyerAddressTextview.setText(String.valueOf (Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getAddress1() +" , "+'\n'+Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getAddress2()+" - "+""+Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getPincode()+" . "));



                   }
                   catch (Exception e){
                       e.printStackTrace();
                   }
                   
                   int selectedBuyerPositionInt = 0;
                   try {
                       if (!Objects.equals(buyerViewModel.getSelectedBuyerPositionStringLiveData().getValue(), "")) {
                           selectedBuyerPositionInt = Integer.parseInt(buyerViewModel.getSelectedBuyerPositionStringLiveData().getValue());
                           binding.autoCompleteTextViewBuyer.setText(String.valueOf(Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getName()),false);

                       }
                   } catch (Exception e) {
                       e.printStackTrace();
                   }

               }
           }
           catch (Exception e){
               e.printStackTrace();
           }
        });


        // Handle AutoCompleteTextView selection
        binding.autoCompleteTextViewBuyer.setOnItemClickListener((parent, view1, position, id) -> {
            String selectedBuyer = (String) parent.getItemAtPosition(position);
            // Handle the selected buyer
            buyersModel = (buyerViewModel.getBuyerDataFromViewModelUsingBuyerName(selectedBuyer));
            binding.buyerMobileNoTextview.setText(String.valueOf(buyersModel.getMobileno()));
              address ="";
            try{
                binding.buyerMobileNoTextview.setText(String.valueOf(Objects.requireNonNull(buyersModel.getMobileno())));
                if(!buyersModel.getAddress1().equals("")){
                    address = Objects.requireNonNull(buyersModel.getAddress1());
                }
                if(!buyersModel.getAddress2().equals("")){
                    if(!address.equals("")){
                        address = address +" , " +'\n'+Objects.requireNonNull(buyersModel.getAddress2());
                    }
                    else{
                        address = Objects.requireNonNull(buyersModel.getAddress2());

                    }
                }
                if(!buyersModel.getPincode().equals("")){
                    if(!address.equals("")){
                        address = address +" - " +'\n'+Objects.requireNonNull(buyersModel.getPincode());
                    }
                    else{
                        address = Objects.requireNonNull(buyersModel.getPincode());

                    }
                }
                binding.buyerAddressTextview.setText(String.valueOf(address));
                // binding.buyerAddressTextview.setText(String.valueOf (Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getAddress1() +" , "+'\n'+Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getAddress2()+" - "+""+Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getPincode()+" . "));



            }
            catch (Exception e){
                e.printStackTrace();
            }
            binding.buyerAddressTextview.setText(String.valueOf(address));
          //  binding.buyerAddressTextview.setText(String.valueOf (buyersModel.getAddress1() +" , "+'\n'+buyersModel.getAddress2()+" - "+""+buyersModel.getPincode()+" . "));
            buyerViewModel.setSelectedBuyerPositionStringLiveData( String.valueOf(position));
            try {
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                Objects.requireNonNull(imm).hideSoftInputFromWindow(binding.autoCompleteTextViewBuyer.getWindowToken(), 0);
            }
            catch (Exception e){
                e.printStackTrace();
            }
           // Toast.makeText(requireContext(), "Selected: " + selectedBuyer, Toast.LENGTH_SHORT).show();
        });


        binding.selectBuyerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(buyersModel.getUniquekey().length()>0) {
                    buyerViewModel.setSelectedBuyerLiveData(buyersModel);

                    dismiss();
                }
                else {
                    showSnackbar(requireView(), "Please select a buyer");

                }
            }
        });

        binding.closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.addBuyerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendHandlerMessage("newbuyer","");
                dismiss();
            }
        });
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
