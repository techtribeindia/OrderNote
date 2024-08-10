package com.project.ordernote.ui.adapter;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.project.ordernote.R;
import com.project.ordernote.data.model.ItemDetails_Model;
import com.project.ordernote.data.model.MenuItems_Model;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.SessionManager;
import com.project.ordernote.viewmodel.MenuItems_ViewModel;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuItemListAdapter extends RecyclerView.Adapter<MenuItemListAdapter.OrderViewHolder> {
    private List<MenuItems_Model> orders = new ArrayList<>();
    MenuItems_ViewModel menuItemsViewModel;
    private String selectedScreen;
    Handler mHandler;
    private SessionManager sessionManager;

    public MenuItemListAdapter(MenuItems_ViewModel menuItemsViewModel) {
        this.menuItemsViewModel = menuItemsViewModel;
    }


    @NonNull
    @Override
    public OrderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.menuitemslist_items, parent, false);
        sessionManager = new SessionManager(parent.getContext());
        return new OrderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderViewHolder holder, int position) {
        MenuItems_Model order = orders.get(position);
        holder.menuitemName.setText(order.getItemname() != null ? order.getItemname() : "N/A");
        String price = String.valueOf(order.getUnitprice());
        String priceper = "";
        if(order.getItemtype().equalsIgnoreCase(Constants.priceperkg_itemtype))
        {
            float grossweight = order.getGrossweight();
            if(grossweight > 0)
            {
                grossweight = grossweight/1000;
                priceper = String.valueOf(grossweight)+" KG";
            }
            else
            {
                priceper = order.getPortionsize();
            }

        }

        if(order.getItemtype().equalsIgnoreCase(Constants.unitprice_itemtype))
        {
            priceper = order.getPortionsize();
            if(priceper == "")
            {
                float grossweight = order.getGrossweight();
                if (grossweight>0)
                {
                    grossweight = grossweight/1000;
                }

                priceper = String.valueOf(grossweight)+" KG";
            }

        }
            holder.priceInfo.setText("₹ "+price+" / "+ priceper);

//        holder.buyerName.setText(order.getBuyername() != null ? order.getBuyername() : "N/A");
//        holder.buyerAddress.setText(order.getBuyeraddress() != null ? order.getBuyeraddress() : "N/A");
//        holder.orderQty.setText(String.valueOf(order.getTotalquantity()));
//        holder.orderPrice.setText(String.valueOf(order.getTotalprice()));
//        if ((Objects.equals(order.getDispatchstatus(), Constants.editrequested_dispatchstatus)  && sessionManager.getRole().equalsIgnoreCase(Constants.admin_role)) || (Objects.equals(order.getDispatchstatus(), Constants.editapproved_dispatchstatus)  && sessionManager.getRole().equalsIgnoreCase(Constants.staff_role)))
//        {
//            holder.itemCard.setBackgroundResource(R.color.backgroundred);
//        }

        holder.menuSelectionCardView.setOnClickListener(view -> {
            menuItemsViewModel.setSelectedMenu(order);
//            sendHandlerMessage("Fetch");
        });
    }

    public void sendHandlerMessage(String status) {
        if (mHandler != null) { // Null check to prevent NullPointerException
            Message message = new Message();
            Bundle bundle = new Bundle();

            bundle.putString("fragment", selectedScreen);
            bundle.putString("status", status);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    public void setOrders(List<MenuItems_Model> orders, String selectedOrderButton) {
        this.orders = orders;
        this.selectedScreen  = selectedOrderButton;
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
        TextView menuitemName,priceInfo;
        CardView menuSelectionCardView;

        public OrderViewHolder(@NonNull View itemView) {
            super(itemView);
            menuitemName = itemView.findViewById(R.id.menuItemName_textview);
            priceInfo = itemView.findViewById(R.id.priceinfo);
            menuSelectionCardView = itemView.findViewById(R.id.menuselectionCardView);
        }
    }

}
