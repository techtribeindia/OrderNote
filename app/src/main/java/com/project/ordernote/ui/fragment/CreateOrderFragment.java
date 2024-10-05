package com.project.ordernote.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.project.ordernote.R;
import com.project.ordernote.data.local.LocalDataManager;
import com.project.ordernote.data.model.AppData_Model;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.model.ItemDetails_Model;
import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.data.model.OrderDetails_Model;

import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.databinding.FragmentAddOrdersBinding;
import com.project.ordernote.ui.activity.Dashboard;
import com.project.ordernote.ui.adapter.CreateOrderCartItemAdapter;
import com.project.ordernote.utils.AlertDialogUtil;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.DateParserClass;
import com.project.ordernote.utils.DecimalInputFilter;
import com.project.ordernote.utils.SwipeToDeleteCallback;
import com.project.ordernote.viewmodel.AppData_ViewModel;
import com.project.ordernote.viewmodel.Buyers_ViewModel;
import com.project.ordernote.viewmodel.Counter_ViewModel;
import com.project.ordernote.viewmodel.MenuItems_ViewModel;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;
import com.project.ordernote.viewmodel.OrderItemDetails_ViewModel;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CreateOrderFragment extends Fragment {
    private OrderDetails_ViewModel ordersViewModel;
    private OrderItemDetails_ViewModel orderItemDetailsViewModel;
    private MenuItems_ViewModel menuItemsViewModel;
    private AppData_ViewModel appDataViewModel;
    private Buyers_ViewModel buyersViewModel;
    private Counter_ViewModel counterViewModel;


    private List<String> paymentModeStringArrayList = new ArrayList<>();

    AppData_Model appData_model;

    String selectedPaymentMode = "" , paymentDesc = "";
    double receivedAmount = 0 ;
    boolean menuItemFetchedSuccesfully = false , buyerDetailsFetchedSuccessfully = false ,
            isPaymentModeSelected = false , isGenerateOrderClicked = false;

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
            try{
                orderItemDetailsViewModel = new ViewModelProvider(requireActivity()).get(OrderItemDetails_ViewModel.class);


            }
            catch (Exception e){
                e.printStackTrace();
            }
            try {
                ordersViewModel = new ViewModelProvider(requireActivity()).get(OrderDetails_ViewModel.class);
                ordersViewModel.clearAllLiveData();
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

                buyersViewModel.clearSelectedLiveData();

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

                menuItemsViewModel.updateSelectedMenuItemModel(new MenuItems_Model());

               // menuItemsViewModel = new ViewModelProvider(this).get(MenuItems_ViewModel.class);

            } catch (Exception e) {
                e.printStackTrace();
            }


            try{
                appDataViewModel  = new ViewModelProvider(requireActivity()).get(AppData_ViewModel.class);

                appData_model = LocalDataManager.getInstance().getAppData_model();

                if(appData_model == null ){
                      appDataViewModel.FetchAppDataFromRepositoryAndSaveInLocalDataManager();
                }
                else{

                    paymentModeStringArrayList = appData_model.getPaymentmode();


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
        
            paymentModeAdapter = new ArrayAdapter<>(requireActivity(), android.R.layout.simple_spinner_item, paymentModeStringArrayList);
            paymentModeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            binding.paymentModeSpinner.setAdapter(paymentModeAdapter);

    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        binding.dateValueTextview.setText(String.valueOf(DateParserClass.getDateInStandardFormat()));
        setObserver();
        setAdapterForPaymentList();
        ordersViewModel.getItemDetailsArraylistViewModel().observeForever(itemAddedInCartObserver);
        ordersViewModel.getOrderDetailsCalculatedValueModel().observeForever(orderDetailCalculatedValueModelObserver);
        counterViewModel.getOrderNumberLiveData().observeForever(orderNoObserver);
        binding.paymentModeOverlayTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.paymentModeOverlayTextview.setVisibility(View.GONE);
                binding.paymentModeSpinner.setVisibility(View.VISIBLE);
                binding.paymentModeSpinner.performClick();

            }
        });

        binding.paymentModeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                // Get the selected item as a String
                isPaymentModeSelected  = true;
                selectedPaymentMode = (String) parent.getItemAtPosition(position);
                Toast.makeText(requireContext(), "Selected: " + selectedPaymentMode, Toast.LENGTH_SHORT).show();

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
                    showSnackbar(view,"Before applying discount total value should be greater than zero");

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
                    receivedAmount = enteredPrice;
                    balanceAmount = totalPrice - enteredPrice;
                    binding.balanceAmountTextview.setText(String.valueOf(currencyFormat.format(balanceAmount)));

                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        binding. paymentDescriptionEdittext.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                paymentDesc = editable.toString();



            }
        });



        binding.createOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    if(ordersViewModel==null){
                        //Toast.makeText(requireActivity(), "Please add item in cart", Toast.LENGTH_SHORT).show();

                        showSnackbar(view,"Please add item in cart");

                        return;
                    }
                    if(ordersViewModel.getItemDetailsArraylistViewModel().getValue()==null){
                        showSnackbar(view,"Please add item in cart");

                        return;
                    }

                    if(buyersViewModel==null){
                        showSnackbar(view,"Please select buyer");

                        return;
                    }
                    if(buyersViewModel.getSelectedBuyersDetailsFromViewModel().getValue()==null){
                        showSnackbar(view,"Please select buyer");

                        return;
                    }


                    if((!Objects.requireNonNull(ordersViewModel.getItemDetailsArraylistViewModel().getValue()).isEmpty() )){
                            if(!buyersViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getMobileno().equals("")){
                                if(isPaymentModeSelected){
                                    showProgressBar(true);


                                    showConfirmationAlerDialog();
                                }
                                else{
                                    showSnackbar(view,"Failed in payment selection");
                                    return;


                                }
                            }
                            else{
                                showSnackbar(view,"Failed in buyer selection");
                                return;


                            }
                    }
                    else{

                        showSnackbar(view,"Please add item in cart");
                        return;


                    }
                }
                catch (Exception e){

                    Toast.makeText(requireActivity(), "Failed in OnClick", Toast.LENGTH_SHORT).show();

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
                        showSnackbar(view,resource.message);

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


                        if (resource.message != null) {
                            if (resource.message.equals(Constants.noDataAvailable)) {
                                LocalDataManager.getInstance().setBuyers(new ArrayList<>());

                            } else {
                                showSnackbar(view,resource.message);
                            }
                        } else {
                            showSnackbar(view,resource.message);
                        }


                      ///  Toast.makeText(requireActivity(), "Error in fetching Buyer create order 2 ", Toast.LENGTH_SHORT).show();
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
                else{
                    binding.selectedBuyerNameTextview.setText(String.valueOf("Select Buyer Name"));
                    binding.selecedBuyerDetailsTextview.setText(String.valueOf("xx , xxxxx  xxx xxxx , \nxxxx - xxxx . \nPh : - +91798xxxxxxx"));

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
                        paymentModeStringArrayList = appData_model.getPaymentmode();
                        setAdapterForPaymentList();
                        break;
                    case ERROR:
                        showSnackbar(view, resource.message);
                        break;
                }
            }
        });


    }

    private void showConfirmationAlerDialog() {
        try {
            // Inside an Activity or Fragment
            AlertDialogUtil.showCustomDialog(
                    requireActivity(),
                    "Create Order",
                    "Do you want this create this order now.", "Create", "Not now", "RED",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle positive button click
                            isGenerateOrderClicked = true;
                            counterViewModel.incrementOrderCounter("vendor_1");


                        }
                    },
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Handle negative button click
                            isGenerateOrderClicked = false;


                        }
                    }, new DialogInterface.OnCancelListener() {
                        @Override
                        public void onCancel(DialogInterface dialogInterface) {
                            isGenerateOrderClicked = false;
                        }
                    }

            );





        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    private void CreateOrderInFirestore() {

        try{
            OrderDetails_Model orderDetailsModel = new OrderDetails_Model();
            //buyer details
            try{
                orderDetailsModel.setBuyerkey(Objects.requireNonNull(buyersViewModel.getSelectedBuyersDetailsFromViewModel().getValue()).getUniquekey());

            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{
                orderDetailsModel.setBuyeraddress(buyersViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getAddress1()+" , "+buyersViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getAddress2());


            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{
                orderDetailsModel.setBuyername(buyersViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getName());

            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{
                orderDetailsModel.setBuyermobileno(buyersViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getMobileno());

            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{
                orderDetailsModel.setBuyergstin(buyersViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getGstin());

            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{
                orderDetailsModel.setBuyerpincode(buyersViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getPincode());

            }
            catch (Exception e){
                e.printStackTrace();
            }


            //orderNO

            try{

                orderDetailsModel.setTokenno(String.valueOf(Objects.requireNonNull(counterViewModel.getOrderNumberLiveData().getValue()).data));

            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{
                orderDetailsModel.setOrderid(String.valueOf(System.currentTimeMillis()));

            }
            catch (Exception e){
                e.printStackTrace();
            }



            //ORDERDETAILS:

            try{
                orderDetailsModel.setItemdetails(ordersViewModel.getItemDetailsArraylistViewModel().getValue());

            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{
                orderDetailsModel.setTotalquantity((double) (ordersViewModel.getTotalQtyFromItemDetailsArrayList()));

            }
            catch (Exception e){
                e.printStackTrace();
            }

            try{
                orderDetailsModel.setTotalprice((ordersViewModel.getTotalPrice()));

            }
            catch (Exception e){
                e.printStackTrace();
            }

            try{
                orderDetailsModel.setDiscount((ordersViewModel.getDiscountValue().getValue()));

            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{
                orderDetailsModel.setPrice((ordersViewModel.getSubTotalPrice()));

            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{
                orderDetailsModel.setOrderplaceddate(Timestamp.now());

            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{
                orderDetailsModel.setVendorkey(("vendor_1"));

            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{
                orderDetailsModel.setVendorname(("Ponrathi Traders"));

            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{
                orderDetailsModel.setVendorname((orderDetailsModel.getVendorname()));

            }
            catch (Exception e){
                e.printStackTrace();
            }


            try{
                orderDetailsModel.setReceivedamount(receivedAmount);

            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{
                orderDetailsModel.setDescription(paymentDesc);

            }
            catch (Exception e){
                e.printStackTrace();
            }
            try{

                orderDetailsModel.setStatus(Constants.created_status);

            }
            catch (Exception e){
                e.printStackTrace();
            }





            orderItemDetailsViewModel.addItemDetailsEntryInDBFromLOOP(ordersViewModel.getItemDetailsArraylistViewModel().getValue() , "vendor_1" , "Ponrathi Traders" , orderDetailsModel.getOrderid() , orderDetailsModel.getOrderplaceddate() , buyersViewModel.getSelectedBuyersDetailsFromViewModel().getValue());

            ordersViewModel.createOrderInDb(orderDetailsModel, new FirestoreService.FirestoreCallback<Void>() {
                @Override
                public void onSuccess(Void result) {
                    isGenerateOrderClicked = false;
                    neutralizeEveryVariableAndUI();
                    // Handle success
                    // e.g., show a success message or update the UI
                }

                @Override
                public void onFailure(Exception e) {
                    isGenerateOrderClicked = false;
                    showProgressBar(false);
                    // Handle failure
                    // e.g., show an error message
                }
            });




        }
        catch (Exception e){
            e.printStackTrace();
        }


    }

    private void neutralizeEveryVariableAndUI() {

        receivedAmount = 0;
         selectedPaymentMode = "";paymentDesc ="";
         menuItemFetchedSuccesfully = false ; buyerDetailsFetchedSuccessfully = false ;
         isPaymentModeSelected = false ; isGenerateOrderClicked = false;

        binding.paymentModeOverlayTextview.setVisibility(View.VISIBLE);
        binding.paymentModeSpinner.setVisibility(View.GONE);
        binding.paymentDescriptionEdittext.setText("");
        binding.receivedAmountEditText.setText("");
        binding.balanceAmountTextview.setText(currencyFormat.format(0.00));


        ordersViewModel.clearAllLiveData();
        buyersViewModel.clearSelectedLiveData();
        menuItemsViewModel.updateSelectedMenuItemModel(new MenuItems_Model());
        counterViewModel.getOrderCounterAndIncrementLocally("vendor_1");
        setObserver();
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
                                    binding.ordernoValueTexview.setText(String.valueOf("" + String.valueOf(resource.data)));

                                    if(isGenerateOrderClicked){
                                        CreateOrderInFirestore();
                                    }
                                    else{
                                        showProgressBar(false);

                                    }

                                  //  Toast.makeText(requireActivity(), "Success in orderno", Toast.LENGTH_SHORT).show();
                                    break;
                                case ERROR:
                                    showSnackbar(requireView(), resource.message);
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
                createOrderCartItemAdapter.setHandler( newHandler());
                binding.itemDetailsRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));


                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(createOrderCartItemAdapter,getContext()));
                itemTouchHelper.attachToRecyclerView(binding.itemDetailsRecyclerview);

            }





            if (createOrderCartItemAdapter != null) {
                int totalItems = createOrderCartItemAdapter.getItemCount();
                int totalHeight = 0;

                // Iterate over all items in the adapter
                for (int i = 0; i < totalItems; i++) {
                    View listItem = createOrderCartItemAdapter.createViewHolder(binding.itemDetailsRecyclerview, createOrderCartItemAdapter.getItemViewType(i)).itemView;
                    listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                    totalHeight += listItem.getMeasuredHeight()+10;
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





    private Handler newHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String data = bundle.getString("fromadapter");


                if(data.equals("CreateOrderItem_Delete")){
                    try {
                        int position = bundle.getInt("position");

                        // Inside an Activity or Fragment
                        AlertDialogUtil.showCustomDialog(
                                requireActivity(),
                                "Remove Item From Cart ",
                                "Do you want to Remove this item from cart .", "Remove", "Cancel","BLACK",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Handle positive button click

                                        ordersViewModel.removeItemFromCart(position);

                                    }
                                },
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Handle negative button click
                                        createOrderCartItemAdapter.notifyItemChanged(position);


                                    }
                                },
                                 new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialogInterface) {
                                        createOrderCartItemAdapter.notifyItemChanged(position);
                                    }
                                }

                        );





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
    private void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        Dashboard activity = (Dashboard) getActivity(); // Get reference to the activity
        if (activity != null) {
            FloatingActionButton fab = activity.getFabButton(); // Get FAB from the activity

             snackbar.setAnchorView(fab); // Set the FAB as the anchor view

        }

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
