package com.project.ordernote.ui.fragment;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.project.ordernote.R;
import com.project.ordernote.databinding.FragmentDispatchBinding;
import com.project.ordernote.utils.ApiResponseState_Enum;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;

import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DispatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DispatchFragment extends DialogFragment {
    FragmentDispatchBinding binding;
    String orderid,transportname,drivermobileno,truckno,dispatchstatus;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private OrderDetails_ViewModel orderDetails_viewModel;
    private String selectedScreen="";
    private Handler mHandler;
    private String mParam1;
    private String mParam2;

    public DispatchFragment() {
        // Required empty public constructor
    }
    public void setmHandler(Handler mHandler, String selectedOrderButton) {
        this.mHandler = mHandler;
        this.selectedScreen  = selectedOrderButton;

    }

    public void sendHandlerMessage() {
        if (mHandler != null) { // Null check to prevent NullPointerException
            Message message = new Message();
            Bundle bundle = new Bundle();

            bundle.putString("fragment", "DispatchFragment");
            bundle.putString("orderid", orderid);
            bundle.putString("transporName", transportname);
            bundle.putString("driverMobieno", drivermobileno);
            bundle.putString("truckNo", truckno);
            message.setData(bundle);
            mHandler.sendMessage(message);
        }
    }

    public void setDispatchDetails(String orderid, String transportname, String drivermobileno, String truckno,String dispatchstatus)
    {
        this.orderid=orderid;
        this.transportname=transportname;
        this.drivermobileno=drivermobileno;
        this.truckno=truckno;
        this.dispatchstatus = dispatchstatus;
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DispatchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DispatchFragment newInstance(String param1, String param2) {
        DispatchFragment fragment = new DispatchFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        orderDetails_viewModel = new ViewModelProvider(requireActivity()).get(OrderDetails_ViewModel.class);
        binding = FragmentDispatchBinding.inflate(inflater, container, false);

        binding.transportName.setText(transportname != null ? transportname : "");
        binding.driverMobieno.setText(drivermobileno != null ? drivermobileno : "");
        binding.truckNo.setText(truckno != null ? truckno : "");

        binding.closeIcon.setOnClickListener(view -> {
            Objects.requireNonNull(getDialog()).dismiss();
        });

        binding.updateButton.setOnClickListener(view -> {
            boolean Dispatchstatus = true;

            String transporName = binding.transportName.getText().toString();
            String driverMobieno = binding.driverMobieno.getText().toString();
            String truckNo = binding.truckNo.getText().toString();



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
            transportname=transporName;
            drivermobileno=driverMobieno;
            truckno=truckNo;

            showConfirmationDialog(
                    "Accept Order",
                    "Are you sure you want to update the dispatch details?",
                    () -> handleOrderDispatch(orderid,transporName,driverMobieno,truckNo)
            );
        });

        return binding.getRoot();
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
           /* Window window = getDialog().getWindow();
            if (window != null) {
                WindowManager.LayoutParams params = window.getAttributes();
                // Set the dialog width to match the parent minus the margin
                int marginHorizontal = (int) getResources().getDimension(R.dimen.dialog_margin_horizontal);
                params.width = WindowManager.LayoutParams.MATCH_PARENT;
                window.setAttributes(params);
                window.setLayout(params.width - marginHorizontal * 2, WindowManager.LayoutParams.WRAP_CONTENT);
            }

            */
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

    private void handleOrderDispatch(String orderid,String transporName, String driverMobieno, String truckNo)
    {
        orderDetails_viewModel.updateBatchDetails(orderid,transporName,driverMobieno,truckNo).observe(this,
                this::observeOrderDetails);
    }
    private void observeOrderDetails(ApiResponseState_Enum<String> resource) {

        switch (resource.status) {
            case LOADING:
                Toast.makeText(requireActivity(), "Loading Orders", Toast.LENGTH_SHORT).show();
                break;
            case SUCCESS:
                sendHandlerMessage();

                Toast.makeText(requireActivity(), "Success in fetching orders", Toast.LENGTH_SHORT).show();
                showSnackbar(resource.message);
                Objects.requireNonNull(getDialog()).dismiss();
                break;
            case ERROR:
                Toast.makeText(requireActivity(), "Error in fetching orders "+resource.data, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    private void showSnackbar(String message) {
        Snackbar snackbar = Snackbar.make(requireView(), message, Snackbar.LENGTH_LONG);
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
}