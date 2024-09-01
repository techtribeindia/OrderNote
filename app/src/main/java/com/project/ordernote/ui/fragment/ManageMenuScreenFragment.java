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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.ordernote.R;
import com.project.ordernote.data.local.LocalDataManager;
import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.databinding.FragmentManageMenuScreenBinding;
import com.project.ordernote.databinding.FragmentSettingsBinding;
import com.project.ordernote.ui.adapter.MenuItemListAdapter;
import com.project.ordernote.utils.SessionManager;
import com.project.ordernote.viewmodel.MenuItems_ViewModel;

import java.util.List;
import java.util.Objects;

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
        sessionManager = new SessionManager(requireActivity());
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
        menuItemsDescFragment = new MenuItemsDescFragment();
        menuItemsDescFragment.setmHandler(newHandler(),"add");
        binding.menuRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(),1));
        binding.menuRecyclerView.setAdapter(menuItemListAdapter);

        binding.addMenuitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                menuItemsDescFragment.show(getParentFragmentManager(),"ManageMenuScreenFragment");
            }
        });

        return binding.getRoot();
    }

    private Handler newHandler() {
        return new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                Bundle bundle = message.getData();

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

        menuItemsViewModel.getMenuItemsFromViewModel().observe(getViewLifecycleOwner(), resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:

                        Toast.makeText(requireActivity(), "Loading Menu", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        menuItemListAdapter.setOrders(resource.data,"ManageMenuScreenFragment");
                        Toast.makeText(requireActivity(), "Success in fetching menu", Toast.LENGTH_SHORT).show();


                        break;
                    case ERROR:
                        Toast.makeText(requireActivity(), "Error in fetching menu", Toast.LENGTH_SHORT).show();

                        break;
                }
            }
        });
    }
}