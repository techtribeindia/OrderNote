package com.project.ordernote.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.project.ordernote.data.local.LocalDataManager;
import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.databinding.FragmentAddMenuItemInCartBinding;
import com.project.ordernote.ui.adapter.AutoCompleteMenuItemAdapter;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.WeightConverter;
import com.project.ordernote.utils.calculations.MenuItemValueCalculator;
import com.project.ordernote.viewmodel.MenuItems_ViewModel;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class AddMenuItem_InCart_Fragment extends DialogFragment {

    FragmentAddMenuItemInCartBinding binding;




    private Observer<MenuItems_Model> selectedMenuItemObserver;


    MenuItems_ViewModel menuItemsViewModel;
    OrderDetails_ViewModel orderDetails_viewModel;

    boolean menuItemFetchedSuccesfully = false;
    private AutoCompleteMenuItemAdapter menuItemAdapter;

    boolean price_settedFromObserver = false , weight_settedFromObserver = false , needToUpdateUI_inObserver = false;


    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));



    public AddMenuItem_InCart_Fragment() {
        // Required empty public constructor
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
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        try {
            menuItemsViewModel = new ViewModelProvider(requireActivity()).get(MenuItems_ViewModel.class);



            List<MenuItems_Model> menuItemsList = LocalDataManager.getInstance().getMenuItem();

            if(menuItemsList.size() > 0){
                menuItemsViewModel.setMenuListinMutableLiveData(menuItemsList);
            }
            else{
                menuItemsViewModel.FetchMenuItemByVendorKeyFromRepository("vendor_1");
            }


            // menuItemsViewModel = new ViewModelProvider(this).get(MenuItems_ViewModel.class);

        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            orderDetails_viewModel = new ViewModelProvider(requireActivity()).get(OrderDetails_ViewModel.class);




        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_add_menu_item__in_cart_, container, false);
        binding = FragmentAddMenuItemInCartBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        menuItemsViewModel.getMenuItemsFromViewModel().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        menuItemFetchedSuccesfully = false;
                        showProgressBar(true);
                        Toast.makeText(requireActivity(), "Loading Menu", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        menuItemFetchedSuccesfully = true;
                        showProgressBar(false);
                        Toast.makeText(requireActivity(), "Success in fetching menu", Toast.LENGTH_SHORT).show();

                        setAdapterForAutoCompleteWithMenuItem(resource.data);
                        break;
                    case ERROR:
                        Toast.makeText(requireActivity(), "Error in fetching menu", Toast.LENGTH_SHORT).show();
                        menuItemFetchedSuccesfully = false;
                        showProgressBar(false);
                        break;
                }
            }
        });

        setObserver();
        menuItemsViewModel.getSelectedMenuItemsFromViewModel().observeForever(selectedMenuItemObserver);
        binding.closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });
        binding.priceEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!price_settedFromObserver) {
                    try {
                        MenuItems_Model menuItemsModel = menuItemsViewModel.getSelectedMenuItemsFromViewModel().getValue();
                        String priceperitem = String.valueOf(binding.priceEditText.getText().toString());
                        double priceperitem_double = 0;
                        try {
                            String digitsOnlytext = priceperitem.replaceAll("[^0-9][^\\d.]", "");
                            priceperitem_double = Double.parseDouble(digitsOnlytext);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }


                        if (Objects.requireNonNull(menuItemsModel).getItemtype().equals(Constants.priceperkg_pricetype)) {

                            menuItemsModel.setPriceperkg(priceperitem_double);

                        } else if (menuItemsModel.getItemtype().equals(Constants.unitprice_pricetype)) {
                            menuItemsModel.setUnitprice(priceperitem_double);
                        }
                        menuItemsViewModel.updateSelectedMenuItemModel(menuItemsModel);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    price_settedFromObserver = false;
                }

            }
        });


        binding.weightEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if(!weight_settedFromObserver) {
                    try {
                        String weight = String.valueOf(binding.weightEditText.getText().toString());
                        double weight_double = 0;
                        try {
                            String digitsOnlytext = weight.replaceAll("[^0-9][^\\d.]", "");
                            weight_double = Double.parseDouble(digitsOnlytext);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        MenuItems_Model menuItemsModel = menuItemsViewModel.getSelectedMenuItemsFromViewModel().getValue();
                        Objects.requireNonNull(menuItemsModel).setGrossweight(Double.parseDouble(WeightConverter.ConvertKilogramstoGrams(String.valueOf(weight_double))));
                        menuItemsViewModel.updateSelectedMenuItemModel(menuItemsModel);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                else{
                    weight_settedFromObserver = false;
                }

            }
        });

        binding.plusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String quantity = String.valueOf(binding.quantityTextview.getText().toString());
                    double quantity_double = 0 ;
                    String digitsOnlytext = quantity.replaceAll("[^0-9]", "");
                    quantity_double = Double.parseDouble(digitsOnlytext);
                    quantity_double = quantity_double + 1 ;

                    binding.quantityTextview.setText(String.valueOf((int)quantity_double));
                    try {
                        MenuItems_Model menuItemsModel = menuItemsViewModel.getSelectedMenuItemsFromViewModel().getValue();
                        menuItemsModel.setQuantity(quantity_double);
                        menuItemsViewModel.updateSelectedMenuItemModel(menuItemsModel);
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                    try {

                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }

                }
                catch (Exception e ){
                    e.printStackTrace();
                }

            }
        });

        binding.minusIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    String quantity = String.valueOf(binding.quantityTextview.getText().toString());
                    double quantity_double = 0 ;
                    String digitsOnlytext = quantity.replaceAll("[^0-9]", "");
                    quantity_double = Double.parseDouble(digitsOnlytext);

                    if(quantity_double > 1) {
                        quantity_double = quantity_double - 1;

                        binding.quantityTextview.setText(String.valueOf((int)quantity_double));


                        try {
                            MenuItems_Model menuItemsModel = menuItemsViewModel.getSelectedMenuItemsFromViewModel().getValue();
                            menuItemsModel.setQuantity(quantity_double);
                            menuItemsViewModel.updateSelectedMenuItemModel(menuItemsModel);
                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }

                        try {
                         } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else{
                        Toast.makeText(requireActivity(), "Can't reduce quantity less than 1", Toast.LENGTH_SHORT).show();
                    }

                }
                catch (Exception e ){
                    e.printStackTrace();
                }
            }
        });

        binding.addInCartButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if(!Objects.equals(Objects.requireNonNull(menuItemsViewModel.getSelectedMenuItemsFromViewModel().getValue()).getItemkey(), "")){
                        orderDetails_viewModel.addItemInCart(menuItemsViewModel.getSelectedMenuItemsFromViewModel().getValue());
                        dismiss();

                        menuItemsViewModel.updateSelectedMenuItemModel(new MenuItems_Model());
                    }
                    else{
                        Toast.makeText(requireActivity(), "Please select an menu item from the list", Toast.LENGTH_SHORT).show();
                    }


                 }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


    }

    private void getCurrentKgAndPriceValue_and_Call_Calculation() {

        try{
            MenuItems_Model newMenuItemModel = menuItemsViewModel.getSelectedMenuItemsFromViewModel() .getValue();
            String quantity = String.valueOf(binding.quantityTextview.getText().toString());
            String priceperitem = String.valueOf(binding.priceEditText.getText().toString());
            double priceperitem_double = 0 , weight_double = 0 , quantity_double = 0  ;

           /* try{
                String digitsOnlytext = priceperitem.replaceAll("[^0-9]", "");
                priceperitem_double = Double.parseDouble(digitsOnlytext);
            }
            catch (Exception e ){
                e.printStackTrace();
            }
            try{
                String digitsOnlytext = quantity.replaceAll("[^0-9]", "");
                quantity_double = Double.parseDouble(digitsOnlytext);
            }
            catch (Exception e ){
                e.printStackTrace();
            }


           // newMenuItemModel.setQuantity((int)quantity_double);

            if (menuItemsViewModel.getSelectedMenuItemsFromViewModel().getValue().getItemtype().equals(Constants.priceperkg_pricetype)) {
                String weight = String.valueOf(binding.weightEditText.getText().toString());

                try{
                    String digitsOnlytext = weight.replaceAll("[^0-9.]", "");
                    weight_double = Double.parseDouble(digitsOnlytext);
                }
                catch (Exception e ){
                    e.printStackTrace();
                }
               // newMenuItemModel.setGrossweightAndConvertItToGrams(weight_double);
               // newMenuItemModel.setPriceperkg(priceperitem_double);


            }
            else if (menuItemsViewModel.getSelectedMenuItemsFromViewModel().getValue().getItemtype().equals(Constants.unitprice_pricetype)) {
              //  newMenuItemModel.setUnitprice(priceperitem_double);

            }
            else {

            }

            */

            double itemprice = MenuItemValueCalculator.calculateItemtotalPrice(newMenuItemModel);
            binding.finalPriceTextview.setText(String.valueOf(currencyFormat.format(itemprice)));

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setAdapterForAutoCompleteWithMenuItem(List<MenuItems_Model> data) {

        if (menuItemAdapter == null) {

            menuItemAdapter = new AutoCompleteMenuItemAdapter (requireContext(), data);
            menuItemAdapter.setHandler(newHandler());
            binding.autoCompleteTextViewMenuItem.setAdapter(menuItemAdapter);
        }


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




    private Handler newHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String data = bundle.getString("fromadapter");


                if(data.equals("closemenuitemadapter")){
                    String menuitemkey = bundle.getString("menuitemkey");

                    needToUpdateUI_inObserver = true;

                         try {

                             menuItemsViewModel.getMenuItemForMenuItemKeyFromViewModel(menuitemkey);

                             binding.autoCompleteTextViewMenuItem.clearFocus();

                            binding.autoCompleteTextViewMenuItem.dismissDropDown();
                            InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                            Objects.requireNonNull(imm).hideSoftInputFromWindow(binding.autoCompleteTextViewMenuItem.getWindowToken(), 0);

                        }
                        catch (Exception e){
                            e.printStackTrace();
                        }


                }

                return false;
            }
        };
        return new Handler(callback);
    }

    private void setObserver() {
        try{

            selectedMenuItemObserver = new Observer<MenuItems_Model>() {
                @Override
                public void onChanged(@Nullable MenuItems_Model menuItemsModel) {
                    // Update your UI or perform any actions based on the updated data
                    if(needToUpdateUI_inObserver) {
                        try {
                            price_settedFromObserver = true;
                            weight_settedFromObserver = true;
                            if (menuItemsModel.getItemtype().equals(Constants.unitprice_pricetype)) {
                                binding.weightEditText.setVisibility(View.GONE);
                                binding.weightTextView.setVisibility(View.VISIBLE);
                                binding.priceperitemorunitpricelabel.setText("Price Per Unit");
                                binding.priceEditText.setText(String.valueOf(menuItemsModel.getUnitprice()));
                                binding.finalPriceTextview.setText(String.valueOf(currencyFormat.format(menuItemsModel.getUnitprice())));

                                if (!Objects.equals(String.valueOf(menuItemsModel.getGrossweight()), "")) {
                                    binding.weightTextView.setText(String.valueOf(menuItemsModel.getGrossweight()));
                                } else if (!String.valueOf(menuItemsModel.getPortionsize()).equals("")) {
                                    binding.weightTextView.setText(String.valueOf(menuItemsModel.getPortionsize()));

                                } else if (!String.valueOf(menuItemsModel.getNetweight()).equals("")) {
                                    binding.weightTextView.setText(String.valueOf(menuItemsModel.getNetweight()));

                                } else {
                                    binding.weightTextView.setText(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(menuItemsModel.getGrossweight()))));

                                }
                                getCurrentKgAndPriceValue_and_Call_Calculation();

                            }
                            else if (menuItemsModel.getItemtype().equals(Constants.priceperkg_pricetype)) {
                                binding.weightEditText.setVisibility(View.VISIBLE);
                                binding.weightTextView.setVisibility(View.GONE);
                                binding.weightEditText.setText(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(menuItemsModel.getGrossweight()))));
                                binding.priceperitemorunitpricelabel.setText("Price Per Kg");
                                binding.priceEditText.setText(String.valueOf(menuItemsModel.getPriceperkg()));
                                binding.finalPriceTextview.setText(String.valueOf(currencyFormat.format(menuItemsModel.getPriceperkg())));
                                getCurrentKgAndPriceValue_and_Call_Calculation();


                            }
                            else {
                                binding.weightEditText.setVisibility(View.VISIBLE);
                                binding.weightTextView.setVisibility(View.GONE);
                                binding.priceEditText.setText(String.valueOf(menuItemsModel.getPriceperkg()));
                                binding.finalPriceTextview.setText(String.valueOf(currencyFormat.format(menuItemsModel.getPriceperkg())));

                                binding.priceperitemorunitpricelabel.setText("Price Per Kg");

                            }

                            try {
                                binding.quantityTextview.setText(String.valueOf(1));
                                binding.autoCompleteTextViewMenuItem.setText(String.valueOf(menuItemsModel.getItemname()));

                                binding.autoCompleteTextViewMenuItem.dismissDropDown();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            needToUpdateUI_inObserver = false;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    else{

                        getCurrentKgAndPriceValue_and_Call_Calculation();
                    }
                }
            };

      /*  menuItemsViewModel.getSelectedMenuItemsFromViewModel().observe(getViewLifecycleOwner() , menuItemsModel ->{

            try{

                if(menuItemsModel.getItemtype().equals(Constants.unitprice_pricetype)){
                    binding.weightEditText.setVisibility(View.GONE);
                    binding.weightTextView.setVisibility(View.VISIBLE);
                    binding.priceperitemorunitpricelabel.setText("Price Per Unit");
                    binding.priceEditText.setText(String.valueOf(menuItemsModel.getUnitprice()));
                    binding.finalPriceTextview.setText(String.valueOf(menuItemsModel.getUnitprice()));

                    if(!Objects.equals(String.valueOf(menuItemsModel.getGrossweight()), "")){
                        binding.weightTextView.setText(String.valueOf(menuItemsModel.getGrossweight()));
                    }
                    else  if(!String.valueOf(menuItemsModel.getPortionsize()).equals("")){
                        binding.weightTextView.setText(String.valueOf(menuItemsModel.getPortionsize()));

                    }
                    else   if(!String.valueOf(menuItemsModel.getNetweight()).equals("")){
                        binding.weightTextView.setText(String.valueOf(menuItemsModel.getNetweight()));

                    }
                    else{
                        binding.weightTextView.setText(String.valueOf(menuItemsModel.getGrossweight()));

                    }
                }
                else if(menuItemsModel.getItemtype().equals(Constants.priceperkg_pricetype)){
                    binding.weightEditText.setVisibility(View.VISIBLE);
                    binding.weightTextView.setVisibility(View.GONE);
                    binding.weightEditText.setText(String.valueOf(menuItemsModel.getGrossweight()));
                    binding.priceperitemorunitpricelabel.setText("Price Per Kg");
                    binding.priceEditText.setText(String.valueOf(menuItemsModel.getPriceperkg()));
                    binding.finalPriceTextview.setText(String.valueOf(menuItemsModel.getPriceperkg()));



                }
                else{
                    binding.weightEditText.setVisibility(View.VISIBLE);
                    binding.weightTextView.setVisibility(View.GONE);
                    binding.priceEditText.setText(String.valueOf(menuItemsModel.getPriceperkg()));
                    binding.finalPriceTextview.setText(String.valueOf(menuItemsModel.getPriceperkg()));

                    binding.priceperitemorunitpricelabel.setText("Price Per Kg");

                }

                try{
                    binding.quantityTextview.setText(String.valueOf(1));
                    getCurrentKgAndPriceValue_and_Call_Calculation();
                    binding.autoCompleteTextViewMenuItem.setText(String.valueOf(menuItemsModel.getItemname()));

                    binding.autoCompleteTextViewMenuItem.dismissDropDown();
                }
                catch (Exception e){
                    e.printStackTrace();
                }


            }
            catch (Exception e){
                e.printStackTrace();
            }



        });

       */

        }
        catch (Exception e){
        e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove the observer when the Fragment is destroyed
        menuItemsViewModel.getSelectedMenuItemsFromViewModel().removeObserver(selectedMenuItemObserver);
    }
}