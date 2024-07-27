package com.project.ordernote.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.ordernote.R;
import com.project.ordernote.databinding.FragmentAddOrdersBinding;
import com.project.ordernote.databinding.FragmentOrdersBinding;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.viewmodel.Buyers_ViewModel;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrdersListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrdersListFragment extends Fragment {

    FragmentOrdersBinding fragmentOrdersBinding;
    private Buyers_ViewModel buyersViewModel;


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public OrdersListFragment() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static OrdersListFragment newInstance(String param1, String param2) {
        OrdersListFragment fragment = new OrdersListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }



        try {
         //   buyersViewModel = new ViewModelProvider(requireActivity()).get(Buyers_ViewModel.class);
           // buyersViewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()).create(Buyers_ViewModel.class);

            buyersViewModel = new ViewModelProvider(requireActivity()).get(Buyers_ViewModel.class);

        } catch (Exception e) {
            e.printStackTrace();
        }




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentOrdersBinding = FragmentOrdersBinding.inflate(inflater, container, false);



        try {
            buyersViewModel.getBuyersListFromViewModel().observe(getViewLifecycleOwner(), buyersResponse -> {
                if (buyersResponse.status == ApiResponseState_Enum.Status.SUCCESS) {
                    // Use buyers data
                    fragmentOrdersBinding.buyersname.setText(String.valueOf(buyersResponse.data.size()));
                }
            });

             /*
            buyersViewModel.getBuyersListFromViewModel().observe(getViewLifecycleOwner(), resource -> {
                if (resource != null) {
                    switch (resource.status) {
                        case LOADING:
                            fragmentOrdersBinding.buyersname.setText(String.valueOf("Loading"));
                            break;
                        case SUCCESS:
                            fragmentOrdersBinding.buyersname.setText(String.valueOf(resource.data.size()));
                            break;
                        case ERROR:
                            fragmentOrdersBinding.buyersname.setText(String.valueOf("Error"));

                            break;
                    }
                }
            });


              */
        }

        catch (Exception e){
            e.printStackTrace();
        }


        return fragmentOrdersBinding.getRoot();

    }
}