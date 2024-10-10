package com.project.ordernote.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.ordernote.data.local.LocalDataManager;
import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.databinding.FragmentManageMenuScreenBinding;
import com.project.ordernote.ui.adapter.MenuItemListAdapter;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.SessionManager;
import com.project.ordernote.viewmodel.MenuItems_ViewModel;

import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ManageMenuScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ManageMenuScreenFragment extends DialogFragment {
    FragmentManageMenuScreenBinding binding;
    MenuItemsDescFragment menuItemsDescFragment;
    private SessionManager sessionManager;
    private MenuItems_ViewModel menuItemsViewModel;
    private MenuItemListAdapter menuItemListAdapter;
    List<MenuItems_Model> menuItemsList;

    public ManageMenuScreenFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static ManageMenuScreenFragment newInstance() {
        ManageMenuScreenFragment fragment = new ManageMenuScreenFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        sessionManager = new SessionManager(requireActivity(), Constants.USERPREF_NAME);
        menuItemsViewModel = new ViewModelProvider(requireActivity()).get(MenuItems_ViewModel.class);
        menuItemsList = LocalDataManager.getInstance().getMenuItem();
        if(menuItemsList.size() > 0){
            menuItemsViewModel.setMenuListinMutableLiveData(menuItemsList);
        }
        else{
            menuItemsViewModel.FetchMenuItemByVendorKeyFromRepository(sessionManager.getVendorkey());
        }
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentManageMenuScreenBinding.inflate(inflater, container, false);

        menuItemsViewModel = new ViewModelProvider(requireActivity()).get(MenuItems_ViewModel.class);
        menuItemListAdapter = new MenuItemListAdapter(menuItemsViewModel);

        binding.menuRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(),1));
        binding.menuRecyclerView.setAdapter(menuItemListAdapter);

        binding.addMenuitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuItemsDescFragment dialogFragment = new MenuItemsDescFragment();
                dialogFragment.setmHandler(newHandler(),"add");
                dialogFragment.show(getParentFragmentManager(),"ManageMenuScreenFragment");
            }
        });

        binding.dialogBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });

        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String menuName = charSequence.toString();

                menuItemsViewModel.filterOrderWithMenuName(menuName);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return binding.getRoot();
    }

    private Handler newHandler() {
        return new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                Bundle bundle = message.getData();
                String data = bundle.getString("function");

                if(data.equals("add")){
                    binding.searchEditText.setText("");

                }

                return false;
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Removes default background
        }
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        menuItemListAdapter.clearOrders();
        observeOrderDetails();
        menuItemsViewModel.clearSelecteMenuJson();
        menuItemsViewModel.getSelecteMenuJson().observe(getViewLifecycleOwner(), orderJson -> {
            if ((orderJson != null)&&(!orderJson.equals(""))) {
                MenuItemsDescFragment menuItemsDescFragment1 = MenuItemsDescFragment.newInstance(orderJson);
                menuItemsDescFragment1.setmHandler(newHandler(),"ManageMenuScreenFragment");
                menuItemsDescFragment1.show(getParentFragmentManager(), "ManageMenuScreenFragment");
            }
        });
    }

    private void observeOrderDetails() {
        menuItemListAdapter.clearOrders();
        menuItemsViewModel.getMenuItemsFromViewModel().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:

                        showProgressBar(true);
                        break;
                    case SUCCESS:
                        showProgressBar(false);
                        menuItemsViewModel.setOrderDetails(resource);
                        menuItemListAdapter.setOrders(resource.data,"ManageMenuScreenFragment");
                         if(resource.message == "" )
                    {
                        binding.dialogOrderstatusCard.setVisibility(View.GONE);
                        binding.menuRecyclerView.setVisibility(View.VISIBLE);
                        binding.searchLayout.setVisibility(View.VISIBLE);
                    }
                    else
                    {
                        binding.dialogOrderstatusCard.setVisibility(View.VISIBLE);
                        binding.menuRecyclerView.setVisibility(View.GONE);
                        binding.searchLayout.setVisibility(View.VISIBLE);
                        binding.dialogOrderstatusText.setText(String.valueOf(resource.message));
                    }
                        break;
                    case ERROR:
                        showProgressBar(false);
                        binding.dialogOrderstatusCard.setVisibility(View.VISIBLE);
                        binding.menuRecyclerView.setVisibility(View.GONE);
                        binding.searchLayout.setVisibility(View.GONE);
                        binding.dialogOrderstatusText.setText(String.valueOf(resource.message));

                        break;
                }
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
}