package com.project.ordernote.ui.fragment;

import android.app.DatePickerDialog;
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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.project.ordernote.R;
import com.project.ordernote.ui.adapter.OrdersListAdapter;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.SessionManager;
import com.project.ordernote.viewmodel.OrderDetails_ViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DateWiseOrderScreenFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateWiseOrderScreenFragment extends DialogFragment {
    private OrderDetails_ViewModel orderDetails_viewModel;
    private OrdersListAdapter ordersListAdapter;
    private OrderListItemDescFragment orderListItemDescFragment;
    private DatePicker datePicker;
    private Calendar calendar;
    private Date selectedDate;
    private TextView tvSelectedDate;
    private int year, month, day;
    private SessionManager sessionManager;


    public DateWiseOrderScreenFragment() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DateWiseOrderScreenFragment newInstance() {
        DateWiseOrderScreenFragment fragment = new DateWiseOrderScreenFragment();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sessionManager = new SessionManager(requireActivity());
        if (getArguments() != null) {

        }
    }
    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT)); // Removes default background
        }
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_date_wise_order_screen, container, false);


        RelativeLayout backbut = view.findViewById(R.id.dialog_back);
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        LinearLayout dateLayout = view.findViewById(R.id.date_layout);
        LinearLayout fetchDataLayout = view.findViewById(R.id.fetch_data_layout);
        RecyclerView OrderRecyclerView = view.findViewById(R.id.orders_recycler_view);

        orderListItemDescFragment = new OrderListItemDescFragment();
        orderDetails_viewModel = new ViewModelProvider(requireActivity()).get(OrderDetails_ViewModel.class);
        orderDetails_viewModel.setUserDetails(sessionManager.getVendorkey());
        ordersListAdapter = new OrdersListAdapter(orderDetails_viewModel);
        Toast.makeText(requireActivity(), sessionManager.getVendorkey(), Toast.LENGTH_SHORT).show();
        OrderRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        OrderRecyclerView.setAdapter(ordersListAdapter);




        tvSelectedDate.setText("--//--//----");
        dateLayout.setOnClickListener(v -> {
            showDatePickerDialog();
        });
        backbut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Objects.requireNonNull(getDialog()).dismiss();

            }
        });
        fetchDataLayout.setOnClickListener(v -> {
            if (Objects.equals(selectedDate, ""))
            {
                Toast.makeText(requireActivity(), "Please select order placed date", Toast.LENGTH_SHORT).show();
            }
        });

        try {
            orderDetailViewModel(Constants.placed_status);
        }
        catch (Exception e ){
            e.printStackTrace();
        }

        return view;
    }
    private void showDatePickerDialog() {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and show it
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity(), (view, year1, month1, dayOfMonth) -> {
            // month1 is 0-based, so add 1
            String selectedDate1 = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            tvSelectedDate.setText(selectedDate1);

            // Convert selectedDate1 to a Date object
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                 selectedDate = sdf.parse(selectedDate1);

                orderDetailViewModel(Constants.placed_status);
            } catch (ParseException e) {
                e.printStackTrace();
            }

        }, year, month, day);

        datePickerDialog.show();
    }

    private void orderDetailViewModel(String status) {

            Calendar calendar = Calendar.getInstance();
            calendar.setTime(selectedDate);

            // Set start of the day
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            Date startDate = calendar.getTime();
            Timestamp startTimestamp = new Timestamp(startDate);

            // Set end of the day
            calendar.set(Calendar.HOUR_OF_DAY, 23);
            calendar.set(Calendar.MINUTE, 59);
            calendar.set(Calendar.SECOND, 59);
            calendar.set(Calendar.MILLISECOND, 999);
            Date endDate = calendar.getTime();
            Timestamp endTimestamp = new Timestamp(endDate);

            // Fetch data with the exact timestamps for the selected date
            orderDetails_viewModel.getOrdersByStatus_DateAndVendorKey(status, startTimestamp, endTimestamp,"vendor_1");

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
                        ordersListAdapter.setOrders(resource.data,"DatwWiseOrderScreen");
                        break;
                    case ERROR:
                        Toast.makeText(requireActivity(), "Error in fetching orders", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
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
}