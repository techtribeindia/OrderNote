package com.project.ordernote.ui.fragment;

import android.app.DatePickerDialog;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.project.ordernote.R;
import com.project.ordernote.data.local.LocalDataManager;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.model.ReportsFilterDetails_Model;
import com.project.ordernote.utils.Constants;
import com.project.ordernote.utils.DateParserClass;
import com.project.ordernote.utils.OnDateSelectedListener;
import com.project.ordernote.viewmodel.Buyers_ViewModel;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;

public class FilterScreenBottomSheetFragment extends BottomSheetDialogFragment  {

    private FilterListener filterListener;
    RadioGroup filterRadioGroup;
    RadioButton today_radiobutton ,yesterday_radiobutton , last7days_radiobutton , buyerwise_radiobutton ,customDateRangeRadioButton ;

    private Buyers_ViewModel buyersViewModel;

    RelativeLayout startDatebuyerwiselayout , endDatebuyerwiselayout  , startDateCustomDateRangelayout , endDateCustomDateRangelayout ;

    LinearLayout buyer_and_date_SelectionLayout ,customDateRangeSelectionLayout ,generateButtonlayout , buyer_SelectionLayout ,
            buyerwiseDateRangeSelectionLayout;

    OnDateSelectedListener ondateSelectedListener;


    String startDate = "" , endDate = "" ,  fileType = "";
    boolean isStartDateSelected = false  , isEndDateSelected = false;
    boolean isStartDateLayoutClicked = false  , isEndDateLayoutClicked = false ,buyerDetailsFetchedSuccessfully = false , menuItemFetchedSuccesfully = false;
    boolean buyerAndDateFilterOptionSelected = false , customDateFilterOptionSelected = false , todayFilterOptionSelected = false , yesterdayFilterOptionSelected = false
            ,last7daysFilterOptionSelected = false;
    DatePickerDialog datepicker,enddatepicker;
    TextView buyernameTextview , pdf_fileTypeText , xls_fileTypeText;
    TextView buyerwise_startDate , buyerwise_endDate , customDate_startDate, customDate_endDate;

    CardView closeImage_cardview;



    public interface FilterListener {
        void onApplyFilters(ReportsFilterDetails_Model filterValue);
    }

    public void setFilterListener(FilterListener filterListener) {
        this.filterListener = filterListener;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.filter_screen_bottom_sheet, container, false);

        filterRadioGroup = view.findViewById(R.id.filterRadioGroup);
        today_radiobutton = view.findViewById(R.id.today_radiobutton);
        yesterday_radiobutton = view.findViewById(R.id.yesterday_radiobutton);
        last7days_radiobutton = view.findViewById(R.id.last7days_radiobutton);
        buyerwise_radiobutton = view.findViewById(R.id.buyerwise_radiobutton);
        customDateRangeRadioButton = view.findViewById(R.id.customDateRangeRadioButton);
        buyer_SelectionLayout = view.findViewById(R.id.buyer_SelectionLayout);

        buyerwise_startDate = view.findViewById(R.id.buyerwise_startDate);
        buyerwise_endDate = view.findViewById(R.id.buyerwise_endDate);
        customDate_startDate = view.findViewById(R.id.customDate_startDate);
        customDate_endDate = view.findViewById(R.id.customDate_endDate);

        buyernameTextview = view.findViewById(R.id.buyernameTextview);
        xls_fileTypeText  = view.findViewById(R.id.xls_fileTypeTextview);
        pdf_fileTypeText  = view.findViewById(R.id.pdf_fileTypeTextview);
        closeImage_cardview = view.findViewById(R.id.closeImage_cardview);

        startDatebuyerwiselayout = view.findViewById(R.id.startDatebuyerwiselayout);
        endDatebuyerwiselayout = view.findViewById(R.id.endDatebuyerwiselayout);
        startDateCustomDateRangelayout = view.findViewById(R.id.startDateCustomDateRangelayout);
        endDateCustomDateRangelayout = view.findViewById(R.id.endDateCustomDateRangelayout);

        buyerwiseDateRangeSelectionLayout = view.findViewById(R.id.buyerwiseDateRangeSelectionLayout);
         customDateRangeSelectionLayout = view.findViewById(R.id.customDateRangeSelectionLayout);


        buyer_and_date_SelectionLayout = view.findViewById(R.id.buyer_and_date_SelectionLayout);
        generateButtonlayout = view.findViewById(R.id.generateButtonLayout);

        last7days_radiobutton.setText(String.valueOf("Last 7 Days ("+String.valueOf(DateParserClass.getDateAndMonthOfNo_ofDaysBack(6))+" - "+String.valueOf(DateParserClass.getCurrentDateAndMonth())+" )"));

        try {
            // buyersViewModel = new ViewModelProvider.AndroidViewModelFactory(requireActivity().getApplication()).create(Buyers_ViewModel.class);
            // Initialize ViewModels
            buyersViewModel = new ViewModelProvider(requireActivity()).get(Buyers_ViewModel.class);

            //     buyersViewModel = new ViewModelProvider(this).get(Buyers_ViewModel.class);
            List<Buyers_Model> buyersList = LocalDataManager.getInstance().getBuyers();

            if(buyersList.size() > 0){

                buyersViewModel.setBuyersListinMutableLiveData(buyersList);
            }
            else{
                buyersViewModel.getBuyersListFromRepository("vendor_1");
            }

            buyersViewModel.clearSelectedLiveData();

        } catch (Exception e) {
            e.printStackTrace();
        }


        closeImage_cardview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });



        ondateSelectedListener = new OnDateSelectedListener() {
            @Override
            public void onDateSelected(String selectedDate) {
                if(isStartDateLayoutClicked && !isEndDateLayoutClicked){
                    isStartDateSelected =true;
                    startDate = selectedDate;
                    if(customDateFilterOptionSelected){
                        customDate_startDate.setText(String.valueOf(selectedDate));
                    }
                    else if(buyerAndDateFilterOptionSelected){
                        buyerwise_startDate.setText(String.valueOf(selectedDate));

                    }
                    isEndDateSelected =false;
                    isEndDateLayoutClicked = false;
                    endDate = "";
                    if(customDateFilterOptionSelected){
                        customDate_endDate.setText(String.valueOf(""));
                    }
                    else if(buyerAndDateFilterOptionSelected){
                        buyerwise_endDate.setText(String.valueOf(""));
                    }

                }
                if(isStartDateLayoutClicked && isEndDateLayoutClicked) {
                    isEndDateSelected = true;
                    endDate = selectedDate;
                    isEndDateLayoutClicked = false;
                   // isStartDateLayoutClicked = false;
                    if(customDateFilterOptionSelected){
                        customDate_endDate.setText(String.valueOf(selectedDate));
                    }
                    else if(buyerAndDateFilterOptionSelected){
                        buyerwise_endDate.setText(String.valueOf(selectedDate));
                    }


                    Toast.makeText(requireContext(), "Selected date range: " + startDate + " to " + endDate, Toast.LENGTH_LONG).show();

                }
            }

            @Override
            public void onDateSelectionCanceled() {
                if(isStartDateLayoutClicked && !isEndDateLayoutClicked){
                    isStartDateSelected =false;
                    isStartDateLayoutClicked = false;
                    startDate = "";
                    isEndDateSelected =false;
                    isEndDateLayoutClicked = false;
                    endDate = "";


                    if(customDateFilterOptionSelected){
                        customDate_startDate.setText(String.valueOf(""));
                    }
                    else if(buyerAndDateFilterOptionSelected){
                        buyerwise_startDate.setText(String.valueOf(""));

                    }
                }

                if(isStartDateLayoutClicked && isEndDateLayoutClicked) {
                    isEndDateSelected =false;
                    isEndDateLayoutClicked = false;
                    endDate = "";
                    if(customDateFilterOptionSelected){
                        customDate_endDate.setText(String.valueOf(""));
                    }
                    else if(buyerAndDateFilterOptionSelected){
                        buyerwise_endDate.setText(String.valueOf(""));
                    }
                }


            }
        };
        return view;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        customDateRangeSelectionLayout.setVisibility(View.GONE);
        buyer_and_date_SelectionLayout.setVisibility(View.GONE);





        buyersViewModel.getBuyersListFromViewModel().observe(getViewLifecycleOwner(),  resource -> {
            if (resource != null) {
                switch (resource.status) {
                    case LOADING:
                        buyerDetailsFetchedSuccessfully = false;
                      //  showProgressBar(true);
                        //Toast.makeText(requireActivity(), "Loading Buyer", Toast.LENGTH_SHORT).show();
                        break;
                    case SUCCESS:
                        buyerDetailsFetchedSuccessfully = true;
                        if(menuItemFetchedSuccesfully){
                           // showProgressBar(false);
                        }

                        //Toast.makeText(requireActivity(), "Success in fetching Buyer", Toast.LENGTH_SHORT).show();
                        break;
                    case ERROR:
                        buyerDetailsFetchedSuccessfully = false;


                        if (resource.message != null) {
                            if (resource.message.equals(Constants.noDataAvailable)) {
                                LocalDataManager.getInstance().setBuyers(new ArrayList<>());

                            } else {
                                Toast.makeText(requireActivity(), "Error in fetching Buyer ", Toast.LENGTH_SHORT).show();

                            }
                        } else {
                            Toast.makeText(requireActivity(), "Error in fetching Buyer  ", Toast.LENGTH_SHORT).show();

                        }


                        ///  Toast.makeText(requireActivity(), "Error in fetching Buyer create order 2 ", Toast.LENGTH_SHORT).show();
                     //   showProgressBar(false);
                        break;
                }
            }
        });

        buyersViewModel.getSelectedBuyersDetailsFromViewModel().observe(getViewLifecycleOwner(), buyerData -> {
            if (buyerData != null) {

                if(!buyerData.getName().equals("")){
                    buyernameTextview.setText(String.valueOf(buyerData.getName()));
                   // binding.selecedBuyerDetailsTextview.setText(String.valueOf(buyerData.getAddress1() +" , "+'\n'+buyerData.getAddress2()+" - "+""+buyerData.getPincode()+" . "+'\n'+"Ph:- +91"+buyerData.getMobileno()));
                }
                else{
                    buyernameTextview.setText(String.valueOf(""));
                    //binding.selecedBuyerDetailsTextview.setText(String.valueOf("xx , xxxxx  xxx xxxx , \nxxxx - xxxx . \nPh : - +91798xxxxxxx"));

                }



            }
        });


        filterRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                buyerAndDateFilterOptionSelected = false;
                customDateFilterOptionSelected = false;
                last7daysFilterOptionSelected = false;
                yesterdayFilterOptionSelected = false;
                todayFilterOptionSelected = false;
                customDateRangeSelectionLayout.setVisibility(View.GONE);
                buyer_and_date_SelectionLayout.setVisibility(View.GONE);


                isStartDateLayoutClicked = false;
                isEndDateLayoutClicked = false;
                isStartDateSelected = false;
                isEndDateSelected = false;
                buyerwise_startDate.setText(String.valueOf(""));
                buyerwise_endDate.setText("");
                customDate_startDate.setText(String.valueOf(""));
                customDate_endDate.setText("");


                buyersViewModel.setSelectedBuyerLiveData(new Buyers_Model());


                if(checkedId == R.id.today_radiobutton){
                    buyerAndDateFilterOptionSelected = false;
                    customDateFilterOptionSelected = false;
                    last7daysFilterOptionSelected = false;
                    yesterdayFilterOptionSelected = false;
                    todayFilterOptionSelected = true;


                }
                else if(checkedId == R.id.yesterday_radiobutton){
                    buyerAndDateFilterOptionSelected = false;
                    customDateFilterOptionSelected = false;
                    last7daysFilterOptionSelected = false;
                    yesterdayFilterOptionSelected = true;
                    todayFilterOptionSelected = false;
                }
                else if(checkedId == R.id.last7days_radiobutton){
                    buyerAndDateFilterOptionSelected = false;
                    customDateFilterOptionSelected = false;
                    last7daysFilterOptionSelected = true;
                    yesterdayFilterOptionSelected = false;
                    todayFilterOptionSelected = false;
                }
                else if(checkedId == R.id.buyerwise_radiobutton){
                    buyer_and_date_SelectionLayout.setVisibility(View.VISIBLE);
                    buyerAndDateFilterOptionSelected = true;
                     customDateFilterOptionSelected = false;
                    last7daysFilterOptionSelected = false;
                    yesterdayFilterOptionSelected = false;
                    todayFilterOptionSelected = false;
                  /*  isStartDateLayoutClicked = false;
                    isEndDateLayoutClicked = false;
                    isStartDateSelected = false;
                    isEndDateSelected = false;
                    buyerwise_startDate.setText(String.valueOf(""));
                    buyerwise_endDate.setText("");
                    customDate_startDate.setText(String.valueOf(""));
                    customDate_endDate.setText("");

                   */
                }
                else if(checkedId == R.id.customDateRangeRadioButton){
                    customDateRangeSelectionLayout.setVisibility(View.VISIBLE);
                     buyerAndDateFilterOptionSelected = false;
                    customDateFilterOptionSelected = true;
                    last7daysFilterOptionSelected = false;
                    yesterdayFilterOptionSelected = false;
                    todayFilterOptionSelected = false;

                }



            }
        });
        generateButtonlayout .setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                ReportsFilterDetails_Model filterValue = new ReportsFilterDetails_Model();

                filterValue.setVendorkey(Constants.vendor_1_key_UserDetails);
                filterValue.setSelectedFileType(fileType);


                 if(todayFilterOptionSelected){
                     filterValue.setSelectedFilterType(Constants.today_filter);
                    // Log.i("Today  - Start Date : ",String.valueOf(DateParserClass.getDateInStandardFormat()));
                    //Log.i("Today  - End Date : ",String.valueOf(DateParserClass.getDateInStandardFormat()));
                    filterValue.setStartDate(DateParserClass.convertGivenDateToTimeStamp(DateParserClass.getDateInStandardFormat()+" 00:00:00"));
                     filterValue.setEndDate(DateParserClass.convertGivenDateToTimeStamp(DateParserClass.getDateInStandardFormat()+" 23:59:59"));
                 //   Toast.makeText(requireContext(), " Today ", Toast.LENGTH_SHORT).show();

                }
                else if (yesterdayFilterOptionSelected) {

                     filterValue.setStartDate(DateParserClass.convertGivenDateToTimeStamp(DateParserClass.getDateTextFor_OldDaysfrom_given(1,DateParserClass.getDateInStandardFormat())+" 00:00:00"));
                    filterValue.setEndDate(DateParserClass.convertGivenDateToTimeStamp(DateParserClass.getDateTextFor_OldDaysfrom_given(1, DateParserClass.getDateInStandardFormat())+" 23:59:59"));
                     filterValue.setSelectedFilterType(Constants.yesterday_filter);
                   // Toast.makeText(requireContext(), " Yesterday ", Toast.LENGTH_SHORT).show();

                }
                else if (last7daysFilterOptionSelected) {

                    filterValue.setStartDate(DateParserClass.convertGivenDateToTimeStamp(DateParserClass.getDateTextFor_OldDaysfrom_given(8 ,  DateParserClass.getDateInStandardFormat())+" 00:00:00"));
                    filterValue.setEndDate(DateParserClass.convertGivenDateToTimeStamp(DateParserClass.getDateInStandardFormat()+" 23:59:59"));

                     filterValue.setSelectedFilterType(Constants.last7day_filter);
                    //Toast.makeText(requireContext(), " last7days ", Toast.LENGTH_SHORT).show();

                }
                else if (buyerAndDateFilterOptionSelected) {
                    filterValue.setStartDate(DateParserClass.convertGivenDateToTimeStamp(startDate+" 00:00:00"));
                    filterValue.setEndDate(DateParserClass.convertGivenDateToTimeStamp(endDate+" 23:59:59"));
                    filterValue.setSelectedFilterType(Constants.buyerwise_filter);
                    filterValue.setSelectedBuyerKey(buyersViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getUniquekey());
                     filterValue.setSelectedBuyerName(buyersViewModel.getSelectedBuyersDetailsFromViewModel().getValue().getName());

                }
                else if (customDateFilterOptionSelected) {
                  //  Toast.makeText(requireContext(), " Custom date  ", Toast.LENGTH_SHORT).show();
                //   Toast.makeText(requireContext(), " start date  "+startDate +"end date "+endDate, Toast.LENGTH_SHORT).show();
                    filterValue.setSelectedFilterType(Constants.customdatewise_filter);
                    filterValue.setStartDate(DateParserClass.convertGivenDateToTimeStamp(startDate+" 00:00:00"));
                    filterValue.setEndDate(DateParserClass.convertGivenDateToTimeStamp(endDate+" 23:59:59"));
                }
                if (filterListener != null) {
                    filterListener.onApplyFilters(filterValue);
                    dismiss();
                }


            }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        startDatebuyerwiselayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a MaterialDatePicker instance
                try{
                    isStartDateLayoutClicked = true;
                    // DateParserClass.showStartDatePicker(getActivity(), ondateSelectedListener);
                    openDatePicker();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        endDatebuyerwiselayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEndDateLayoutClicked = true;
                //   DateParserClass.showEndDatePicker(getActivity(), ondateSelectedListener , startDate );
                openEndDatePicker();
            }
        });


        startDateCustomDateRangelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Create a MaterialDatePicker instance
                try{
                    isStartDateLayoutClicked = true;
                    // DateParserClass.showStartDatePicker(getActivity(), ondateSelectedListener);
                    openDatePicker();
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

        endDateCustomDateRangelayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEndDateLayoutClicked = true;
                //   DateParserClass.showEndDatePicker(getActivity(), ondateSelectedListener , startDate );
                openEndDatePicker();
            }
        });


        buyer_SelectionLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBuyerSelectionDialog();
            }
        });
        buyernameTextview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openBuyerSelectionDialog();
            }
        });


        pdf_fileTypeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable backgroundpdf = ContextCompat.getDrawable(requireActivity(), R.drawable.round_redbackground_withoutpadding);
                pdf_fileTypeText.setBackground(backgroundpdf);
                pdf_fileTypeText.setTextColor(getResources().getColor(R.color.white));

                Drawable backgroundxls = ContextCompat.getDrawable(requireActivity(), R.drawable.round_lightredbackground);
                xls_fileTypeText.setBackground(backgroundxls);
                xls_fileTypeText.setTextColor(getResources().getColor(R.color.black));
                fileType = Constants.pdf_filetype;

            }
        });
        pdf_fileTypeText.performClick();
        xls_fileTypeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Drawable backgroundxls= ContextCompat.getDrawable(requireActivity(), R.drawable.round_redbackground_withoutpadding);
                xls_fileTypeText .setBackground(backgroundxls);
                xls_fileTypeText.setTextColor(getResources().getColor(R.color.white));

                Drawable backgroundpdf = ContextCompat.getDrawable(requireActivity(), R.drawable.round_lightredbackground);
                pdf_fileTypeText.setBackground(backgroundpdf);
                pdf_fileTypeText.setTextColor(getResources().getColor(R.color.black));
                fileType = Constants.xls_filetype;
            }
        });
    }



    private void openDatePicker() {



        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        datepicker = new DatePickerDialog(requireActivity(),R.style.CustomDatePickerDialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {

                           /* String month_in_String = DateParserClass.getMonthString(monthOfYear);
                            String monthstring = String.valueOf(monthOfYear + 1);
                            String datestring = String.valueOf(dayOfMonth);
                            if (datestring.length() == 1) {
                                datestring = "0" + datestring;
                            }
                            if (monthstring.length() == 1) {
                                monthstring = "0" + monthstring;
                            }

                            Calendar myCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);

                            int dayOfWeek = myCalendar.get(Calendar.DAY_OF_WEEK);

                            String CurrentDay = DateParserClass.getDayString(dayOfWeek);
                            //Log.d(Constants.TAG, "dayOfWeek Response: " + dayOfWeek);


                            String CurrentDateString = datestring + monthstring + String.valueOf(year);

                            */
                            Calendar myCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);

                            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.standardDateFormat, Locale.getDefault());
                            String selectedDateString = dateFormat.format(myCalendar.getTime());
                            startDate = selectedDateString;
                            isStartDateSelected = true;
                          //  startDate = (CurrentDay + ", " + dayOfMonth + " " + month_in_String + " " + year);

                            if(buyerAndDateFilterOptionSelected){

                                buyerwise_startDate.setText(String.valueOf(startDate));
                                buyerwise_endDate.setText("");
                            }
                            else if(customDateFilterOptionSelected){
                                customDate_startDate.setText(String.valueOf(startDate));
                                customDate_endDate.setText("");


                            }

                             endDate = (DateParserClass.getDateTextFor_OldDaysfrom_given(-6,(startDate))+" 00:00:00");

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, year, month, day);






        Calendar c = Calendar.getInstance();



        DatePicker datePicker = datepicker.getDatePicker();

        c.add(Calendar.YEAR, -2);
        // Toast.makeText(getApplicationContext(), Calendar.DATE, Toast.LENGTH_LONG).show();
        Log.d(Constants.TAG, "Calendar.DATE " + String.valueOf(Calendar.DATE));
        long oneMonthAhead = c.getTimeInMillis();
        datePicker.setMaxDate(System.currentTimeMillis() - 1000);
        datePicker.setMinDate(oneMonthAhead);

        datepicker.show();
    }

    private void openEndDatePicker() {


        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        enddatepicker = new DatePickerDialog(requireActivity(),R.style.CustomDatePickerDialog,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {




                           /* String month_in_String =  DateParserClass.getMonthString(monthOfYear);
                            String monthstring = String.valueOf(monthOfYear + 1);
                            String datestring = String.valueOf(dayOfMonth);
                            if (datestring.length() == 1) {
                                datestring = "0" + datestring;
                            }
                            if (monthstring.length() == 1) {
                                monthstring = "0" + monthstring;
                            }


                            Calendar myCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);

                            int dayOfWeek = myCalendar.get(Calendar.DAY_OF_WEEK);

                            String CurrentDay = DateParserClass.getDayString(dayOfWeek);
                            //Log.d(Constants.TAG, "dayOfWeek Response: " + dayOfWeek);


                            String CurrentDateString = datestring + monthstring + String.valueOf(year);


                            */


                            Calendar myCalendar = new GregorianCalendar(year, monthOfYear, dayOfMonth);

                            SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.standardDateFormat, Locale.getDefault());
                            String selectedDateString = dateFormat.format(myCalendar.getTime());
                            isEndDateSelected = true;
                            endDate = selectedDateString;
                             //endDate = (CurrentDay + ", " + dayOfMonth + " " + month_in_String + " " + year);

                            if(buyerAndDateFilterOptionSelected){

                                buyerwise_endDate.setText(String.valueOf(endDate));
                            }
                            else if(customDateFilterOptionSelected){

                                customDate_endDate.setText(String.valueOf(endDate));
                            }
                             Toast.makeText(requireActivity() , "After Selecting the data . Please Click on Get Data Button", Toast.LENGTH_LONG).show();

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, year, month, day);






        Calendar c = Calendar.getInstance();




        boolean isEndDateisAfterCurrentDate = false;
        Date d2=null,d1 = null;
        long MaxDate = DateParserClass.getMillisecondsFromDate(endDate);
        long MinDate = DateParserClass.getMillisecondsFromDate(startDate);

        String todayDate = DateParserClass.getDateInStandardFormat();
        SimpleDateFormat sdformat = new SimpleDateFormat(Constants.standardDateFormat, Locale.ENGLISH);
        sdformat.setTimeZone(TimeZone.getTimeZone(Constants.timeZone_Format));

        try {
            d2 = sdformat.parse(todayDate);

            d1 = sdformat.parse(endDate);
            if((d1.compareTo(d2) < 0)||(d1.compareTo(d2) == 0)){
                isEndDateisAfterCurrentDate =false;
            }
            else if(d1.compareTo(d2) > 0){
                isEndDateisAfterCurrentDate =true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        DatePicker datePicker = enddatepicker.getDatePicker();
        c.add(Calendar.DATE, -7);
        try {
            if (!isEndDateisAfterCurrentDate) {

                MaxDate = DateParserClass.getMillisecondsFromDate(endDate);

            } else {
                MaxDate = DateParserClass.getMillisecondsFromDate(todayDate);

            }
            MinDate = DateParserClass.getMillisecondsFromDate(startDate);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        long oneMonthAhead = c.getTimeInMillis();
        datePicker.setMaxDate(MaxDate);
        datePicker.setMinDate(MinDate);

        enddatepicker.show();



    }



    private void openBuyerSelectionDialog() {

        try{
            BuyerSelectionDialogFragment dialogFragment = new BuyerSelectionDialogFragment();
            // dialogFragment.setBuyerSelectionListener(this); // Set the listener
            dialogFragment.show(getParentFragmentManager(), "BuyerSelectionDialogFragment");

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }



}