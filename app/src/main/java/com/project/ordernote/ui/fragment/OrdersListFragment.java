package com.project.ordernote.ui.fragment;

import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.project.ordernote.R;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.databinding.FragmentOrdersBinding;
import com.project.ordernote.ui.adapter.OrdersListAdapter;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.SessionManager;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class OrdersListFragment extends Fragment {
    private OrderDetails_ViewModel orderDetails_viewModel;
    private OrdersListAdapter ordersListAdapter;
    private FragmentOrdersBinding binding;
    private OrderListItemDescFragment orderListItemDescFragment;
    private String selectedOrderButton = Constants.created_status;
    private SessionManager sessionManager;
    RecyclerView recyclerView;
    CardView messageCard;
    TextView messageText;
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
        sessionManager = new SessionManager(requireActivity());
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        orderDetails_viewModel = new ViewModelProvider(requireActivity()).get(OrderDetails_ViewModel.class);
        orderDetails_viewModel.setUserDetails(sessionManager.getVendorkey());
        binding = FragmentOrdersBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        orderListItemDescFragment = new OrderListItemDescFragment();
        Handler handler = newHandler();

        ordersListAdapter = new OrdersListAdapter(orderDetails_viewModel);
        recyclerView =binding.ordersRecyclerView;
        messageCard = binding.messageCard;
        messageText = binding.messageText;

        binding.ordersRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        binding.ordersRecyclerView.setAdapter(ordersListAdapter);

        setActiveButton(binding.pendingordersButton);
        orderDetails_viewModel.clearOrderDetails();
        try {
            orderDetailViewModel(Constants.created_status);
        }
        catch (Exception e ){
            e.printStackTrace();
        }

        binding.pendingordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                binding.searchEditText.setText("");
                setActiveButton(binding.pendingordersButton);
                orderDetails_viewModel.clearOrderDetails();
                orderDetailViewModel(Constants.created_status);
                selectedOrderButton = Constants.created_status;

            }
        });

        binding.rejectedordersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                binding.searchEditText.setText("");
                setActiveButton(binding.rejectedordersButton);
                orderDetails_viewModel.clearOrderDetails();
                orderDetailViewModel(Constants.rejected_status);
                selectedOrderButton = Constants.rejected_status;

            }
        });

        binding.searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String buyerName = charSequence.toString();

                orderDetails_viewModel.filterOrderWithBuyerName(buyerName);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        return view;
    }

    private Handler newHandler() {
        return new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                Bundle bundle = message.getData();

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

                        showProgressBar(true);
                        break;
                    case SUCCESS:


                        orderDetails_viewModel.setOrderDetails(resource);
                        ordersListAdapter.setOrders(resource.data,selectedOrderButton);
                        messageText.setText(resource.message);
                        if(Objects.equals(resource.message, ""))
                        {
                            recyclerView.setVisibility(View.VISIBLE);
                            messageCard.setVisibility(View.GONE);
                        }
                        else
                        {
                            messageCard.setVisibility(View.VISIBLE);
                            recyclerView.setVisibility(View.GONE);
                        }

                        showProgressBar(false);
                        break;
                    case ERROR:

                        messageCard.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                        messageText.setText(resource.message);
                        showSnackbar(getView(), resource.message);
                        showProgressBar(false);
                        break;
                }
            }
        });
    }

    private void orderDetailViewModel(String status) {
        if (Objects.equals(status, Constants.placed_status))
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

            orderDetails_viewModel.getOrdersByStatus_DateAndVendorKey(status, startTimestamp, endTimestamp,"vendor_1");

            return;
        }
        orderDetails_viewModel.getOrdersByStatus(status);
    }

    private void setActiveButton(TextView activeButton) {
        binding.pendingordersButton.setBackgroundResource(R.drawable.button_inactive);
        binding.rejectedordersButton.setBackgroundResource(R.drawable.button_inactive);


        binding.pendingordersButton.setTextColor(getResources().getColor(R.color.black));
        binding.rejectedordersButton.setTextColor(getResources().getColor(R.color.black));


        activeButton.setBackgroundResource(R.drawable.button_active);
        activeButton.setTextColor(getResources().getColor(R.color.white));
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
    private void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("X", v -> snackbar.dismiss());
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent)); // optional: set the action color

        // Get the Snackbar's layout view
        View snackbarView = snackbar.getView();

        // Check if the parent is CoordinatorLayout
        if (snackbarView.getLayoutParams() instanceof CoordinatorLayout.LayoutParams) {
            CoordinatorLayout.LayoutParams params = (CoordinatorLayout.LayoutParams) snackbarView.getLayoutParams();
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            int marginInDp = (int) (30 * getResources().getDisplayMetrics().density); // Convert 30dp to pixels
            params.setMargins(0, marginInDp, 0, 0);
            snackbarView.setLayoutParams(params);
        } else if (snackbarView.getLayoutParams() instanceof FrameLayout.LayoutParams) {
            // If it's a FrameLayout, handle it like before
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
            params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
            int marginInDp = (int) (30 * getResources().getDisplayMetrics().density); // Convert 30dp to pixels
            params.setMargins(0, marginInDp, 0, 0);
            snackbarView.setLayoutParams(params);
        }

        snackbar.show();
    }

}
