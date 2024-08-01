package com.project.ordernote.ui.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.project.ordernote.data.local.LocalDataManager;
import com.project.ordernote.data.model.AppData_Model;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.model.ItemDetails_Model;
import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;

import com.project.ordernote.databinding.FragmentAddOrdersBinding;
import com.project.ordernote.ui.adapter.CreateOrderCartItemAdapter;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.DateParserClass;
import com.project.ordernote.utils.DecimalInputFilter;
import com.project.ordernote.viewmodel.AppData_ViewModel;
import com.project.ordernote.viewmodel.Buyers_ViewModel;
import com.project.ordernote.viewmodel.Counter_ViewModel;
import com.project.ordernote.viewmodel.MenuItems_ViewModel;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateOrderFragment extends Fragment {
    private OrderDetails_ViewModel ordersViewModel;
    private MenuItems_ViewModel menuItemsViewModel;
    private AppData_ViewModel appDataViewModel;
    private Buyers_ViewModel buyersViewModel;
    private Counter_ViewModel counterViewModel;


    private List<String> paymentMode = new ArrayList<>();

    AppData_Model appData_model;

    boolean menuItemFetchedSuccesfully = false , buyerDetailsFetchedSuccessfully = false;

    private FragmentAddOrdersBinding binding;
    CreateOrderCartItemAdapter createOrderCartItemAdapter ;

    private Observer<List<ItemDetails_Model>> itemAddedInCartObserver;
    private Observer<OrderDetails_Model> orderDetailCalculatedValueModelObserver;
    private Observer<ApiResponseState_Enum<Long>> orderNoObserver;

    private ArrayAdapter<String> paymentModeAdapter;
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentAddOrdersBinding.inflate(inflater, container, false);

        return binding.getRoot();
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
            try {
                ordersViewModel = new ViewModelProvider(requireActivity()).get(OrderDetails_ViewModel.class);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                counterViewModel = new ViewModelProvider(requireActivity()).get(Counter_ViewModel.class);
                counterViewModel.getOrderCounterAndIncrementLocally("vendor_1");
            } catch (Exception e) {
                e.printStackTrace();
            }


            try {
               // buyersViewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()).create(Buyers_ViewModel.class);
                // Initialize ViewModels
                buyersViewModel = new ViewModelProvider(requireActivity()).get(Buyers_ViewModel.class);

                //     buyersViewModel = new ViewModelProvider(this).get(Buyers_ViewModel.class);
                List<Buyers_Model> buyersList = LocalDataManager.getInstance().getBuyers();

                if(buyersList.size() > 0){

                    buyersViewModel.setBuyersListinMutableLiveData(buyersList);
                }
                else{
                    buyersViewModel.getBuyersListFromRepository("vendor_1");
                }

                buyersViewModel.setSelectedBuyerLiveData(new Buyers_Model());

            } catch (Exception e) {
                e.printStackTrace();
            }


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


            try{
                appDataViewModel  = new ViewModelProvider(requireActivity()).get(AppData_ViewModel.class);

                appData_model = LocalDataManager.getInstance().getAppData_model();

                if(appData_model == null){
                      appDataViewModel.FetchAppDataFromRepositoryAndSaveInLocalDataManager();
                }
                else{

                    paymentMode = appData_model.getPaymentmode();


                }

            }
            catch (Exception e){
                e.printStackTrace();
            }

        }
        catch (Exception e ){
            e.printStackTrace();
        }


    }

    private void setAdapterForPaymentList() {

        paymentModeAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, paymentMode);
        paymentModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        binding.paymentModeSpinner.setAdapter(paymentModeAdapter);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.dateValueTextview.setText(String.valueOf(DateParserClass.getDate_ReadableFormat()));
        setObserver();
        setAdapterForPaymentList();
        ordersViewModel.getItemDetailsArraylistViewModel().observeForever(itemAddedInCartObserver);
        ordersViewModel.getOrderDetailsCalculatedValueModel().observeForever(orderDetailCalculatedValueModelObserver);
        counterViewModel.getOrderNumberLiveData().observeForever(orderNoObserver);

        binding.paymentModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item as a String
                String selectedItem = (String) parent.getItemAtPosition(position);
                // Do something with the selected item
                //Toast.makeText(requireContext(), "Selected: " + selectedItem, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // This can be left empty, or you can handle cases when nothing is selected
            }
        });

        binding.buyerselectionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBuyerSelectionDialog();
            }
        });


        binding.addItemCardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openMenuitemSelectionDialog();
            }
        });

        binding.editDicountImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(ordersViewModel.getSubTotalPrice()>0){
                    openApplyDiscountDialog();
                }
                else{
                    //Toast.makeText(requireActivity(), "Before applying discount total value should be greater than zero", Toast.LENGTH_SHORT).show();
                }
            }
        });

        binding.discountLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.editDicountImage.performClick();
            }
        });

        binding .receivedAmountEditText.setFilters(new InputFilter[]{new DecimalInputFilter()});


        binding. receivedAmountEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                double totalPrice = 0;
                double enteredPrice = 0 ;
                double balanceAmount = 0 ;
                try{
                    totalPrice = ordersViewModel.getTotalPrice();
                }
                catch (Exception e){
                    e.printStackTrace();
                }

                try{
                    String enteredPriceString = editable.toString();
                    String numericPart = extractNumericValue(enteredPriceString);
                    enteredPrice = Double.parseDouble(numericPart);
                    // Format the numeric value

                }
                catch (Exception e){
                    e.printStackTrace();
                }


                try {

                    balanceAmount = totalPrice - enteredPrice;
                    binding.balanceAmountTextview.setText(String.valueOf(balanceAmount));

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });




        menuItemsViewModel.getMenuItemsFromViewModel().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        menuItemFetchedSuccesfully = false;
                        showProgressBar(true);
                        //Toast.makeText(requireActivity(), "Loading Menu", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        menuItemFetchedSuccesfully = true;
                        if(buyerDetailsFetchedSuccessfully) {
                            showProgressBar(false);
                        }
                        //Toast.makeText(requireActivity(), "Success in fetching menu", Toast.LENGTH_SHORT).show();

                        // adapter.setMenuItems(resource.data);
                        break;
                    case ERROR:
                        Toast.makeText(requireActivity(), "Error in fetching menu", Toast.LENGTH_SHORT).show();
                        menuItemFetchedSuccesfully = false;
                        showProgressBar(false);
                        break;
                }
            }
        });

        buyersViewModel.getBuyersListFromViewModel().observe(getViewLifecycleOwner(),  resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        buyerDetailsFetchedSuccessfully = false;
                        showProgressBar(true);
                        //Toast.makeText(requireActivity(), "Loading Buyer", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        buyerDetailsFetchedSuccessfully = true;
                        if(menuItemFetchedSuccesfully){
                            showProgressBar(false);
                        }

                        //Toast.makeText(requireActivity(), "Success in fetching Buyer", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR:
                        buyerDetailsFetchedSuccessfully = false;
                        Toast.makeText(requireActivity(), "Error in fetching Buyer", Toast.LENGTH_SHORT).show();
                        showProgressBar(false);
                        break;
                }
            }
        });

        buyersViewModel.getSelectedBuyersDetailsFromViewModel().observe(getViewLifecycleOwner(), buyerData -> {
            if (buyerData != null) {

                if(!buyerData.getName().equals("")){
                    binding.selectedBuyerNameTextview.setText(String.valueOf(buyerData.getName()));
                    binding.selecedBuyerDetailsTextview.setText(String.valueOf(buyerData.getAddress1() +" , "+'\n'+buyerData.getAddress2()+" - "+""+buyerData.getPincode()+" . "+'\n'+"Ph:- +91"+buyerData.getMobileno()));
                }



            }
        });


        appDataViewModel.getAppModelDataFromLiveModel().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        //Toast.makeText(requireActivity(), " loading ", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        //Toast.makeText(requireActivity(), " got payment mode ", Toast.LENGTH_SHORT).show();
                        paymentMode = appData_model.getPaymentmode();
                        setAdapterForPaymentList();
                        break;
                    case ERROR:
                        Toast.makeText(requireActivity(), " error ", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });


    }

    private void openApplyDiscountDialog() {

        try{
            ApplyDiscountDialogFragment dialogFragment2 = new ApplyDiscountDialogFragment();
            // dialogFragment.setBuyerSelectionListener(this); // Set the listener
            dialogFragment2.show(getParentFragmentManager(), "ApplyDiscountDialogFragment");

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private void setObserver() {
        try{
            orderNoObserver = new Observer<ApiResponseState_Enum<Long>>() {
                @Override
                public void onChanged(@Nullable ApiResponseState_Enum<Long> resource) {
                    // Update your UI or perform any actions based on the updated data
                    try {
                        if (resource != null) {
                            switch (resource.status) {
                                case LOADING:
                                    showProgressBar(true);
                                   // Toast.makeText(requireActivity(), "Loading orderno", Toast.LENGTH_SHORT).show();
                                    break;
                                case SUCCESS:
                                    showProgressBar(false);
                                    binding.ordernoValueTexview.setText(String.valueOf("# " + String.valueOf(resource.data)));
                                  //  Toast.makeText(requireActivity(), "Success in orderno", Toast.LENGTH_SHORT).show();
                                    break;
                                case ERROR:
                                   Toast.makeText(requireActivity(), "Error in orderno ," + String.valueOf(resource.message), Toast.LENGTH_SHORT).show();
                                    showProgressBar(false);
                                    break;
                            }
                        }
                        //  //Toast.makeText(requireActivity(), "sizze from observer: "+String.valueOf(itemDetailsList.size()), Toast.LENGTH_SHORT).show();
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                }
            };

            itemAddedInCartObserver = new Observer<List<ItemDetails_Model>>() {
                @Override
                public void onChanged(@Nullable List<ItemDetails_Model> itemDetailsList) {
                    // Update your UI or perform any actions based on the updated data

                  //  //Toast.makeText(requireActivity(), "sizze from observer: "+String.valueOf(itemDetailsList.size()), Toast.LENGTH_SHORT).show();
                    setAdapterForCartRecyclerView(Objects.requireNonNull(itemDetailsList));

                }
            };


            orderDetailCalculatedValueModelObserver = new Observer<OrderDetails_Model>() {
                @Override
                public void onChanged(@Nullable OrderDetails_Model itemDetailsList) {
                    // Update your UI or perform any actions based on the updated data





                    binding.dicountValue.setText(String.valueOf(currencyFormat.format(Objects.requireNonNull(itemDetailsList).getDiscount())));
                    binding.totalPriceTextview.setText(String.valueOf(currencyFormat.format(itemDetailsList.getTotalprice())));

                    binding.subtotalTextview.setText(String.valueOf(currencyFormat.format(itemDetailsList.getPrice())));
                }
            };

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        // Remove the observer when the Fragment is destroyed
        ordersViewModel.getOrderDetailsCalculatedValueModel().removeObserver(orderDetailCalculatedValueModelObserver);
        ordersViewModel.getItemDetailsArraylistViewModel().removeObserver(itemAddedInCartObserver);
        counterViewModel.getOrderNumberLiveData().removeObserver(orderNoObserver);


    }
    private void setAdapterForCartRecyclerView(List<ItemDetails_Model> data) {
        if(!data.isEmpty()) {

            binding.itemDetailsRecyclerview.setVisibility(View.VISIBLE);
            binding.itemDetailsInstrucTextviewRecyclerview.setVisibility(View.GONE);
            if (createOrderCartItemAdapter != null) {
                createOrderCartItemAdapter.setData(data);
            } else {
                createOrderCartItemAdapter = new CreateOrderCartItemAdapter(data);
                binding.itemDetailsRecyclerview.setAdapter(createOrderCartItemAdapter);
                binding.itemDetailsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));

            }





            if (createOrderCartItemAdapter != null) {
                int totalItems = createOrderCartItemAdapter.getItemCount();
                int totalHeight = 0;

                // Iterate over all items in the adapter
                for (int i = 0; i < totalItems; i++) {
                    View listItem = createOrderCartItemAdapter.createViewHolder(binding.itemDetailsRecyclerview, createOrderCartItemAdapter.getItemViewType(i)).itemView;
                    listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight()+50;
                }

                // Add some padding or spacing if needed
                int spacing = binding.itemDetailsRecyclerview.getItemDecorationCount() * 10; // Example spacing
                totalHeight += spacing;

                // Set the RecyclerView's height
                ViewGroup.LayoutParams params = binding.itemDetailsRecyclerview.getLayoutParams();
                params.height = totalHeight;
                binding.itemDetailsRecyclerview.setLayoutParams(params);
            }



        }
        else{
            binding.itemDetailsRecyclerview.setVisibility(View.GONE);
            binding.itemDetailsInstrucTextviewRecyclerview.setVisibility(View.VISIBLE);
        }





    }

    private void openMenuitemSelectionDialog() {
        try{
            AddMenuItem_InCart_Fragment dialogFragment2 = new AddMenuItem_InCart_Fragment();
            // dialogFragment.setBuyerSelectionListener(this); // Set the listener
            dialogFragment2.show(getParentFragmentManager(), "AddMenuItemInCartDialogFragment");

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }

    private void openBuyerSelectionDialog() {

        try{
            BuyerSelectionDialogFragment dialogFragment = new BuyerSelectionDialogFragment();
           // dialogFragment.setBuyerSelectionListener(this); // Set the listener
            dialogFragment.show(getParentFragmentManager(), "BuyerSelectionDialogFragment");

        }
        catch (Exception e){
            e.printStackTrace();
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


}
