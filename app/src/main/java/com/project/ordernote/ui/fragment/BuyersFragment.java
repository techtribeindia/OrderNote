package com.project.ordernote.ui.fragment;

import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.project.ordernote.data.local.LocalDataManager;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.databinding.FragmentBuyersBinding;
import com.project.ordernote.ui.adapter.BuyerList_Adapter;
import com.project.ordernote.ui.adapter.CreateOrderCartItemAdapter;
import com.project.ordernote.utils.AlertDialogUtil;
import com.project.ordernote.utils.SwipeToDeleteCallback;
import com.project.ordernote.viewmodel.Buyers_ViewModel;

import java.util.List;


public class BuyersFragment extends Fragment {


    FragmentBuyersBinding binding;

    Buyers_ViewModel buyersViewModel ;

    BuyerList_Adapter buyerListAdapter;


    boolean buyerDetailsFetchedSuccessfully = false;

    public BuyersFragment() {
        // Required empty public constructor
    }

    public static BuyersFragment newInstance(String param1, String param2) {
        BuyersFragment fragment = new BuyersFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        binding = FragmentBuyersBinding.inflate(inflater, container, false);
        return binding.getRoot();
       /// return inflater.inflate(R.layout.fragment_buyers, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        try {
            // Initialize ViewModels
            buyersViewModel = new ViewModelProvider(this).get(Buyers_ViewModel.class);

             List<Buyers_Model> buyersList = LocalDataManager.getInstance().getBuyers();

            if(buyersList.size() > 0){

                buyersViewModel.setBuyersListinMutableLiveData(buyersList);
            }
            else{
                buyersViewModel.getBuyersListFromRepository("vendor_1");
            }



        } catch (Exception e) {
            e.printStackTrace();
        }



        buyersViewModel.getBuyersListFromViewModel().observe(getViewLifecycleOwner(),  resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        buyerDetailsFetchedSuccessfully = false;
                        showProgressBar(true);
                        //Toast.makeText(requireActivity(), "Loading Buyer", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        setAdapterForBuyersRecyclerview(resource.data);
                        buyerDetailsFetchedSuccessfully = true;
                        showProgressBar(false);


                        //Toast.makeText(requireActivity(), "Success in fetching Buyer", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR:
                        buyerDetailsFetchedSuccessfully = false;
                        Toast.makeText(requireActivity(), "Error in fetching Buyer", Toast.LENGTH_SHORT).show();
                        showProgressBar(false);
                        break;
                }
            }
        });


    }

    private void setAdapterForBuyersRecyclerview(List<Buyers_Model> data) {
        if (!data.isEmpty()) {

            binding.buyerListRecyclerview.setVisibility(View.VISIBLE);
            binding.instructionTextviewBuyerListRecyclerview.setVisibility(View.GONE);
            if (buyerListAdapter != null) {
                buyerListAdapter.setData(data);
            } else {
                buyerListAdapter = new BuyerList_Adapter(data);
                binding.buyerListRecyclerview.setAdapter(buyerListAdapter);
                buyerListAdapter.setHandler(newHandler());
                binding.buyerListRecyclerview.setLayoutManager(new LinearLayoutManager(getContext()));


                ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new SwipeToDeleteCallback(buyerListAdapter, getContext()));
                itemTouchHelper.attachToRecyclerView(binding.buyerListRecyclerview);

            }


        }
        else {
            binding.buyerListRecyclerview.setVisibility(View.GONE);
            binding.instructionTextviewBuyerListRecyclerview.setVisibility(View.VISIBLE);


        }


        if (buyerListAdapter != null) {
            int totalItems = buyerListAdapter.getItemCount();
            int totalHeight = 0;

            // Iterate over all items in the adapter
            for (int i = 0; i < totalItems; i++) {
                View listItem = buyerListAdapter.createViewHolder(binding.buyerListRecyclerview, buyerListAdapter.getItemViewType(i)).itemView;
                listItem.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
                totalHeight += listItem.getMeasuredHeight()+25;
            }

            // Add some padding or spacing if needed
            int spacing = binding.buyerListRecyclerview.getItemDecorationCount() * 10; // Example spacing
            totalHeight += spacing;

            // Set the RecyclerView's height
            ViewGroup.LayoutParams params = binding.buyerListRecyclerview.getLayoutParams();
            params.height = totalHeight;
            binding.buyerListRecyclerview.setLayoutParams(params);
        }



    }

    private Handler newHandler() {
        Handler.Callback callback = new Handler.Callback() {

            @Override
            public boolean handleMessage(Message msg) {
                Bundle bundle = msg.getData();
                String data = bundle.getString("fromadapter");


                if(data.equals("BuyerDetailsList_Delete")){
                    try {
                        int position = bundle.getInt("position");

                        // Inside an Activity or Fragment
                        AlertDialogUtil.showCustomDialog(
                                requireActivity(),
                                "Delete Buyer ",
                                "Do you want to delete this buyer details permenantly .", "Delete", "No , Keep it","RED",
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Handle positive button click

                                        buyersViewModel.deleteBuyerDetailsFromDB(position);
                                        Toast.makeText(requireActivity(), "Remove", Toast.LENGTH_SHORT).show();
                                    }
                                },
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Handle negative button click
                                        buyerListAdapter.notifyItemChanged(position);
                                        Toast.makeText(requireActivity(), "Cancel", Toast.LENGTH_SHORT).show();

                                    }
                                }
                        );





                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }





                }

                return false;
            }
        };
        return new Handler(callback);
    }


    private void showProgressBar(boolean show) {
        try {
            if (show) {
                binding.progressbar.playAnimation();
                binding.progressbar.setVisibility(View.VISIBLE);
                binding.progressbarBacklayout.setVisibility(View.VISIBLE);
            } else {
                binding.progressbar.cancelAnimation();
                binding.progressbar.setVisibility(View.GONE);
                binding.progressbarBacklayout.setVisibility(View.GONE);
            }
        }
        catch (Exception e){
            e.printStackTrace();

        }
    }
}