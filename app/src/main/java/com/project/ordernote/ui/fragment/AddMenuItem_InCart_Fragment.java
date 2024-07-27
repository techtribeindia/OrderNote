package com.project.ordernote.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.project.ordernote.R;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.databinding.FragmentAddMenuItemInCartBinding;
import com.project.ordernote.viewmodel.Buyers_ViewModel;

public class AddMenuItem_InCart_Fragment extends DialogFragment {

    FragmentAddMenuItemInCartBinding binding;

    private MenuItems_Model menuItemsModel = new MenuItems_Model();




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

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
       // return inflater.inflate(R.layout.fragment_add_menu_item__in_cart_, container, false);
        binding = FragmentAddMenuItemInCartBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }
}