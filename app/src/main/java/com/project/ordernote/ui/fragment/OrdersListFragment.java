package com.project.ordernote.ui.fragment;

import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.Timestamp;
import com.project.ordernote.R;
import com.project.ordernote.databinding.FragmentOrdersBinding;
import com.project.ordernote.ui.adapter.OrdersListAdapter;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Objects;

public class OrdersListFragment extends Fragment {
    private OrderDetails_ViewModel orderDetails_viewModel;
    private OrdersListAdapter ordersListAdapter;
    private FragmentOrdersBinding binding;
    private OrderListItemDescFragment orderListItemDescFragment;
    private String selectedOrderButton = "CREATED";
    public OrdersListFragment() {
        // Required empty public constructor
    }


    public static OrdersListFragment newInstance(String param1, String param2) {
        OrdersListFragment fragment = new OrdersListFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        orderDetails_viewModel = new ViewModelProvider(requireActivity()).get(OrderDetails_ViewModel.class);
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        orderListItemDescFragment = new OrderListItemDescFragment();
        Handler handler = newHandler();

        ordersListAdapter = new OrdersListAdapter(orderDetails_viewModel);
//        ordersListAdapter.setmHandler(newHandler());
        binding.ordersRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.ordersRecyclerView.setAdapter(ordersListAdapter);

        setActiveButton(binding.pendingordersButton);

        try {
            orderDetailViewModel("CREATED");
        }
        catch (Exception e ){
            e.printStackTrace();
        }

        binding.pendingordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                setActiveButton(binding.pendingordersButton);
                orderDetailViewModel("CREATED");
                selectedOrderButton = "CREATED";
            }
        });

        binding.rejectedordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {

                setActiveButton(binding.rejectedordersButton);
                orderDetailViewModel("REJECTED");

                selectedOrderButton = "REJECTED";
            }
        });

        return view;
    }

    private Handler newHandler() {
        return new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                Bundle bundle = message.getData();
                Toast.makeText(requireActivity(), String.valueOf(bundle.getString("OrderListItemDescFragment")), Toast.LENGTH_SHORT).show();
                if (Objects.equals(bundle.getString("fragment"), "OrderListItemDescFragment"))
                {
                    if (Objects.equals(bundle.getString("status"), "success"))
                    {
                       // observeOrderDetails();
                    }
                }
                return false;
            }
        });
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ordersListAdapter.clearOrders();
        observeOrderDetails();
        orderDetails_viewModel.clearSelectedOrderJson();
        orderDetails_viewModel.getSelectedOrderJson().observe(getViewLifecycleOwner(), orderJson -> {
            if ((orderJson != null)&&(!orderJson.equals(""))) {
                OrderListItemDescFragment orderListItemDescFragment = OrderListItemDescFragment.newInstance(orderJson);
                orderListItemDescFragment.setmHandler(newHandler(),"DatwWiseOrderScreen");
                orderListItemDescFragment.show(getParentFragmentManager(), "DatwWiseOrderScreen");
            }
        });
    }

    private void observeOrderDetails() {
        orderDetails_viewModel.clearFromViewModel();
        orderDetails_viewModel.getOrdersByStatusFromViewModel().observe(getViewLifecycleOwner(), resource -> {
            ordersListAdapter.clearOrders();
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        Toast.makeText(requireActivity(), "Loading Orders", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        Toast.makeText(requireActivity(), "Success in fetching orders", Toast.LENGTH_SHORT).show();
                        ordersListAdapter.setOrders(resource.data,selectedOrderButton);
                        break;
                    case ERROR:
                        Toast.makeText(requireActivity(), "Error in fetching orders", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
    }

    private void orderDetailViewModel(String status) {
        if (status == " PLACED")
        {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

// Start of the day
            Date startDate = calendar.getTime();
            Timestamp startTimestamp = new Timestamp(startDate);

// End of the day
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            Date endDate = calendar.getTime();
            Timestamp endTimestamp = new Timestamp(endDate);

            orderDetails_viewModel.getOrdersByStatusAndDate(status, startTimestamp, endTimestamp);

            return;
        }
        orderDetails_viewModel.getOrdersByStatus(status);
    }

    private void setActiveButton(Button activeButton) {
        binding.pendingordersButton.setBackgroundResource(R.drawable.button_inactive);
        binding.rejectedordersButton.setBackgroundResource(R.drawable.button_inactive);


        binding.pendingordersButton.setTextColor(getResources().getColor(R.color.black));
        binding.rejectedordersButton.setTextColor(getResources().getColor(R.color.black));


        activeButton.setBackgroundResource(R.drawable.button_active);
        activeButton.setTextColor(getResources().getColor(R.color.white));
    }
}
