package com.project.ordernote.utils;

import static com.project.ordernote.utils.Constants.dateMonth;
import static com.project.ordernote.utils.Constants.newDate_Time_Format;
import static com.project.ordernote.utils.Constants.readableDateTimeFormat;
import static com.project.ordernote.utils.Constants.standardDateFormat;
import static com.project.ordernote.utils.Constants.standardDateTimeFormat;
import static com.project.ordernote.utils.Constants.standardTimeFormat;
import static com.project.ordernote.utils.Constants.timeZone_Format;

import android.app.DatePickerDialog;
import android.content.Context;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.core.util.Pair;
import androidx.fragment.app.FragmentActivity;

import com.google.android.material.datepicker.CalendarConstraints;
import com.google.android.material.datepicker.DateValidatorPointBackward;
import com.google.android.material.datepicker.MaterialDatePicker;
import com.google.firebase.Timestamp;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.TimeZone;

public class DateParserClass {


    public static String getDateTimeInReadableFormat()
    {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(readableDateTimeFormat,Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone(timeZone_Format));
        String CurrentDate_time = df.format(c);
        return CurrentDate_time;
    }
    public static String getDateInStandardFormat()
    {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(standardDateFormat,Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone(timeZone_Format));
       String CurrentDate_time = df.format(c);
        return CurrentDate_time;
    }
    public static String getDateTimeInStandardFormat()
    {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(standardDateTimeFormat,Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone(timeZone_Format));
        String CurrentDate_time = df.format(c);
        return CurrentDate_time;
    }


    public static String getTime_newFormat() {

        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(standardTimeFormat,Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone(timeZone_Format));
        String CurrentDate_time = df.format(c);
        return CurrentDate_time;

    }

    public static String getCurrentDateAndMonth()
    {
        Date c = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat(dateMonth,Locale.ENGLISH);
        df.setTimeZone(TimeZone.getTimeZone(timeZone_Format));
        String CurrentDate_time = df.format(c);
        return CurrentDate_time;
    }

    public static String convertGivenTimeStampToDate(Timestamp date) {
        Date datevalue = date.toDate();

        // Optionally, format the Date to a specific string format if needed
        SimpleDateFormat sdf = new SimpleDateFormat(standardDateFormat, Locale.getDefault());
        String formattedDate = sdf.format(datevalue);

        System.out.println("Formatted Date: " + formattedDate);
        return formattedDate;
    }



    public static Timestamp convertGivenDateToTimeStamp(String date)
    {
        SimpleDateFormat sdf = new SimpleDateFormat(standardDateTimeFormat, Locale.getDefault());
        Date selectedDate = null;
        try {
            selectedDate = sdf.parse(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        Timestamp startTimestamp = new Timestamp(selectedDate);
        return startTimestamp;
     //   Calendar calendar = Calendar.getInstance();
     //   calendar.setTime(selectedDate);

        // Set start of the day
     //   calendar.set(Calendar.HOUR_OF_DAY, 0);
     //   calendar.set(Calendar.MINUTE, 0);
     //   calendar.set(Calendar.SECOND, 0);
     //   calendar.set(Calendar.MILLISECOND, 0);
       // Date startDate = calendar.getTime();
       // Timestamp startTimestamp = new Timestamp(selectedDate);
      //  return startTimestamp;
    }


    public static String getDateAndMonthOfNo_ofDaysBack(int no_of_daysBefore)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(standardDateFormat,Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone_Format));
        Date date = null;
        try {
            date = dateFormat.parse(getDateInStandardFormat());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.DATE, -no_of_daysBefore);


        Date c1 = calendar.getTime();


        SimpleDateFormat df1 = new SimpleDateFormat(dateMonth,Locale.ENGLISH);
        df1.setTimeZone(TimeZone.getTimeZone(timeZone_Format));

         return df1.format(c1);
    }

    public static String getDateAndTimeOfNo_ofDaysBack(int no_of_daysBefore)
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat(standardDateFormat,Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone_Format));
        Date date = null;
        try {
            date = dateFormat.parse(getDateInStandardFormat());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.DATE, -no_of_daysBefore);


        Date c1 = calendar.getTime();


        SimpleDateFormat df1 = new SimpleDateFormat(standardDateTimeFormat,Locale.ENGLISH);
        df1.setTimeZone(TimeZone.getTimeZone(timeZone_Format));

        return df1.format(c1);
    }


    public static String getFirstDayOfWeek(String inputDate) {

        SimpleDateFormat outputFormat = new SimpleDateFormat(standardDateFormat);

        SimpleDateFormat sdf = new SimpleDateFormat(standardDateFormat);
        Calendar calendar = Calendar.getInstance();
        try {
            Date date = sdf.parse(inputDate);

            // Get the first day of the week

        calendar.setTime(date);

        // Set the first day of the week as per your locale or preference
        calendar.setFirstDayOfWeek(Calendar.SUNDAY);

        // Get the first day of the current week
        calendar.set(Calendar.DAY_OF_WEEK, calendar.getFirstDayOfWeek());





            return outputFormat.format(calendar.getTime());
        } catch (Exception e) {
            e.printStackTrace();
            return outputFormat.format(calendar.getTime());


        }
    }
    public static  String getDayString(int value) {
        if (value == 1) {
            return "Sun";
        }  else if (value == 2) {
            return "Mon";
        } else if (value == 3) {
            return "Tue";
        } else if (value == 4) {
            return "Wed";
        } else if (value == 5) {
            return "Thu";
        } else if (value == 6) {
            return "Fri";
        }
        else if (value == 7) {
            return "Sat";
        }
        return "";
    }



    public static long getMillisecondsFromDate(String dateString) {
        Calendar calendarr = Calendar.getInstance();



        calendarr.add(Calendar.DATE,-1);



        long milliseconds = calendarr.getTimeInMillis();
        SimpleDateFormat sdf = new SimpleDateFormat(standardDateFormat,Locale.ENGLISH);
        sdf.setTimeZone(TimeZone.getTimeZone(timeZone_Format));

        try{
            //formatting the dateString to convert it into a Date
            Date date = sdf.parse(dateString);
            System.out.println("Given Time in milliseconds : "+date.getTime());

            Calendar calendar = Calendar.getInstance();
            //Setting the Calendar date and time to the given date and time
            calendar.setTime(date);
            System.out.println("Given Time in milliseconds : "+calendar.getTimeInMillis());
            milliseconds = calendar.getTimeInMillis();
        }catch(ParseException e){
            e.printStackTrace();
        }
        return  milliseconds;
    }


    public static String convertOldFormatDateintoNewFormat(String todaysdate) {


        Date date = null;
        String CurrentDate = "";
        SimpleDateFormat formatGMT = new SimpleDateFormat(standardDateFormat, Locale.ENGLISH);

        formatGMT.setTimeZone(TimeZone.getTimeZone(timeZone_Format));

        try
        {
            date  = formatGMT.parse(todaysdate);
        }
        catch (ParseException e)
        {
            //log(Log.ERROR, "DB Insertion error", e.getMessage().toString());
            //logException(e);
            e.printStackTrace();
        }

        try{

            SimpleDateFormat day = new SimpleDateFormat(standardDateFormat,Locale.ENGLISH);
            day.setTimeZone(TimeZone.getTimeZone(timeZone_Format));


            CurrentDate = day.format(date);


        }
        catch (Exception e){
            e.printStackTrace();
        }


        return CurrentDate;

    }

    public static String getDateTextFor_OldDaysfrom_given(int no_of_daysBefore , String time){
        SimpleDateFormat dateFormat = new SimpleDateFormat(standardDateFormat,Locale.ENGLISH);
        dateFormat.setTimeZone(TimeZone.getTimeZone(timeZone_Format));
        Date date = null;
        try {
            date = dateFormat.parse(time);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);

        calendar.add(Calendar.DATE, -no_of_daysBefore);


        Date c1 = calendar.getTime();


        SimpleDateFormat df1 = new SimpleDateFormat(standardDateFormat,Locale.ENGLISH);
        df1.setTimeZone(TimeZone.getTimeZone(timeZone_Format));

        String  PreviousdayDate = df1.format(c1);

        return PreviousdayDate;
    }

    public static String getMonthString(int value) {

        if (value == 0) {
            return "Jan";
        } else if (value == 1) {
            return "Feb";
        } else if (value ==2) {
            return "Mar";
        } else if (value ==3) {
            return "Apr";
        } else if (value ==4) {
            return "May";
        } else if (value ==5) {
            return "Jun";
        } else if (value ==6) {
            return "Jul";
        } else if (value ==7) {
            return "Aug";
        } else if (value ==8) {
            return "Sep";
        } else if (value ==9) {
            return "Oct";
        } else if (value ==10) {
            return "Nov";
        } else if (value ==11) {
            return "Dec";
        }
        return "";
    }






/*
    public static void showStartDatePicker(Context context, OnDateSelectedListener listener) {
        // Create the date picker

        MaterialDatePicker<Long> datePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select start date")
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setEnd(Calendar.getInstance().getTimeInMillis()) // Set current date as the maximum date
                        .setValidator(DateValidatorPointBackward.now()) // Prevent future dates
                        .build())
                .build();

// Show the Date Picker dialog
        datePicker.show(((FragmentActivity) context).getSupportFragmentManager(), "DATE_PICKER");

// Set listener to handle the selected date
        datePicker.addOnPositiveButtonClickListener(selection -> {
            // Convert selection to date
            String selectedDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(selection));
            if (listener != null) {
                listener.onDateSelected(selectedDate);
            }
        });

        datePicker.addOnNegativeButtonClickListener(dialog -> {
            // Handle when the user clicks the cancel button
            if (listener != null) {
                listener.onDateSelectionCanceled();
            }
        });



    }

    public static void showEndDatePicker(Context context, OnDateSelectedListener listener, String startDate) {

        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        Calendar startDateCalendar = Calendar.getInstance();
        try {
            Date parsedStartDate = dateFormat.parse(startDate); // Assuming startDate is in "dd/MM/yyyy" format.
            if (parsedStartDate != null) {
                startDateCalendar.setTime(parsedStartDate);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

// Get the current date
        Calendar todayCalendar = Calendar.getInstance();
        long todayInMillis = todayCalendar.getTimeInMillis();
        long startDateInMillis = startDateCalendar.getTimeInMillis();

// Setup MaterialDatePicker
        MaterialDatePicker<Long> enddatePicker = MaterialDatePicker.Builder.datePicker()
                .setTitleText("Select end date")
                .setCalendarConstraints(new CalendarConstraints.Builder()
                        .setStart(startDateInMillis)
                        .setEnd(todayInMillis) // Set current date as the maximum date
                        .setValidator(DateValidatorPointBackward.now()) // Disallow future dates
                        .build())
                .build();

// Show the Date Picker dialog
        enddatePicker.show(((FragmentActivity) context).getSupportFragmentManager(), "DATE_PICKER");

// Set listener to handle the selected date
        enddatePicker.addOnPositiveButtonClickListener(selection -> {
            // Convert selection to date
            String selectedDate = dateFormat.format(new Date(selection));
            if (listener != null) {
                listener.onDateSelected(selectedDate);
            }
        });

        enddatePicker.addOnNegativeButtonClickListener(dialog -> {
            // Handle when the user clicks the cancel button
            if (listener != null) {
                listener.onDateSelectionCanceled();
            }
        });



    }



    public static void openDatePicker(Context context, OnDateSelectedListener listener) {



        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
        DatePickerDialog datepicker = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {

                            String month_in_String = getMonthString(monthOfYear);
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

                            String CurrentDay = getDayString(dayOfWeek);
                            //Log.d(Constants.TAG, "dayOfWeek Response: " + dayOfWeek);


                            String CurrentDateString = datestring + monthstring + String.valueOf(year);
                            listener.onDateSelected(CurrentDateString);

                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, year, month, day);






        Calendar c = Calendar.getInstance();



        DatePicker datePicker = datepicker.getDatePicker();

        c.add(Calendar.YEAR, -2);
        // Toast.makeText(getApplicationContext(), Calendar.DATE, Toast.LENGTH_LONG).show();
        Log.d("DateParser", "Calendar.DATE " + String.valueOf(Calendar.DATE));
        long oneMonthAhead = c.getTimeInMillis();
        datePicker.setMaxDate(System.currentTimeMillis() - 1000);
        datePicker.setMinDate(oneMonthAhead);

        datepicker.show();
    }

    public static void openEndDatePicker(Context context, OnDateSelectedListener listener, String startDate) {

        final String[] selectedEndDate = {""};
        final Calendar cldr = Calendar.getInstance();
        int day = cldr.get(Calendar.DAY_OF_MONTH);
        int month = cldr.get(Calendar.MONTH);
        int year = cldr.get(Calendar.YEAR);
        // date picker dialog
       DatePickerDialog enddatepicker = new DatePickerDialog(context,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        try {




                            String month_in_String =  getMonthString(monthOfYear);
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

                            String CurrentDay = getDayString(dayOfWeek);
                            //Log.d(Constants.TAG, "dayOfWeek Response: " + dayOfWeek);


                            selectedEndDate[0] = datestring + monthstring + String.valueOf(year);

                            listener.onDateSelected(selectedEndDate[0]);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    }
                }, year, month, day);






        Calendar c = Calendar.getInstance();




        boolean isEndDateisAfterCurrentDate = false;
        Date d2=null,d1 = null;
        long MaxDate = getMillisecondsFromDate(selectedEndDate[0]);
        long MinDate =getMillisecondsFromDate(startDate);

        String todayDate = getDateInStandardFormat();
        SimpleDateFormat sdformat = new SimpleDateFormat("EEE, d MMM yyyy",Locale.ENGLISH);
        sdformat.setTimeZone(TimeZone.getTimeZone("Asia/Kolkata"));

        try {
            d2 = sdformat.parse(todayDate);

            d1 = sdformat.parse(selectedEndDate[0]);
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

                MaxDate = getMillisecondsFromDate(selectedEndDate[0]);

            } else {
                MaxDate = getMillisecondsFromDate(todayDate);

            }
            MinDate = getMillisecondsFromDate(startDate);
        }
        catch (Exception e){
            e.printStackTrace();
        }

        long oneMonthAhead = c.getTimeInMillis();
        datePicker.setMaxDate(MaxDate);
        datePicker.setMinDate(MinDate);

        enddatepicker.show();



    }

    public static void showRangewiseDatePicker(Context context) {
        Calendar calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());

        // Current date
        long today = calendar.getTimeInMillis();

        // Start date: 3 months before today
        calendar.add(Calendar.MONTH, -6);
        long threeMonthsAgo = calendar.getTimeInMillis();

        // End date: Today
        calendar = Calendar.getInstance(TimeZone.getDefault(), Locale.getDefault());
        long endDate = calendar.getTimeInMillis();

        // Configure date picker builder
        MaterialDatePicker.Builder<Pair<Long, Long>> builder = MaterialDatePicker.Builder.dateRangePicker();

        // Create CalendarConstraints and set them to the builder
        CalendarConstraints.Builder constraintsBuilder = new CalendarConstraints.Builder();

        // Prevent selecting dates after the current date
        constraintsBuilder.setValidator(DateValidatorPointBackward.now());

        constraintsBuilder.setStart(threeMonthsAgo);
        constraintsBuilder.setEnd(endDate);

        builder.setCalendarConstraints(constraintsBuilder.build());

        // Create the date picker
        MaterialDatePicker<Pair<Long, Long>> dateRangePicker = builder.build();

        // Show the Date Picker dialog
        dateRangePicker.show(((FragmentActivity) context).getSupportFragmentManager(), "DATE_RANGE_PICKER");
         // Set listeners
        dateRangePicker.addOnPositiveButtonClickListener(selection -> {
            // Handle date range selection
            Pair<Long, Long> selectedDates = selection;

            // Ensure end date is no more than 7 days after start date
            if (selectedDates != null) {
                long startDate = selectedDates.first;
                long endDateSelected = selectedDates.second;
                long maxEndDate = startDate + (7 * 24 * 60 * 60 * 1000); // Start date + 7 days

                if (endDateSelected > maxEndDate) {
                    Toast.makeText(context, "End date cannot be more than 7 days after the start date", Toast.LENGTH_LONG).show();
                   // return; // Exit early if end date is invalid
                }
                else{
                    String startDateFormatted = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(startDate));
                    String endDateFormatted = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date(endDateSelected));

                    Toast.makeText(context, "Selected date range: " + startDateFormatted + " to " + endDateFormatted, Toast.LENGTH_LONG).show();

                }

            }
        });

        dateRangePicker.addOnNegativeButtonClickListener(dialog -> {
            // Handle when the user clicks the cancel button
            Toast.makeText(context, "Date selection canceled", Toast.LENGTH_SHORT).show();
        });
    }

 */
 }
