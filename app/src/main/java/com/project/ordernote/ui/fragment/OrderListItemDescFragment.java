package com.project.ordernote.ui.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.project.ordernote.R;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.ui.adapter.OrderItemListAdapter;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;

import java.util.Objects;

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
    CardView dialogOrderStatusCard;
    TextView dialogOrderStatusText;
    LinearLayout dialogButtonLayout,dialogCancelAndPlaceAgainButtonLayout,dialogEditRequestButtonLayout;
    private String selectedScreen;

    public OrderListItemDescFragment() {

    }

    public static OrderListItemDescFragment newInstance(String orderJson) {
        OrderListItemDescFragment fragment = new OrderListItemDescFragment();
        Bundle args = new Bundle();
        args.putString("order_json", orderJson);
        fragment.setArguments(args);
        return fragment;
    }

    public void setmHandler(Handler mHandler, String selectedOrderButton) {
        this.mHandler = mHandler;
        this.selectedScreen  = selectedOrderButton;
        Log.d("selectedOrderButton", String.valueOf(selectedOrderButton));
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

                TextView dialogTransportNameText = view.findViewById(R.id.dialog_transportname_text);
                TextView dialogMobileNoText = view.findViewById(R.id.dialog_driver_mobileno_text);
                TextView dialogTrucckNoText = view.findViewById(R.id.dialog_truckno_text);

                RelativeLayout backbut = view.findViewById(R.id.dialog_back);
                RelativeLayout rejectbut = view.findViewById(R.id.dialog_reject);
                RelativeLayout accepttbut = view.findViewById(R.id.dialog_accept);
                EditText transport_name = view.findViewById(R.id.dialog_transportname);
                EditText driver_mobieno = view.findViewById(R.id.dialog_driver_mobileno);
                EditText truck_no = view.findViewById(R.id.dialog_truckno);
                dialogOrderStatusCard = view.findViewById(R.id.dialog_orderstatus_card);
                dialogOrderStatusText = view.findViewById(R.id.dialog_orderstatus_text);
                dialogButtonLayout = view.findViewById(R.id.dialog_button_layout);

                dialogCancelAndPlaceAgainButtonLayout = view.findViewById(R.id.dialog_cancel_and_reapeal_button_layout);
                RelativeLayout dialogCancel = view.findViewById(R.id.dialog_cancel);
                RelativeLayout dialogPlaceAgain = view.findViewById(R.id.dialog_place_again);

                dialogEditRequestButtonLayout = view.findViewById(R.id.dialog_edit_request_button_layout);
                RelativeLayout dialogEditRequest = view.findViewById(R.id.dialog_edit_request);

                // Check for empty transport details

                if (Objects.equals(selectedScreen, "ORDERCREATED"))
                {
                    dialogButtonLayout.setVisibility(View.VISIBLE);
                }
                else if (Objects.equals(selectedScreen, "ORDERREJECTED"))
                {
                    dialogCancelAndPlaceAgainButtonLayout.setVisibility(View.VISIBLE);

                }
                else
                {
                    if (order.isEditrequest())
                    {
                        dialogOrderStatusCard.setVisibility(View.VISIBLE);
                        dialogOrderStatusText.setText(String.valueOf("The request to edit the dispatch details has already been submitted"));
                    }
                    else
                    {
                        dialogEditRequestButtonLayout.setVisibility(View.VISIBLE);
                    }


                }

                if (!Objects.equals(selectedScreen, "ORDERCREATED"))
                {
                    dialogTransportNameText.setVisibility(View.VISIBLE);
                    dialogMobileNoText.setVisibility(View.VISIBLE);
                    dialogTrucckNoText.setVisibility(View.VISIBLE);

                    transport_name.setVisibility(View.GONE);
                    driver_mobieno.setVisibility(View.GONE);
                    truck_no.setVisibility(View.GONE);
                }
                dialogTransportNameText.setText(order.getTransportname() != null ? order.getTransportname() : "");
                dialogMobileNoText.setText(order.getDrivermobileno() != null ? order.getDrivermobileno() : "");
                dialogTrucckNoText.setText(order.getTruckno() != null ? order.getTruckno() : "");

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

                    String transporName = transport_name.getText().toString();
                    String driverMobieno = driver_mobieno.getText().toString();
                    String truckNo = truck_no.getText().toString();

                    if (transporName.isEmpty() && driverMobieno.isEmpty() && truckNo.isEmpty()) {
                        Dispatchstatus = false;
                    }

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

                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showConfirmationDialog(
                                "Order Cancellation",
                                "Do you want to cancel the order? \n Order Cancellation cannot be reversed",
                                () -> handleOrderCancellation(order.getOrderid(),"ORDERCANCELLED")
                        );
                    }
                });

                dialogPlaceAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showConfirmationDialog(
                                "Accept Order",
                                "Are you sure you want to accept this order?",
                                () -> handleOrderPlace(order.getOrderid(),"ORDERCREATED")
                        );
                    }
                });
                dialogEditRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showConfirmationDialog(
                                "Dispatch Edit Request",
                                "Request Admin to edit the Dispatch Details",
                                () -> handleEditRequest(order.getOrderid())
                        );
                    }
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

    public void handleOrderAcceptance(String transporName, String driverMobieno, String truckNo,String orderid,String status)
    {
        orderDetailAcceptOrderModel(transporName, driverMobieno, truckNo,orderid,status);
    }

    public void handleOrderRejection(String orderid,String status)
    {

        orderDetailRejectOrderModel(orderid , status);
    }

    public void handleOrderCancellation(String orderid, String ststus)
    {
        orderDetailCancelOrderModel(orderid , ststus);
    }
    public void handleOrderPlace(String orderid, String ststus)
    {
        orderDetailPlaceOrderModel(orderid , ststus);
    }
    public void handleEditRequest(String orderid)
    {
        orderDetailEditRequestModel(orderid);
    }

    private void orderDetailAcceptOrderModel(String transporName, String driverMobieno, String truckNo, String orderid, String status) {
        orderDetails_viewModel.acceptOrder(transporName, driverMobieno, truckNo,orderid,status).observe(this, this::observeOrderDetails);
    }

    private void orderDetailRejectOrderModel(String orderid, String status) {
        orderDetails_viewModel.rejectOrder( orderid,status).observe(this, this::observeOrderDetails);
       // orderDetails_viewModel.removeOrderFromLiveData(orderid);
    }
    private void orderDetailCancelOrderModel(String orderid, String status)
    {
        orderDetails_viewModel.cancelOrder( orderid,status).observe(this, this::observeOrderDetails);
    }

    private void orderDetailPlaceOrderModel(String orderid, String status)
    {
        orderDetails_viewModel.placeOrder( orderid,status).observe(this, this::observeOrderDetails);
    }

    private void orderDetailEditRequestModel(String orderid)
    {
        orderDetails_viewModel.placeEditRequest( orderid).observe(this, this::observeOrderDetails);
    }

    private void observeOrderDetails(ApiResponseState_Enum<String> resource) {

        switch (resource.status) {
            case LOADING:
                Toast.makeText(requireActivity(), "Loading Orders", Toast.LENGTH_SHORT).show();
                break;
            case SUCCESS:
                sendHandlerMessage("success");
                dialogButtonLayout.setVisibility(View.GONE);
                dialogCancelAndPlaceAgainButtonLayout.setVisibility(View.GONE);
                dialogEditRequestButtonLayout.setVisibility(View.GONE);
                dialogOrderStatusCard.setVisibility(View.VISIBLE);
                dialogOrderStatusText.setText(String.valueOf(resource.data));
                Toast.makeText(requireActivity(), "Success in fetching orders", Toast.LENGTH_SHORT).show();
//                        ordersListAdapter.setOrders(resource.data);
                break;
            case ERROR:
                Toast.makeText(requireActivity(), "Error in fetching orders "+resource.data, Toast.LENGTH_SHORT).show();
                break;
        }
    }
}
