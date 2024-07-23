package com.project.ordernote.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.project.ordernote.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.OrderViewHolder> {
    private List<Map<String, Object>> orders = new ArrayList<>();

    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlist_items, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        Map<String, Object> order = orders.get(position);

        // Safely retrieve and convert the order ID and status
        String orderId = order.get("tokenno") != null ? order.get("tokenno").toString() : "N/A";
        String orderStatus = order.get("status") != null ? order.get("status").toString() : "N/A";

        holder.orderId.setText("#invoice "+orderId);
        
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(List<Map<String, Object>> orders) {
        this.orders = orders;
        notifyDataSetChanged();
    }

    static class OrderViewHolder extends RecyclerView.ViewHolder {
        TextView orderId, orderStatus;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.invoice_number);

        }
    }
}
