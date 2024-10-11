package com.project.ordernote.ui.fragment;

import android.app.DatePickerDialog;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
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
    CardView dialogOrderStatusCard;
    TextView dialogOrderStatusText;
    RecyclerView OrderRecyclerView;
    View progressbarLayout;
    LottieAnimationView progressbar;
    LinearLayout searchLayout;
    EditText searchInput;

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
        sessionManager = new SessionManager(requireActivity(), Constants.USERPREF_NAME);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.MyCustomDialogTheme);

        if (getArguments() != null) {

        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (getDialog() != null && getDialog().getWindow() != null) {
            getDialog().getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            int statusBarColor = ContextCompat.getColor(requireContext(), R.color.reddishgrey);
         //   getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(statusBarColor)); // Removes default background

            // Forcefully set status bar color and clear any transparency
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Window window = getDialog().getWindow();
                window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
                window.setStatusBarColor(statusBarColor); // Set status bar color to white

                // Set dark icons if Android version supports it
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
                } else {
                    // For older versions, you may not be able to change icon colors, but status bar should be white
                    window.getDecorView().setSystemUiVisibility(0); // Clear any flags affecting icon color
                }
            }
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_date_wise_order_screen, container, false);

        progressbarLayout = view.findViewById(R.id.progressbar_backlayout);
        progressbar = view.findViewById(R.id.progressbar);

        searchLayout = view.findViewById(R.id.search_layout);
        searchInput = view.findViewById(R.id.search_edit_text);

        RelativeLayout backbut = view.findViewById(R.id.dialog_back);
        tvSelectedDate = view.findViewById(R.id.tvSelectedDate);
        LinearLayout dateLayout = view.findViewById(R.id.date_layout);
        LinearLayout fetchDataLayout = view.findViewById(R.id.fetch_data_layout);
        OrderRecyclerView = view.findViewById(R.id.orders_recycler_view);
        dialogOrderStatusCard = view.findViewById(R.id.dialog_orderstatus_card);
        dialogOrderStatusText = view.findViewById(R.id.dialog_orderstatus_text);

        orderListItemDescFragment = new OrderListItemDescFragment();
        orderDetails_viewModel = new ViewModelProvider(requireActivity()).get(OrderDetails_ViewModel.class);
        orderDetails_viewModel.setUserDetails(sessionManager.getVendorkey());
        ordersListAdapter = new OrdersListAdapter(orderDetails_viewModel);
        Toast.makeText(requireActivity(), sessionManager.getVendorkey(), Toast.LENGTH_SHORT).show();
        OrderRecyclerView.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        OrderRecyclerView.setAdapter(ordersListAdapter);





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
            if ((Objects.equals(selectedDate, ""))||(Objects.equals(selectedDate, null)))
            {

                OrderRecyclerView.setVisibility(View.GONE);
                dialogOrderStatusCard.setVisibility(View.VISIBLE);
                searchLayout.setVisibility(View.GONE);
                dialogOrderStatusText.setText(String.valueOf("Please select the Date by clicking the select date button and then click on the Fetch Button to fetch the orders"));
                return;
            }
            orderDetailViewModel(Constants.accepted_status);
        });

        searchInput.addTextChangedListener(new TextWatcher() {
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
    private void showDatePickerDialog() {
        // Get current date
        final Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        // Create a new instance of DatePickerDialog and show it
        DatePickerDialog datePickerDialog = new DatePickerDialog(requireActivity() ,R.style.CustomDatePickerDialog, (view, year1, month1, dayOfMonth) -> {
            searchInput.setText("");
            String selectedDate1 = dayOfMonth + "/" + (month1 + 1) + "/" + year1;
            tvSelectedDate.setText(selectedDate1);
            orderDetails_viewModel.clearOrderDetails();
            orderDetails_viewModel.clearFromViewModel();
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            try {
                 selectedDate = sdf.parse(selectedDate1);


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

            searchInput.setText("");
            orderDetails_viewModel.getOrdersByStatus_DateAndVendorKey(status, startTimestamp, endTimestamp,sessionManager.getVendorkey());

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
                        showProgressBar(false);
                        orderDetails_viewModel.setOrderDetails(resource);
                        if ((Objects.equals(selectedDate, ""))||(Objects.equals(selectedDate, null)))
                        {
                            dialogOrderStatusCard.setVisibility(View.VISIBLE);
                            OrderRecyclerView.setVisibility(View.GONE);
                            searchLayout.setVisibility(View.GONE);
                            dialogOrderStatusText.setText(String.valueOf("Please select the Date by clicking the select date button and then click on the Fetch Button to fetch the orders"));
                        }
                        else
                        {
                            if(resource.message == null || resource.message == "cleared" )
                            {
                                dialogOrderStatusCard.setVisibility(View.VISIBLE);
                                OrderRecyclerView.setVisibility(View.GONE);
                                searchLayout.setVisibility(View.GONE);
                                dialogOrderStatusText.setText(String.valueOf("Please click the Fetch Data button to load the orders"));
                            }

                            else if(resource.message == "" )
                            {
                                dialogOrderStatusCard.setVisibility(View.GONE);
                                OrderRecyclerView.setVisibility(View.VISIBLE);
                                searchLayout.setVisibility(View.VISIBLE);
                            }
                            else
                            {
                                dialogOrderStatusCard.setVisibility(View.VISIBLE);
                                OrderRecyclerView.setVisibility(View.GONE);
                                searchLayout.setVisibility(View.GONE);
                                dialogOrderStatusText.setText(String.valueOf(resource.message));
                            }

                        }


                        ordersListAdapter.setOrders(resource.data,"DatwWiseOrderScreen");
                        break;
                    case ERROR:
                        showProgressBar(false);
                        dialogOrderStatusCard.setVisibility(View.VISIBLE);
                        OrderRecyclerView.setVisibility(View.GONE);
                        searchLayout.setVisibility(View.GONE);
                        dialogOrderStatusText.setText(String.valueOf(resource.message));


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

    @Override
    public void onResume() {
        super.onResume();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = requireActivity().getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(ContextCompat.getColor(requireContext(), R.color.reddishgrey)); // Use a different color if needed
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            View decor = requireActivity().getWindow().getDecorView();
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR); // Adjust as needed
        }
    }

    private void showProgressBar(boolean show) {

        try {
            if (show) {
                progressbar.playAnimation();
                progressbar.setVisibility(View.VISIBLE);
                progressbarLayout.setVisibility(View.VISIBLE);
            } else {
                progressbar.cancelAnimation();
                progressbar.setVisibility(View.GONE);
                progressbarLayout.setVisibility(View.GONE);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}