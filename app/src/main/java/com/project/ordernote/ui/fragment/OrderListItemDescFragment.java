package com.project.ordernote.ui.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.motion.widget.MotionLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.card.MaterialCardView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.Timestamp;
import com.google.gson.Gson;
import com.project.ordernote.R;
import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.ui.adapter.OrderItemListAdapter;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.DatabaseReference;
import com.project.ordernote.utils.SessionManager;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;

import java.util.Calendar;
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
    LinearLayout dialogButtonLayout,dialogCancelAndPlaceAgainButtonLayout,dialogEditRequestButtonLayout,dialogCancelAndApproveLayout,dialogCancelOnlyButtonLayout;
    private String selectedScreen;
    private SessionManager sessionManager;
    private MotionLayout motionLayout;
    private MaterialCardView banner;
    TextView dialogTransportNameText;
    TextView dialogMobileNoText;
    TextView dialogTrucckNoText;

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

            bundle.putString("fragment", selectedScreen);
            bundle.putString("status", status);
            message.setData(bundle);
            mHandler.sendMessage(message);
        } else {
            Log.e(TAG, "Handler is null, cannot send message");
        }
    }

    private Handler newHandler() {
        return new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                Bundle bundle = message.getData();
                Toast.makeText(requireActivity(), String.valueOf(bundle.getString("DispatchFragment")), Toast.LENGTH_SHORT).show();
                if (Objects.equals(bundle.getString("fragment"), "DispatchFragment"))
                {
                    dialogTransportNameText.setText(bundle.getString(DatabaseReference.transportname_OrderDetails) != null ? bundle.getString(DatabaseReference.transportname_OrderDetails) : "");
                    dialogMobileNoText.setText(bundle.getString(DatabaseReference.drivermobileno_OrderDetails) != null ? bundle.getString(DatabaseReference.drivermobileno_OrderDetails) : "");
                    dialogTrucckNoText.setText(bundle.getString(DatabaseReference.truckno_OrderDetails) != null ? bundle.getString(DatabaseReference.truckno_OrderDetails) : "");
                }
                return false;
            }
        });
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireActivity());

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
        motionLayout = view.findViewById(R.id.motionLayout);
        banner = view.findViewById(R.id.banner);
        hideBanner();

        // Parse the JSON data and set it to the respective UI components
        if (orderJson != null) {
            Gson gson = new Gson();
            OrderDetails_Model order = gson.fromJson(orderJson, OrderDetails_Model.class);

            if (order != null) {


                ImageView dispatchSelection = view.findViewById(R.id.dispatch_selection);

                TextView orderIdTextView = view.findViewById(R.id.dialog_orderid);
                TextView buyerNameTextView = view.findViewById(R.id.dialog_buyername);
                TextView buyerAddressTextView = view.findViewById(R.id.dialog_buyeraddress);
                TextView orderPriceTextView = view.findViewById(R.id.dialog_total);

                dialogTransportNameText = view.findViewById(R.id.dialog_transportname_text);
                dialogMobileNoText = view.findViewById(R.id.dialog_driver_mobileno_text);
                dialogTrucckNoText = view.findViewById(R.id.dialog_truckno_text);

                RelativeLayout backbut = view.findViewById(R.id.dialog_back);
                RelativeLayout rejectbut = view.findViewById(R.id.dialog_reject);
                RelativeLayout accepttbut = view.findViewById(R.id.dialog_accept);

                dialogOrderStatusCard = view.findViewById(R.id.dialog_orderstatus_card);
                dialogOrderStatusText = view.findViewById(R.id.dialog_orderstatus_text);
                dialogButtonLayout = view.findViewById(R.id.dialog_button_layout);

                dialogCancelAndPlaceAgainButtonLayout = view.findViewById(R.id.dialog_cancel_and_reapeal_button_layout);
                RelativeLayout dialogCancel = view.findViewById(R.id.dialog_cancel);
                RelativeLayout dialogPlaceAgain = view.findViewById(R.id.dialog_place_again);

                dialogCancelAndApproveLayout = view.findViewById(R.id.dialog_approve_and_deny_button_layout);
                RelativeLayout dialogCancel1 = view.findViewById(R.id.dialog_cancel1);
                RelativeLayout dialogApprove = view.findViewById(R.id.dialog_approve);

                dialogEditRequestButtonLayout = view.findViewById(R.id.dialog_edit_request_button_layout);
                RelativeLayout dialogEditRequest = view.findViewById(R.id.dialog_edit_request);

                dialogCancelOnlyButtonLayout = view.findViewById(R.id.dialog_cancel_only_button_layout);
                RelativeLayout dialogCancelOnlyBut = view.findViewById(R.id.dialog_cancel_only_but);

                Button approveButton = view.findViewById(R.id.approve_button);
                Button denyButton = view.findViewById(R.id.deny_button);

                // Set up click listeners
                approveButton.setOnClickListener(v -> {
                    orderDetailEditRequestModel(order.getOrderid(),Constants.editapproved_dispatchstatus);
                });

                denyButton.setOnClickListener(v -> {
                    orderDetailEditRequestModel(order.getOrderid(),Constants.dispatched_dispatchstatus);
                });

                if(order.getStatus().equalsIgnoreCase(Constants.created_status) )
                {
                    if(sessionManager.getRole().equalsIgnoreCase(Constants.staff_role))
                    {
                        dialogButtonLayout.setVisibility(View.VISIBLE);

                    }
                    else
                    {
                        dialogOrderStatusCard.setVisibility(View.VISIBLE);
                        dialogOrderStatusText.setText(String.valueOf("Only staff can Accept or Reject the Order, you can cancel the order in \n settings -> Date Wise Orders"));
                    }
                }
                if(order.getStatus().equalsIgnoreCase(Constants.rejected_status) && sessionManager.getRole().equalsIgnoreCase(Constants.admin_role))
                {
                    dialogCancelAndPlaceAgainButtonLayout.setVisibility(View.VISIBLE);
                }
                if(order.getStatus().equalsIgnoreCase(Constants.placed_status))
                {

                    if(sessionManager.getRole().equalsIgnoreCase(Constants.staff_role))
                    {
                        if (isToday(order.getOrderplaceddate()) && order.getDispatchstatus().equalsIgnoreCase(Constants.dispatched_dispatchstatus))
                        {
                            dialogEditRequestButtonLayout.setVisibility(View.VISIBLE);
                        }

                    }
                    else if(sessionManager.getRole().equalsIgnoreCase(Constants.admin_role))
                    {
                        dialogCancelOnlyButtonLayout.setVisibility(View.VISIBLE);
                        if(order.getDispatchstatus().equalsIgnoreCase(Constants.editrequested_dispatchstatus))
                        {
                            showBanner();
                        }
                    }

                }

                dialogTransportNameText.setText(order.getTransportname() != null && !Objects.equals(order.getTransportname(), "") ? order.getTransportname() : "----");
                dialogMobileNoText.setText(order.getDrivermobileno() != null && !Objects.equals(order.getDrivermobileno(), "") ? order.getDrivermobileno() : "----");
                dialogTrucckNoText.setText(order.getTruckno() != null && !Objects.equals(order.getTruckno(), "") ? order.getTruckno() : "----");

                RecyclerView orderitem_recyclerView = view.findViewById(R.id.dialog_recycler_view);
                orderitem_recyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 1));
                orderitem_recyclerView.setAdapter(orderItemListAdapter);

                orderIdTextView.setText(order.getOrderid());
                buyerNameTextView.setText(order.getBuyername());
                buyerAddressTextView.setText(order.getBuyeraddress());
                orderPriceTextView.setText(String.valueOf(order.getTotalprice()));
                orderItemListAdapter.setOrders(order.getItemdetails());

                dispatchSelection.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(sessionManager.getRole().equalsIgnoreCase(Constants.staff_role))
                        {
                            if(order.getDispatchstatus().equalsIgnoreCase(Constants.editapproved_dispatchstatus)||order.getStatus().equalsIgnoreCase(Constants.created_status))
                            {
                                openDispatchSelectionDialog(order.getOrderid(),order.getTransportname(),order.getDrivermobileno(),order.getTruckno(),order.getDispatchstatus());
                            }
                          else if(order.getDispatchstatus().equalsIgnoreCase(Constants.editrequested_dispatchstatus))
                            {
                                showSnackbar(view,"Admin yet to give access to edit");
                            }
                          else {
                                if(isToday(order.getOrderplaceddate()))
                                {
                                    showSnackbar(view,"Please give a request to edit dispatch");
                                }
                                else
                                {
                                    showSnackbar(view,"only ask access to edit for the current date");
                                }

                            }
                        }
                        else
                        {
                            showSnackbar(view,"Only staff can update Dispatch Details");
                        }

                    }
                });

                backbut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Objects.requireNonNull(getDialog()).dismiss();
                        Toast.makeText(requireActivity(), "Back button clicked", Toast.LENGTH_SHORT).show();
                    }
                });

                // Reject button with confirmation dialog
                rejectbut.setOnClickListener(v -> showConfirmationDialog(
                        "Reject Order",
                        "Are you sure you want to reject this order?",
                        () -> handleOrderRejection(order.getOrderid(),Constants.rejected_status)  // Call handleOrderRejection() on Yes
                ));

                accepttbut.setOnClickListener(v -> {
                    boolean acceptStatus = false;
                    String TransportNameText = String.valueOf(dialogTransportNameText.getText());
                    String DriverMobileNumber = String.valueOf(dialogMobileNoText.getText());
                    String TruckNumber = String.valueOf(dialogTrucckNoText.getText());

                    if ((TransportNameText.equals("----") && DriverMobileNumber.equals("----") && TruckNumber.equals("----")) || (TransportNameText.isEmpty() && DriverMobileNumber.isEmpty() && TruckNumber.isEmpty()))
                    {
                        showSnackbar(view,"Please update Dispatch Details");
                        return;
                    }

                    showConfirmationDialog(
                            "Accept Order",
                            "Are you sure you want to accept this order?",
                            () -> handleOrderAcceptance(order.getOrderid(),Constants.placed_status)
                    );
                });

                dialogCancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showConfirmationDialog(
                                "Order Cancellation",
                                "Do you want to cancel the order? \n Order Cancellation cannot be reversed",
                                () -> handleOrderCancellation(order.getOrderid(),Constants.cancelled_dispatchstatus)
                        );
                    }
                });
                dialogCancelOnlyBut.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showConfirmationDialog(
                                "Order Cancellation",
                                "Do you want to cancel the order? \n Order Cancellation cannot be reversed",
                                () -> handleOrderCancellation(order.getOrderid(),Constants.cancelled_dispatchstatus)
                        );
                    }
                });
                dialogCancel1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showConfirmationDialog(
                                "Order Cancellation",
                                "Do you want to cancel the order? \n Order Cancellation cannot be reversed",
                                () -> handleOrderCancellation(order.getOrderid(),Constants.cancelled_dispatchstatus)
                        );
                    }
                });

                dialogPlaceAgain.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showConfirmationDialog(
                                "Accept Order",
                                "Are you sure you want to place this order again?",
                                () -> handleOrderPlace(order.getOrderid(),Constants.created_status)
                        );
                    }
                });

                dialogApprove.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {

                    }
                });

                dialogEditRequest.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        showConfirmationDialog(
                                "Dispatch Edit Request",
                                "Request Admin to edit the Dispatch Details",
                                () -> handleEditRequest(order.getOrderid(),Constants.editrequested_dispatchstatus)
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

    private void openDispatchSelectionDialog(String orderid, String transportname, String drivermobileno, String truckno, String dispatchstatus) {

        try{
            Handler handler = newHandler();
            DispatchFragment dialogFragment = new DispatchFragment();
            dialogFragment.setDispatchDetails(orderid,transportname,drivermobileno,truckno,dispatchstatus);
            dialogFragment.setmHandler(handler,selectedScreen);
            dialogFragment.show(getParentFragmentManager(), "DispatchSelectionDialogFragment");

        }
        catch (Exception e){
            e.printStackTrace();
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

    public void handleOrderAcceptance(String orderid,String status)
    {
        orderDetailAcceptOrderModel(orderid,status);
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
    public void handleEditRequest(String orderid, String DispatchStatus)
    {
        orderDetailEditRequestModel(orderid,DispatchStatus);
    }

    private void orderDetailAcceptOrderModel(String orderid, String status) {
        orderDetails_viewModel.acceptOrder(orderid,status).observe(this, this::observeOrderDetails);
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

    private void orderDetailEditRequestModel(String orderid, String DispatchStatus)
    {
        orderDetails_viewModel.placeEditRequest( orderid,DispatchStatus).observe(this, this::observeOrderDetails);
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
                dialogCancelOnlyButtonLayout.setVisibility(View.VISIBLE);
                dialogOrderStatusText.setText(String.valueOf(resource.data));
                hideBanner();
                Toast.makeText(requireActivity(), "Success in fetching orders", Toast.LENGTH_SHORT).show();
//                        ordersListAdapter.setOrders(resource.data);
                break;
            case ERROR:
                Toast.makeText(requireActivity(), "Error in fetching orders "+resource.data, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showSnackbar(View view, String message) {
        Snackbar snackbar = Snackbar.make(view, message, Snackbar.LENGTH_LONG);
        snackbar.setAction("X", v -> snackbar.dismiss());
        snackbar.setActionTextColor(getResources().getColor(R.color.colorAccent)); // optional: set the action color

        // Get the Snackbar's layout view
        View snackbarView = snackbar.getView();

        // Change the Snackbar's position to top and add top margin
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) snackbarView.getLayoutParams();
        params.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        int marginInDp = (int) (30 * getResources().getDisplayMetrics().density); // Convert 20dp to pixels
        params.setMargins(0, marginInDp, 0, 0);
        snackbarView.setLayoutParams(params);

        snackbar.show();
    }
    private static boolean isToday(Timestamp timestamp) {
        Calendar orderDate = Calendar.getInstance();
        orderDate.setTime(timestamp.toDate());

        Calendar today = Calendar.getInstance();

        return orderDate.get(Calendar.YEAR) == today.get(Calendar.YEAR) &&
                orderDate.get(Calendar.MONTH) == today.get(Calendar.MONTH) &&
                orderDate.get(Calendar.DAY_OF_MONTH) == today.get(Calendar.DAY_OF_MONTH);
    }
    private void showBanner() {
        Toast.makeText(requireActivity(), "Show Banner", Toast.LENGTH_SHORT).show();
        motionLayout.setTransition(R.id.transition);
        motionLayout.transitionToEnd();
    }

    private void hideBanner() {
        motionLayout.setTransition(R.id.transition);
        motionLayout.transitionToStart();
    }
}
