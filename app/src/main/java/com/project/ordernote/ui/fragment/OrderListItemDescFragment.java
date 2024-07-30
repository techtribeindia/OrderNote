package com.project.ordernote.ui.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.project.ordernote.R;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.ui.adapter.OrderItemListAdapter;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link OrderListItemDescFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class OrderListItemDescFragment extends DialogFragment {
    private static final String TAG = "OrderListItemDescFragment";
    private String orderJson;
    private OrderItemListAdapter orderItemListAdapter;
    private OrderDetails_ViewModel orderDetails_viewModel;
    private Handler mHandler;

    public OrderListItemDescFragment() {

    }

    public static OrderListItemDescFragment newInstance(String orderJson) {
        OrderListItemDescFragment fragment = new OrderListItemDescFragment();
        Bundle args = new Bundle();
        args.putString("order_json", orderJson);
        fragment.setArguments(args);
        return fragment;
    }

    public void setmHandler(Handler mHandler) {
        this.mHandler = mHandler;
        Log.d(TAG, "Handler set: " + mHandler);
    }
    public void sendHandlerMessage(String status) {
        if (mHandler != null) { // Null check to prevent NullPointerException
            Message message = new Message();
            Bundle bundle = new Bundle();
            bundle.putString("fragment", "OrderListItemDescFragment");
            bundle.putString("status", status);
            message.setData(bundle);
            mHandler.sendMessage(message);
        } else {
            Log.e(TAG, "Handler is null, cannot send message");
        }
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            orderJson = getArguments().getString("order_json");
            Log.d(TAG, "Received order JSON: " + orderJson);
        } else {
            Log.e(TAG, "No arguments found");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        orderDetails_viewModel = new ViewModelProvider(requireActivity()).get(OrderDetails_ViewModel.class);
        View view = inflater.inflate(R.layout.fragment_order_list_item_desc, container, false);
        orderItemListAdapter = new OrderItemListAdapter();


        // Parse the JSON data and set it to the respective UI components
        if (orderJson != null) {
            Gson gson = new Gson();
            OrderDetails_Model order = gson.fromJson(orderJson, OrderDetails_Model.class);

            if (order != null) {
                TextView orderIdTextView = view.findViewById(R.id.dialog_orderid);
                TextView buyerNameTextView = view.findViewById(R.id.dialog_buyername);
                TextView buyerAddressTextView = view.findViewById(R.id.dialog_buyeraddress);
                TextView orderPriceTextView = view.findViewById(R.id.dialog_total);
                RelativeLayout backbut = view.findViewById(R.id.dialog_back);
                RelativeLayout rejectbut = view.findViewById(R.id.dialog_reject);
                RelativeLayout accepttbut = view.findViewById(R.id.dialog_accept);
                EditText transport_name = view.findViewById(R.id.dialog_transportname);
                EditText driver_mobieno = view.findViewById(R.id.dialog_driver_mobileno);
                EditText truck_no = view.findViewById(R.id.dialog_truckno);

                String transporName = transport_name.getText().toString();
                String driverMobieno = driver_mobieno.getText().toString();
                String truckNo = truck_no.getText().toString();
                // Check for empty transport details


                RecyclerView orderitem_recyclerView = view.findViewById(R.id.dialog_recycler_view);
                orderitem_recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
                orderitem_recyclerView.setAdapter(orderItemListAdapter);

                orderIdTextView.setText(order.getOrderid());
                buyerNameTextView.setText(order.getBuyername());
                buyerAddressTextView.setText(order.getBuyeraddress());
                orderPriceTextView.setText(String.valueOf(order.getTotalprice()));
                orderItemListAdapter.setOrders(order.getItemdetails());

                // Back button dismiss dialog

                backbut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getDialog().dismiss();

                    }
                });

                // Reject button with confirmation dialog
                rejectbut.setOnClickListener(v -> showConfirmationDialog(
                        "Reject Order",
                        "Are you sure you want to reject this order?",
                        () -> handleOrderRejection(order.getOrderid(),"ORDERREJECTED")  // Call handleOrderRejection() on Yes
                ));

                accepttbut.setOnClickListener(v -> {
                    boolean Dispatchstatus = true;
                    if (transporName.isEmpty() && driverMobieno.isEmpty() && truckNo.isEmpty()) {
                        Dispatchstatus = false;
                    }
                    Log.d("transporName", transporName);
                    Log.d("driverMobieno", driverMobieno);
                    Log.d("truckNo", truckNo);
                    Log.d("Dispatchstatus", String.valueOf(Dispatchstatus));
                    if (!Dispatchstatus) {
                        new AlertDialog.Builder(requireContext())
                                .setTitle("Dispatch Details")
                                .setMessage("Please enter Transport Name or Driver Mobile.No or Truck.No")
                                .setNegativeButton("OK", (dialog, which) -> dialog.dismiss())
                                .show();
                        return;
                    }
                    showConfirmationDialog(
                            "Accept Order",
                            "Are you sure you want to accept this order?",
                            () -> handleOrderAcceptance(transporName,driverMobieno,truckNo,order.getOrderid(),"ORDERPLACED")
                    );
                });

            } else {
                Log.e(TAG, "Failed to parse order JSON into OrderDetails_Model");
            }
        } else {
            Log.e(TAG, "Order JSON is null");
        }

        return view;
    }



    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Removes default background
        }
    }
    private void showConfirmationDialog(String title, String message, Runnable onConfirm) {
        new AlertDialog.Builder(requireContext())
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton("Yes", (dialog, which) -> onConfirm.run())
                .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                .show();
    }

    public void handleOrderAcceptance(String transporName, String driverMobieno, String truckNo,String orderid,String orderplaced)
    {
        orderDetailAcceptOrderModel(transporName, driverMobieno, truckNo,  orderplaced,orderid);
    }

    public void handleOrderRejection(String orderid,String orderrejected)
    {
        Toast.makeText(requireActivity(), orderid, Toast.LENGTH_LONG).show();
        orderDetailRejectOrderModel(orderid , orderrejected);
    }

    private void orderDetailAcceptOrderModel(String transporName, String driverMobieno, String truckNo, String orderid, String orderrejected) {
        orderDetails_viewModel.acceptOrder(transporName, driverMobieno, truckNo, orderid, orderrejected).observe(this, this::observeOrderDetails);
    }

    private void orderDetailRejectOrderModel(String orderid, String orderrejected) {
      //  orderDetails_viewModel.rejectOrder(orderid, orderrejected).observe(this, this::observeOrderDetails);
        orderDetails_viewModel.removeOrderFromLiveData(orderid);
    }

    private void observeOrderDetails(ApiResponseState_Enum<String> resource) {

        switch (resource.status) {
            case LOADING:
                Toast.makeText(requireActivity(), "Loading Orders", Toast.LENGTH_SHORT).show();
                break;
            case SUCCESS:
                sendHandlerMessage("success");
                Toast.makeText(requireActivity(), "Success in fetching orders", Toast.LENGTH_SHORT).show();
//                        ordersListAdapter.setOrders(resource.data);
                break;
            case ERROR:
                Toast.makeText(requireActivity(), "Error in fetching orders "+resource.data, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
