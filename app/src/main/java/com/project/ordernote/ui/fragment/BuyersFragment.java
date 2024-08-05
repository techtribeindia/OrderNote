package com.project.ordernote.ui.fragment;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.ordernote.R;
import com.project.ordernote.databinding.FragmentBuyerSelectionDialogBinding;
import com.project.ordernote.databinding.FragmentBuyersBinding;


public class BuyersFragment extends Fragment {


    FragmentBuyersBinding binding;


    public BuyersFragment() {
        // Required empty public constructor
    }

    public static BuyersFragment newInstance(String param1, String param2) {
        BuyersFragment fragment = new BuyersFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBuyersBinding.inflate(inflater, container, false);
        return binding.getRoot();
       /// return inflater.inflate(R.layout.fragment_buyers, container, false);
    }
}