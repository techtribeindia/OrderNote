package com.project.ordernote.ui.fragment;

import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.project.ordernote.R;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.databinding.FragmentAddBuyerDetailsBinding;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.TextUtils;
import com.project.ordernote.viewmodel.Buyers_ViewModel;

import java.util.Objects;
import java.util.UUID;


public class AddBuyerDetails_DialogFragment extends DialogFragment {


    FragmentAddBuyerDetailsBinding binding;
    Handler mHandler;

    Buyers_ViewModel buyers_viewModel;

    boolean isAddBuyerCalled = false , isFragmentOpenedToAddNewBuyer = false , isUpdateBuyerCalled = false;
    String buyerkeyFromAnotherFragment = "";

    public AddBuyerDetails_DialogFragment() {
        // Required empty public constructor
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

    public static AddBuyerDetails_DialogFragment newInstance(String param1, String param2) {
        AddBuyerDetails_DialogFragment fragment = new AddBuyerDetails_DialogFragment();
         return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        if(!isAddBuyerCalled){

            super.onDismiss(dialog);

        }
        else{
            Snackbar.make(requireView(), "Please wait", Snackbar.LENGTH_LONG).show();

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
       // return inflater.inflate(R.layout.fragment_add_buyer_details_, container, false);
        binding = FragmentAddBuyerDetailsBinding.inflate(inflater, container, false);






        return binding.getRoot();


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        buyers_viewModel = new ViewModelProvider(requireActivity()).get(Buyers_ViewModel.class);

        Bundle bundle = getArguments();
        if (bundle != null) {
            String processtodo = bundle.getString(Constants.processtodo);

            if(Objects.requireNonNull(processtodo).equals(Constants.createNewBuyer)){
                binding.titleForDialog.setText(getString(R.string.add_new_buyer));
                binding.addBuyerButton.setText(getString(R.string.add));
                isFragmentOpenedToAddNewBuyer = true;
            }
            else  if(processtodo.equals(Constants.updateOldBuyer)){
                binding.titleForDialog.setText(getString(R.string.update_buyer));
                binding.addBuyerButton.setText(getString(R.string.update));
                buyerkeyFromAnotherFragment = bundle.getString(Constants.buyerkey);

                isFragmentOpenedToAddNewBuyer = false;
                updateUIWithSelecctedBuyerData(buyers_viewModel.getBuyerDataFromViewModelUsingBuyerKey(buyerkeyFromAnotherFragment));
            }



        }
        binding.closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDismiss(requireDialog());
            }
        });
        binding.addBuyerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Buyers_Model buyersModel = new Buyers_Model();

                if(!binding.buyernameEdittext.getText().toString().equals("")){
                    if(!binding.mobileNoEdittext.getText().toString().equals("")){
                        if(binding.mobileNoEdittext.getText().toString().length()==10) {
                            if (!binding.addressLine1Edittext.getText().toString().equals("")) {

                                buyersModel.setName(TextUtils.capitalizeWords(binding.buyernameEdittext.getText().toString()));
                                buyersModel.setMobileno(binding.mobileNoEdittext.getText().toString());
                                buyersModel.setAddress1(TextUtils.capitalizeWords(binding.addressLine1Edittext.getText().toString()));
                                buyersModel.setAddress2(TextUtils.capitalizeWords(binding.addressLine2Edittext.getText().toString()));
                                buyersModel.setPincode(binding.pincodeEdittext.getText().toString());
                                buyersModel.setGstin(binding.gstinEdittext.getText().toString());

                                buyersModel.setVendorkey("vendor_1");
                                buyersModel.setVendorName("Ponrathi Travels");
                                if(isFragmentOpenedToAddNewBuyer) {
                                    if(!buyers_viewModel.checkIfBuyerIsPresentInViewModelBuyerList(binding.mobileNoEdittext.getText().toString())){
                                        buyersModel.setUniquekey(String.valueOf(UUID.randomUUID()));
                                        callAddBuyerAndAddListener(buyersModel);

                                    }
                                    else{
                                        Snackbar.make(view, "Buyer with this mobile no is already added ", Snackbar.LENGTH_LONG).show();

                                    }

                                }
                                else{
                                    buyersModel.setUniquekey(String.valueOf(buyerkeyFromAnotherFragment));
                                    callUpdateBuyerAndAddListener(buyersModel);
                                }
                                        // Proceed with your logic (e.g., saving the buyer)



                            } else {
                                // Show Snackbar for missing address line 1
                                Snackbar.make(view, "Please enter Address Line 1", Snackbar.LENGTH_LONG).show();
                            }
                        } else {
                            // Show Snackbar for invalid mobile number length
                            Snackbar.make(view, "Please enter a valid 10-digit Mobile Number", Snackbar.LENGTH_LONG).show();
                        }
                    } else {
                        // Show Snackbar for missing mobile number
                        Snackbar.make(view, "Please enter Mobile Number", Snackbar.LENGTH_LONG).show();
                    }
                } else {
                    // Show Snackbar for missing buyer name
                    Snackbar.make(view, "Please enter Buyer Name", Snackbar.LENGTH_LONG).show();
                }
            }
        });

    }

    private void callUpdateBuyerAndAddListener(Buyers_Model buyersModel) {

        showProgressBar(true);
        if(isUpdateBuyerCalled){
            return;
        }

        isUpdateBuyerCalled = true;
        buyers_viewModel.updateBuyerInDB(buyersModel, new FirestoreService.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isUpdateBuyerCalled = false;

                buyers_viewModel.updateBuyerDataInViewModelAndLocalArray(buyersModel);
                showProgressBar(false);
                onDismiss(requireDialog());
                showSnackbar(requireView(),"Buyer updated successfully");
                sendHandlerMessage("updated","");
                //   neutralizeEveryVariableAndUI();
                // Handle success
                // e.g., show a success message or update the UI
            }

            @Override
            public void onFailure(Exception e) {
                isUpdateBuyerCalled = false;
                showProgressBar(false);
                showSnackbar(requireView(),e.getMessage());

                // Handle failure
                // e.g., show an error message
            }
        });



    }

    private void updateUIWithSelecctedBuyerData(Buyers_Model buyerDataFromViewModelUsingBuyerKey) {


        binding.buyernameEdittext.setText(TextUtils.capitalizeWords(buyerDataFromViewModelUsingBuyerKey.getName()));
        binding.mobileNoEdittext.setText(TextUtils.capitalizeWords(buyerDataFromViewModelUsingBuyerKey.getMobileno()));
        binding.pincodeEdittext.setText(TextUtils.capitalizeWords(buyerDataFromViewModelUsingBuyerKey.getPincode()));
        binding.addressLine1Edittext.setText(TextUtils.capitalizeWords(buyerDataFromViewModelUsingBuyerKey.getAddress1()));
        binding.addressLine2Edittext.setText(TextUtils.capitalizeWords(buyerDataFromViewModelUsingBuyerKey.getAddress2()));
        binding.gstinEdittext.setText(TextUtils.capitalizeWords(buyerDataFromViewModelUsingBuyerKey.getGstin()));


    }

    private void callAddBuyerAndAddListener(Buyers_Model buyersModel) {
        showProgressBar(true);
        if(isAddBuyerCalled){
            return;
        }

        isAddBuyerCalled = true;
        buyers_viewModel.addBuyerInDB(buyersModel, new FirestoreService.FirestoreCallback<Void>() {
            @Override
            public void onSuccess(Void result) {
                isAddBuyerCalled = false;

                buyers_viewModel.addBuyerDataItemInViewModelAndLocalArray(buyersModel);
                showProgressBar(false);
                onDismiss(requireDialog());
                showSnackbar(requireView(),"Buyer Added Sucessfully");
                sendHandlerMessage("added","");
             //   neutralizeEveryVariableAndUI();
                // Handle success
                // e.g., show a success message or update the UI
            }

            @Override
            public void onFailure(Exception e) {
                isAddBuyerCalled = false;
                showProgressBar(false);
                showSnackbar(requireView(),e.getMessage());

                // Handle failure
                // e.g., show an error message
            }
        });


    }




    private void showProgressBar(boolean show) {

        try {
            if (show) {
                binding.progressbar.playAnimation();
                binding.progressbar.setVisibility(View.VISIBLE);
                binding.progressbarBacklayout.setVisibility(View.VISIBLE);
            } else {
                binding.progressbar.cancelAnimation();
                binding.progressbar.setVisibility(View.GONE);
                binding.progressbarBacklayout.setVisibility(View.GONE);
            }
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


}