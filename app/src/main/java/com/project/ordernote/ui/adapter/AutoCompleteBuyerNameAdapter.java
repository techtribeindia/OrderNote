package com.project.ordernote.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.project.ordernote.R;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.model.Buyers_Model;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteBuyerNameAdapter extends ArrayAdapter<Buyers_Model> {


    List<Buyers_Model> buyerItemModelArrayList = new ArrayList<>();
    Boolean isResult_is_Zero = false ;
    Context context;
    private Handler handler;





    public AutoCompleteBuyerNameAdapter(Context mContext, List<Buyers_Model> buyerItemModelArrayListt) {
        super(mContext, 0);
        this.buyerItemModelArrayList = buyerItemModelArrayListt;
        this.context = mContext;
      }

    @NonNull
    @Override
    public Filter getFilter() {
        return menuFilter;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }




    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        convertView = LayoutInflater.from(getContext()).inflate(
                R.layout.autocomplete_textview_item, parent, false
        );
        Buyers_Model menuItemsModel = getItem(position);
        TextView textview = convertView.findViewById(R.id.textview);
        LinearLayout autoCompleteTextViewParent = convertView.findViewById(R.id.autoCompleteTextViewParent);


        textview.setText(menuItemsModel.getName());

        autoCompleteTextViewParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Buyers_Model menuItemsModel2 = getItem(position);

                sendHandlerMessage("closebuyeritemadapter" , menuItemsModel2.getUniquekey());
            }
        });


        return convertView;
    }



    private void sendHandlerMessage(String bundlestr, String itemkey) {
        //Log.e(Constants.TAG, "createBillDetails in cartaItem 1");

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("fromadapter", bundlestr);
        bundle.putString("buyeritemkey", itemkey);
        msg.setData(bundle);
        handler.sendMessage(msg);
    }


    @Override
    public int getCount() {
        return super.getCount();
    }

    private Filter menuFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<Buyers_Model> suggestions = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(buyerItemModelArrayList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (Buyers_Model item : buyerItemModelArrayList) {
                    if (item.getName().toLowerCase().contains(filterPattern) || item.getName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);

                    }

                }
            }

            if(suggestions.size()==0){
                Buyers_Model item = new Buyers_Model();
                suggestions.add(item);
                isResult_is_Zero = true;
            }
            else{
                isResult_is_Zero = false;

            }
            results.values = suggestions;
            results.count = suggestions.size();
            return results;
        }
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {

            clear();
            addAll((List) results.values);
            notifyDataSetChanged();
        }
        @Override
        public CharSequence convertResultToString(Object resultValue) {


            return ((Buyers_Model) resultValue).getName();
        }
    };
}

