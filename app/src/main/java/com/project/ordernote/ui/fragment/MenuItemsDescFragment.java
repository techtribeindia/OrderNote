package com.project.ordernote.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.project.ordernote.R;
import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.databinding.FragmentMenuItemsDescBinding;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.viewmodel.MenuItems_ViewModel;

import java.util.Objects;
import java.util.UUID;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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

        if(Objects.equals(selectedScreen, "add"))
        {
            binding.menuitemButton.setText("Add MenuItem");
        }
        else
        {
            binding.menuitemButton.setText("Update MenuItem");
        }

        binding.priceperkg.setText(Constants.priceperkg_itemtype);
        binding.unitprice.setText(Constants.unitprice_itemtype);

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
                String itemtype = "";
                if (binding.priceperkg.isChecked())
                {
                    itemtype = Constants.priceperkg_itemtype;
                }
                if(binding.unitprice.isChecked())
                {
                    itemtype = Constants.unitprice_itemtype;
                }
                String grossweight = binding.grossweight.getText().toString();
                String portionsize = binding.portionsize.getText().toString();
                String price = binding.price.getText().toString();

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
                menuItemsModel.setItemkey(uuidAsString);
                menuItemsModel.setItemname(itemname);
                menuItemsModel.setItemtype(itemtype);
                menuItemsModel.setGrossweight(Double.parseDouble(grossweight));
                menuItemsModel.setPortionsize(portionsize);
                menuItemsModel.setUnitprice(Double.parseDouble(price));
                handleInsertUpdate(menuItemsModel);
            }
        });

        if (orderJson != null) {
            Gson gson = new Gson();
            MenuItems_Model menuitems = gson.fromJson(orderJson, MenuItems_Model.class);

            if (menuitems != null) {
                binding.itemname.setText(menuitems.getItemname());
                if(menuitems.getItemtype().equalsIgnoreCase(Constants.priceperkg_itemtype))
                {
                    binding.priceperkg.setChecked(true);
                }

                if(menuitems.getItemtype().equalsIgnoreCase(Constants.unitprice_itemtype))
                {
                    binding.unitprice.setChecked(true);
                }
                binding.grossweight.setText(menuitems.getGrossweight()+" g");
                binding.portionsize.setText(menuitems.getPortionsize());
                binding.price.setText(String.valueOf(menuitems.getUnitprice()));

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

        // Change the Snackbar's position to top and add top margin
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        int marginInDp = (int) (30 * getResources().getDisplayMetrics().density); // Convert 20dp to pixels
        params.setMargins(0, marginInDp, 0, 0);
        snackbarView.setLayoutParams(params);

        snackbar.show();
    }
    private void observeOrderDetails(ApiResponseState_Enum<String> resource) {

        switch (resource.status) {
            case LOADING:
                Toast.makeText(requireActivity(), "Loading Orders", Toast.LENGTH_SHORT).show();
                break;
            case SUCCESS:

                getDialog().dismiss();
                Toast.makeText(requireActivity(), "Success in fetching orders", Toast.LENGTH_SHORT).show();

                Objects.requireNonNull(getDialog()).dismiss();
                break;
            case ERROR:
                Toast.makeText(requireActivity(), "Error in fetching orders "+resource.data, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}