package com.project.ordernote.ui.fragment;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.project.ordernote.R;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.databinding.FragmentAddMenuItemInCartBinding;
import com.project.ordernote.viewmodel.Buyers_ViewModel;

import java.util.List;

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



    public class MenuItemAdapter extends RecyclerView.Adapter<MenuItemAdapter.ViewHolder> {

        private List<MenuItems_Model> menuItems;
        private List<Buyers_Model> buyers;

        public MenuItemAdapter(List<MenuItems_Model> menuItems, List<Buyers_Model> buyers) {
            this.menuItems = menuItems;
            this.buyers = buyers;
        }

        public class ViewHolder extends RecyclerView.ViewHolder {
            AutoCompleteTextView autoCompleteMenuItem;

            public ViewHolder(View itemView) {
                super(itemView);
              //  autoCompleteMenuItem = itemView.findViewById(R.id.autoCompleteMenuItem);
            }
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.add_item_in_cart_listitem, parent, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            // Set up the AutoCompleteTextView with menu items
            ArrayAdapter<MenuItems_Model> adapter = new ArrayAdapter<>(holder.itemView.getContext(),
                    android.R.layout.simple_dropdown_item_1line, menuItems);
            holder.autoCompleteMenuItem.setAdapter(adapter);

            holder.autoCompleteMenuItem.setOnItemClickListener((adapterView, view, pos, id) -> {
                MenuItems_Model selectedMenuItem = (MenuItems_Model) adapterView.getItemAtPosition(pos);
                // Update fragment fields based on selected item
                // For example:
               // TextView tvItemName = ( holder.itemView.getContext()).binding.tvItemName;
               // tvItemName.setText(selectedMenuItem.getName());
                // Similarly, set other fields
            });
        }

        @Override
        public int getItemCount() {
            return menuItems.size();
        }
    }



}