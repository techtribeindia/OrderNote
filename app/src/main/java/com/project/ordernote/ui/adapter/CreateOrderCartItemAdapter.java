package com.project.ordernote.ui.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.project.ordernote.data.model.ItemDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.databinding.AddItemInCartListitemBinding;

import java.util.List;

public class CreateOrderCartItemAdapter extends RecyclerView.Adapter<CreateOrderCartItemAdapter.MenuItemViewHolder> {

    private List<ItemDetails_Model> orderItemDetailsArrayList;


    public CreateOrderCartItemAdapter(List<ItemDetails_Model> orderItemDetailsArrayListt) {
        this.orderItemDetailsArrayList = orderItemDetailsArrayListt;
    }

    public static class MenuItemViewHolder extends RecyclerView.ViewHolder {
        public AddItemInCartListitemBinding binding;

        public MenuItemViewHolder(AddItemInCartListitemBinding bindingg) {
            super(bindingg.getRoot());
            this.binding = bindingg;
        }
    }

    @Override
    public MenuItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        AddItemInCartListitemBinding binding = AddItemInCartListitemBinding.inflate(inflater, parent, false);
        return new MenuItemViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(MenuItemViewHolder holder, int position) {
        ItemDetails_Model menuItem = orderItemDetailsArrayList.get(position);

        holder.binding.menuItemNameTextview.setText(String.valueOf(menuItem.getItemname()));
        holder.binding.totalPriceTextview.setText(String.valueOf(menuItem.getTotalprice()));
        holder.binding.itempriceTextview.setText(String.valueOf(menuItem.getPrice()));
        holder.binding.quantityTextview.setText(String.valueOf(menuItem.getQuantity()));



        // Additional binding setup for other views
    }

    @Override
    public int getItemCount() {
        return orderItemDetailsArrayList.size();
    }
}
