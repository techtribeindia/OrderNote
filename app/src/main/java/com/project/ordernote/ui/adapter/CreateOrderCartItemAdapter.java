package com.project.ordernote.ui.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.project.ordernote.data.model.ItemDetails_Model;
import com.project.ordernote.databinding.AddItemInCartListitemBinding;
import com.project.ordernote.utils.ItemTouchHelperAdapterInterface;
import com.project.ordernote.utils.WeightConverter;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class CreateOrderCartItemAdapter extends RecyclerView.Adapter<CreateOrderCartItemAdapter.MenuItemViewHolder> implements ItemTouchHelperAdapterInterface {

    private List<ItemDetails_Model> orderItemDetailsArrayList;
    Handler mHandler;
    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    public CreateOrderCartItemAdapter(List<ItemDetails_Model> orderItemDetailsArrayListt) {
        this.orderItemDetailsArrayList = orderItemDetailsArrayListt;
        this. currencyFormat = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    }

    public void setData(List<ItemDetails_Model> data) {
        this.orderItemDetailsArrayList = data;

        notifyDataSetChanged();

    }

    @Override
    public void onItemDismiss(int position) {
        sendHandlerMessage("CreateOrderItem_Delete" , position);
        //this.orderItemDetailsArrayList.remove(position);
      //  notifyItemRemoved(position);
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }



    private void sendHandlerMessage(String bundlestr, int pos) {
        //Log.e(Constants.TAG, "createBillDetails in cartaItem 1");

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("fromadapter", bundlestr);
        bundle.putInt("position", pos);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
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

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(MenuItemViewHolder holder, int position) {
        ItemDetails_Model menuItem = orderItemDetailsArrayList.get(position);

        holder.binding.menuItemNameTextview.setText(String.valueOf(menuItem.getItemname()));
        holder.binding.totalPriceTextview.setText(String.valueOf(currencyFormat.format(menuItem.getTotalprice())));
        holder.binding.itempriceTextview.setText(String.valueOf(currencyFormat.format(menuItem.getPrice())));
        holder.binding.quantityTextview.setText(String.valueOf(menuItem.getQuantity()));


        if(!String.valueOf(menuItem.getGrossweight()).isEmpty()){
            holder.binding.weightTextview.setText(String.valueOf(WeightConverter.ConvertGramsToKilograms(String.valueOf(menuItem.getGrossweight()))) +"Kg ");
        }
        else  if(!String.valueOf(menuItem.getNetweight()).isEmpty()){
            holder.binding.weightTextview.setText(String.valueOf(menuItem.getNetweight()));

        }
        else  if(!String.valueOf(menuItem.getPortionsize()).isEmpty()){
            holder.binding.weightTextview.setText(String.valueOf(menuItem.getPortionsize()));

        }




        if((orderItemDetailsArrayList.size()-1) == position){
            holder.binding.divider.setVisibility(View.INVISIBLE);
        }
        else{
            holder.binding.divider.setVisibility(View.VISIBLE);
        }

        // Additional binding setup for other views
    }

    @Override
    public int getItemCount() {
        return orderItemDetailsArrayList.size();
    }
}
