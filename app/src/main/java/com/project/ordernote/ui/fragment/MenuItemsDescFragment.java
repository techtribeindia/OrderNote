package com.project.ordernote.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.project.ordernote.R;
import com.project.ordernote.databinding.FragmentManageMenuScreenBinding;
import com.project.ordernote.databinding.FragmentMenuItemsDescBinding;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MenuItemsDescFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MenuItemsDescFragment extends DialogFragment {

    FragmentMenuItemsDescBinding binding;
    private Handler mHandler;
    private String selectedScreen;
    public MenuItemsDescFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MenuItemsDescFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MenuItemsDescFragment newInstance(String param1, String param2) {
        MenuItemsDescFragment fragment = new MenuItemsDescFragment();
        Bundle args = new Bundle();
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

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentMenuItemsDescBinding.inflate(inflater, container, false);
        if(Objects.equals(selectedScreen, "add"))
        {
            binding.menuitemButton.setText("Add MenuItem");
        }

        binding.closeIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDialog().dismiss();
            }
        });
        return binding.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Removes default background
        }
    }
}