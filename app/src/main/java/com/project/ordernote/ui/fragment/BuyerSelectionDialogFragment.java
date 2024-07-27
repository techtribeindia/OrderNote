package com.project.ordernote.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.project.ordernote.R;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.databinding.FragmentBuyerSelectionDialogBinding;
import com.project.ordernote.viewmodel.Buyers_ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


public class BuyerSelectionDialogFragment extends DialogFragment {

    private FragmentBuyerSelectionDialogBinding binding;
    private Buyers_ViewModel buyerViewModel;
    private ArrayAdapter<String> buyerAdapter;

    private Buyers_Model buyersModel = new Buyers_Model();

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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize ViewModel
        buyerViewModel = new ViewModelProvider(requireActivity()).get(Buyers_ViewModel.class);
            try{
                binding.buyerMobileNoTextview.setText(String.valueOf(Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getMobileno()));
                binding.buyerAddressTextview.setText(String.valueOf (Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getAddress1() +" , "+'\n'+Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getAddress2()+" - "+""+Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getPincode()+" . "));

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
                   binding.buyerMobileNoTextview.setText(String.valueOf(Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getMobileno()));
                   binding.buyerAddressTextview.setText(String.valueOf (Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getAddress1() +" , "+'\n'+Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getAddress2()+" - "+""+Objects.requireNonNull(buyerViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getPincode()+" . "));
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
            binding.buyerAddressTextview.setText(String.valueOf (buyersModel.getAddress1() +" , "+'\n'+buyersModel.getAddress2()+" - "+""+buyersModel.getPincode()+" . "));
            buyerViewModel.setSelectedBuyerPositionStringLiveData( String.valueOf(position));
            Toast.makeText(requireContext(), "Selected: " + selectedBuyer, Toast.LENGTH_SHORT).show();
        });


        binding.selectBuyerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buyerViewModel.setSelectedBuyerLiveData(buyersModel);
                dismiss();
            }
        });

        binding.closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
    }
}
