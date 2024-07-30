package com.project.ordernote.ui.adapter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.ordernote.R;
import com.project.ordernote.data.model.ItemDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;

import java.util.ArrayList;
import java.util.List;


public class OrderItemListAdapter extends RecyclerView.Adapter<OrderItemListAdapter.OrderViewHolder> {
    private List<ItemDetails_Model> orders = new ArrayList<>();
    Handler mHandler;

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.add_item_in_cart_listitem, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        ItemDetails_Model order = orders.get(position);
        holder.recycler_itemname.setText(order.getItemname() != null ? order.getItemname() : "N/A");

    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(List<ItemDetails_Model> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }
    public void clearOrders() {
        this.orders.clear();
        notifyDataSetChanged();
    }

    public Handler getmHandler() {
        return mHandler;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView recycler_itemname;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            recycler_itemname = itemView.findViewById(R.id.recycler_itemname);
        }
    }

}

