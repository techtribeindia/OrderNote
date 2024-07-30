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
import androidx.constraintlayout.widget.ConstraintLayout;

import com.project.ordernote.R;
import com.project.ordernote.data.model.MenuItems_Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AutoCompleteMenuItemAdapter extends ArrayAdapter<MenuItems_Model> {


    List<MenuItems_Model> menuItemsModelArrayList = new ArrayList<>();
    Boolean isResult_is_Zero = false , filterMobileNo = false;
    Context context;
    private Handler handler;





    public AutoCompleteMenuItemAdapter(Context mContext, List<MenuItems_Model> menuItemsModelArrayListt) {
        super(mContext, 0);
        this.menuItemsModelArrayList = menuItemsModelArrayListt;
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
        MenuItems_Model menuItemsModel = getItem(position);
        TextView textview = convertView.findViewById(R.id.textview);
        LinearLayout autoCompleteTextViewParent = convertView.findViewById(R.id.autoCompleteTextViewParent);


        textview.setText(menuItemsModel.getItemname());

        autoCompleteTextViewParent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MenuItems_Model menuItemsModel2 = getItem(position);

                sendHandlerMessage("closemenuitemadapter" , menuItemsModel2.getItemkey());
            }
        });


        return convertView;
    }



    private void sendHandlerMessage(String bundlestr, String menuitemkey) {
        //Log.e(Constants.TAG, "createBillDetails in cartaItem 1");

        Message msg = new Message();
        Bundle bundle = new Bundle();
        bundle.putString("fromadapter", bundlestr);
        bundle.putString("menuitemkey", menuitemkey);
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
            List<MenuItems_Model> suggestions = new ArrayList<>();
            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(menuItemsModelArrayList);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (MenuItems_Model item : menuItemsModelArrayList) {
                    if (item.getItemname().toLowerCase().contains(filterPattern) || item.getItemname().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);

                    }

                }
            }

            if(suggestions.size()==0){
                MenuItems_Model item = new MenuItems_Model();
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


            return ((MenuItems_Model) resultValue).getItemname();
        }
    };
}

