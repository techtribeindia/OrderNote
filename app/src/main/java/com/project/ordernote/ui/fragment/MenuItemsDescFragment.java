package com.project.ordernote.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.project.ordernote.R;
import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.databinding.FragmentMenuItemsDescBinding;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.SessionManager;
import com.project.ordernote.viewmodel.MenuItems_ViewModel;

import java.text.DecimalFormat;
import java.util.Objects;
import java.util.UUID;
import java.util.function.DoubleUnaryOperator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuItemsDescFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuItemsDescFragment extends DialogFragment {

    FragmentMenuItemsDescBinding binding;
    private MenuItems_ViewModel menuItemsViewModel;
    private Handler mHandler;
    private String selectedScreen;
    private String orderJson;
    private String function;
    private SessionManager sessionManager;
    private String itemkey="";
    public MenuItemsDescFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static MenuItemsDescFragment newInstance(String orderJson) {
        MenuItemsDescFragment fragment = new MenuItemsDescFragment();
        Bundle args = new Bundle();
        args.putString("order_json", orderJson);
        fragment.setArguments(args);
        return fragment;
    }

    public void setmHandler(Handler mHandler, String selectedOrderButton) {
        this.mHandler = mHandler;
        this.selectedScreen  = selectedOrderButton;
        Log.d("selectedOrderButton", String.valueOf(selectedOrderButton));
    }
    private void sendHandlerMessage(String function) {
        //Log.e(Constants.TAG, "createBillDetails in cartaItem 1");

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("function", function);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireActivity());
        if (getArguments() != null) {
            orderJson = getArguments().getString("order_json");

        } else {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        menuItemsViewModel = new ViewModelProvider(requireActivity()).get(MenuItems_ViewModel.class);
        MenuItems_Model menuItemsModel = new MenuItems_Model();
        binding = FragmentMenuItemsDescBinding.inflate(inflater, container, false);
        binding.sellingPriceInput.setEnabled(false);
        binding.itemtype.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                if (binding.priceperkg.isChecked())
                {
                    binding.priceTextview.setText("Price Per Kg");
                    binding.sellingPriceInput.setVisibility(View.VISIBLE);
                    binding.sellingPriceTextview.setVisibility(View.VISIBLE);
                    binding.grossweightText.setText("GrossWeight in KG");
                    priceperkgFun();

                }
                if(binding.unitprice.isChecked())
                {
                    binding.priceTextview.setText("Price Per Unit");
                    binding.grossweightText.setText("GrossWeight in KG ( optional )");
                    binding.sellingPriceInput.setVisibility(View.GONE);
                    binding.sellingPriceTextview.setVisibility(View.GONE);
                    binding.sellingPriceInput.setText("");
                }
            }
        });

        if(Objects.equals(selectedScreen, "add"))
        {
            binding.menuitemButton.setText("Add MenuItem");
            function = "add";
        }
        else
        {
            binding.menuitemButton.setText("Update MenuItem");
            function = "update";
        }



        binding.priceperkg.setText(Constants.priceperkg_itemtype);
        binding.unitprice.setText(Constants.unitprice_itemtype);

        binding.grossweight.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (binding.priceperkg.isChecked())
                {
                    priceperkgFun();
                }
                if(binding.unitprice.isChecked())
                {

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.price.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (binding.priceperkg.isChecked())
                {
                    priceperkgFun();
                }
                if(binding.unitprice.isChecked())
                {

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        binding.closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        binding.menuitemButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String itemname = binding.itemname.getText().toString();
                String grossweight = binding.grossweight.getText().toString();
                String portionsize = binding.portionsize.getText().toString();
                String price = binding.price.getText().toString();

                String itemtype = "",priceperkg="",unitprice="";

                if (binding.priceperkg.isChecked())
                {
                    priceperkg = binding.price.getText().toString();
                    unitprice = binding.sellingPriceInput.getText().toString();
                    itemtype = Constants.priceperkg_itemtype;
                }
                if(binding.unitprice.isChecked())
                {
                    itemtype = Constants.unitprice_itemtype;
                    priceperkg = binding.price.getText().toString();
                    unitprice = binding.price.getText().toString();
                }


                if(itemname.isEmpty())
                {
                    showSnackbar(view, "Please enter the itemname");
                    return;
                }
                if(itemtype.isEmpty())
                {
                    showSnackbar(view, "Please select the itemtype");
                    return;

                }
                else
                {
                    if(itemtype.equals(Constants.priceperkg_itemtype))
                    {
                        if(grossweight.isEmpty())
                        {
                            showSnackbar(view, "Please enter the Gross weight");
                            return;
                        }
                    }
                    if(itemtype.equals(Constants.unitprice_itemtype))
                    {
                        if(portionsize.equals(Constants.unitprice_itemtype))
                        {
                            showSnackbar(view, "Please enter the portionsize");
                            return;
                        }
                    }
                }
                if (price.isEmpty())
                {
                    showSnackbar(view, "Please enter the portionsize");
                    return;
                }
                UUID uuid = UUID.randomUUID();
                String uuidAsString = uuid.toString();

                Double grossweightingrams = 0.00;
                Double pricerperkgD = 0.00;
                Double unitpriceD=0.00;
                if(!grossweight.isEmpty())
                {
                    grossweightingrams = Double.parseDouble(grossweight)*1000;
                }
                if(!priceperkg.isEmpty())
                {
                    pricerperkgD = Double.parseDouble(priceperkg);
                }
                if(!unitprice.isEmpty())
                {
                    unitpriceD = Double.parseDouble(unitprice);
                }

                if(itemkey.isEmpty())
                {
                    menuItemsModel.setItemkey(uuidAsString);
                }
                else {
                    menuItemsModel.setItemkey(itemkey);
                }
                Toast.makeText(requireActivity(), itemkey, Toast.LENGTH_SHORT).show();
                menuItemsModel.setItemname(itemname);
                menuItemsModel.setItemtype(itemtype);
                menuItemsModel.setGrossweight(grossweightingrams);
                menuItemsModel.setPortionsize(portionsize);
                menuItemsModel.setPriceperkg(pricerperkgD);
                menuItemsModel.setUnitprice(unitpriceD);
                menuItemsModel.setVendorkey(sessionManager.getVendorkey());
                menuItemsModel.setVendorname(sessionManager.getVendorname());
                handleInsertUpdate(menuItemsModel);
            }
        });

        if (orderJson != null) {
            Gson gson = new Gson();
            MenuItems_Model menuitems = gson.fromJson(orderJson, MenuItems_Model.class);

            if (menuitems != null) {
                binding.itemname.setText(menuitems.getItemname());
                itemkey = menuitems.getItemkey();
                if(menuitems.getItemtype().equalsIgnoreCase(Constants.priceperkg_itemtype))
                {

                    binding.priceperkg.setChecked(true);
                    binding.price.setText(String.valueOf(menuitems.getPriceperkg()));

                    Double grossweight = Double.valueOf(menuitems.getGrossweight());
                    Double priceperkg = Double.valueOf(menuitems.getPriceperkg());
                    Double unitprice = 0.00;
                    DecimalFormat precision = new DecimalFormat("0.00");
                    if(priceperkg != 0)
                    {
                        unitprice = (priceperkg/1000)*grossweight;
                    }
                    if(grossweight !=0 )
                    {
                        grossweight = grossweight/1000;
                    }

                    binding.grossweight.setText(String.valueOf(grossweight));
                    binding.sellingPriceInput.setText(String.valueOf(precision.format(unitprice)));



                    binding.sellingPriceInput.setVisibility(View.VISIBLE);
                    binding.sellingPriceTextview.setVisibility(View.VISIBLE);
                }

                if(menuitems.getItemtype().equalsIgnoreCase(Constants.unitprice_itemtype))
                {
                    binding.unitprice.setChecked(true);
                    binding.price.setText(String.valueOf(menuitems.getUnitprice()));

                    binding.sellingPriceInput.setVisibility(View.GONE);
                    binding.sellingPriceTextview.setVisibility(View.GONE);

                    Double grossweight = Double.valueOf(menuitems.getGrossweight());
                    if(grossweight !=0 )
                    {
                        grossweight = grossweight/1000;
                    }

                    binding.grossweight.setText(String.valueOf(grossweight));
                }

                binding.portionsize.setText(menuitems.getPortionsize());


            }
        }



        return binding.getRoot();
    }

    private void handleInsertUpdate(MenuItems_Model menuItemsModel)
    {
        menuItemsViewModel.updateInsertUpdateMenu(menuItemsModel,selectedScreen).observe(this,
                this::observeOrderDetails);
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Removes default background
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
    private void observeOrderDetails(ApiResponseState_Enum<String> resource) {

        switch (resource.status) {
            case LOADING:
                showProgressBar(true);
                break;
            case SUCCESS:

                getDialog().dismiss();
                showSnackbar(requireView(),"Success in fetching orders");
                showProgressBar(false);
                sendHandlerMessage(function);
                Objects.requireNonNull(getDialog()).dismiss();
                break;
            case ERROR:
                showSnackbar(requireView(),resource.message);
                showProgressBar(false);
                break;
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

    private void priceperkgFun()
    {
        Double grossweight = 0.00;
        Double priceperkg = 0.00;
        Double price = 0.00;
        DecimalFormat precision = new DecimalFormat("0.00");
        if(!binding.grossweight.getText().toString().isEmpty() && binding.grossweight.getText() != null)
        {
            grossweight = Double.valueOf((binding.grossweight.getText().toString()));
        }

        if(!binding.price.getText().toString().isEmpty() && binding.price.getText() != null)
        {
            priceperkg = Double.valueOf((binding.price.getText().toString()));
        }
        if(grossweight != 0)
        {
            grossweight = grossweight*1000;
        }
        if(priceperkg!= 0)
        {
            price = (priceperkg/1000)*grossweight;
        }

        binding.sellingPriceInput.setText(String.valueOf(precision.format(price)));
    }

    private void unitpriceFun()
    {

    }
}