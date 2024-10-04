package com.project.ordernote.ui.adapter;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.recyclerview.widget.RecyclerView;

import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.databinding.BuyerdetailslistitemBinding;
import com.project.ordernote.databinding.BuyerdetailslistitemBinding;
import com.project.ordernote.utils.ItemTouchHelperAdapterInterface;
import com.project.ordernote.utils.TextUtils;
import com.project.ordernote.utils.WeightConverter;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

public class BuyerList_Adapter extends RecyclerView.Adapter<BuyerList_Adapter.BuyerItemViewHolder> implements ItemTouchHelperAdapterInterface {

    private List<Buyers_Model> buyerDetailsArrayList;
    Handler mHandler;
 
    public BuyerList_Adapter(List<Buyers_Model> buyerDetailsArrayListt) {
        this.buyerDetailsArrayList = buyerDetailsArrayListt;
     }

    public void setData(List<Buyers_Model> data) {
        this.buyerDetailsArrayList = data;

        notifyDataSetChanged();

    }

    @Override
    public void onItemDismiss(int position) {
        String buyerKey = "";
        buyerKey = buyerDetailsArrayList.get(position).getUniquekey();
        sendHandlerMessage("BuyerDetailsList_Delete" , buyerKey);
        //this.buyerDetailsArrayList.remove(position);
        //  notifyItemRemoved(position);
    }

    public void setHandler(Handler handler) {
        this.mHandler = handler;
    }



    private void sendHandlerMessage(String bundlestr, String buyerKey ) {
        //Log.e(Constants.TAG, "createBillDetails in cartaItem 1");

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("fromadapter", bundlestr);
        bundle.putString("buyerkey", buyerKey);
        msg.setData(bundle);
        mHandler.sendMessage(msg);
    }




    public static class BuyerItemViewHolder extends RecyclerView.ViewHolder {
        public BuyerdetailslistitemBinding binding;

        public BuyerItemViewHolder(BuyerdetailslistitemBinding bindingg) {
            super(bindingg.getRoot());
            this.binding = bindingg;
        }
    }

    @Override
    public BuyerList_Adapter.BuyerItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        BuyerdetailslistitemBinding binding = BuyerdetailslistitemBinding.inflate(inflater, parent, false);
        return new BuyerItemViewHolder(binding);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(BuyerList_Adapter.BuyerItemViewHolder holder, @SuppressLint("RecyclerView") int position) {
        Buyers_Model buyerData = buyerDetailsArrayList.get(position);
        // Additional binding setup for other views
        if (buyerData != null) {
            String initials = TextUtils.getInitials(String.valueOf(buyerData.getName()));

            if(!buyerData.getName().equals("")){
                String pincode = buyerData.getPincode();
                if(!pincode.equals("")){
                    pincode = " - "+pincode+" . "+'\n';
                }
                holder. binding.selectedBuyerNameTextview.setText(String.valueOf(buyerData.getName()));
                holder.binding.selecedBuyerDetailsTextview.setText(String.valueOf(buyerData.getAddress1() +" , "+'\n'+buyerData.getAddress2()+pincode+"Ph:- +91"+buyerData.getMobileno()));
                holder.binding.intialTextview.setText(String.valueOf(initials));
                holder.binding.buyerIcon.setVisibility(View.GONE);
                holder.binding.intialTextview.setVisibility(View.VISIBLE);
            }
            else{
                holder.binding.selectedBuyerNameTextview.setText(String.valueOf("Select Buyer Name"));
                holder.binding.selecedBuyerDetailsTextview.setText(String.valueOf("xx , xxxxx  xxx xxxx , \nxxxx - xxxx . \nPh : - +91798xxxxxxx"));
                holder.binding.buyerIcon.setVisibility(View.VISIBLE);
                holder.binding.intialTextview.setVisibility(View.GONE);
            }

            if(position == (buyerDetailsArrayList.size() - 1)){
                holder.binding.buyerlistdivider.setVisibility(View.GONE);

            }
            else{
                holder.binding.buyerlistdivider.setVisibility(View.VISIBLE);
            }

        }

        holder.binding.buyerselectionCardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String buyerKey = "";
                buyerKey = buyerDetailsArrayList.get(position).getUniquekey();
                sendHandlerMessage("BuyerDetailsList_Selected" , buyerKey);

            }
        });
    }

    @Override
    public int getItemCount() {
        return buyerDetailsArrayList.size();
    }
}

