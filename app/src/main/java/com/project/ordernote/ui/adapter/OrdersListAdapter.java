package com.project.ordernote.ui.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.project.ordernote.R;
import com.project.ordernote.data.model.ItemDetails_Model;
import com.project.ordernote.data.model.OrderDetails_Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.OrderViewHolder> {
    private List<OrderDetails_Model> orders = new ArrayList<>();

    Handler mHandler;


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.orderlist_items, parent, false);
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        OrderDetails_Model order = orders.get(position);
        holder.orderId.setText(order.getTokenno() != null ? order.getTokenno() : "N/A");
        holder.buyerName.setText(order.getBuyername() != null ? order.getBuyername() : "N/A");
        holder.buyerAddress.setText(order.getBuyeraddress() != null ? order.getBuyeraddress() : "N/A");
        holder.orderQty.setText(String.valueOf(order.getTotalqty()));
        holder.orderPrice.setText(String.valueOf(order.getTotalprice()));
        List<ItemDetails_Model> itemDetailsList = order.getItemdetails();
        if (itemDetailsList != null && !itemDetailsList.isEmpty()) {
            for (ItemDetails_Model itemDetail : itemDetailsList) {
                Log.d("Item Detail", "Menu Item Key: " + itemDetail.getMenuitemkey());
                Log.d("Item Detail", "Menu Type: " + itemDetail.getMenutype());
                Log.d("Item Detail", "Gross Weight: " + itemDetail.getGrossweight());
                Log.d("Item Detail", "Net Weight: " + itemDetail.getNetweight());
                Log.d("Item Detail", "Price per Kg: " + itemDetail.getPriceperkg());
                Log.d("Item Detail", "Price: " + itemDetail.getPrice());
            }
        } else {
            Log.d("Item Detail", "No item details available");
        }
        holder.ViewBill.setOnClickListener(view -> {
          //  showOrderDetailsDialog(view.getContext(), order);
            sendHandlerMessage(position);
        });
    }

    private void sendHandlerMessage(int position) {


        Message message = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("fromadapter","openOrderDetailsDialog");
        bundle.putInt("position" , position);
        message.setData(bundle);
        mHandler.sendMessage(message);
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(List<OrderDetails_Model> orders) {
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
        TextView orderId, orderStatus,buyerName,buyerAddress,orderQty,orderPrice;
        Button ViewBill;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            orderId = itemView.findViewById(R.id.invoice_number);
            buyerName = itemView.findViewById(R.id.buyer_name);
            buyerAddress = itemView.findViewById(R.id.buyer_address);
            orderQty = itemView.findViewById(R.id.order_qty);
            orderPrice = itemView.findViewById(R.id.order_amount);
            ViewBill = itemView.findViewById(R.id.view_bill);
        }
    }
    private void showOrderDetailsDialog(Context context, OrderDetails_Model order) {
        // Create an instance of LayoutInflater
        LayoutInflater inflater = LayoutInflater.from(context);
        // Inflate the dialog layout from the XML file
        View dialogView = inflater.inflate(R.layout.dialog_order_details, null);

        // Find the TextViews in the dialog layout and set their text
        TextView orderId = dialogView.findViewById(R.id.dialog_order_id);
        TextView buyerName = dialogView.findViewById(R.id.dialog_buyer_name);
        TextView buyerAddress = dialogView.findViewById(R.id.dialog_buyer_address);
        TextView orderQty = dialogView.findViewById(R.id.dialog_order_qty);
        TextView orderPrice = dialogView.findViewById(R.id.dialog_order_price);

        orderId.setText("Order ID: " + order.getTokenno());
        buyerName.setText("Buyer Name: " + order.getBuyername());
        buyerAddress.setText("Buyer Address: " + order.getBuyeraddress());
        orderQty.setText("Order Quantity: " + order.getTotalqty());
        orderPrice.setText("Order Price: " + order.getTotalprice());

        // Build the dialog
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Order Details")
                .setView(dialogView)
                .setPositiveButton("OK", (dialog, which) -> dialog.dismiss());

        // Show the dialog
        builder.create().show();
    }
}
