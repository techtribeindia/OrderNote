package com.project.ordernote.utils;


public class Constants{
    public static String TAG = "TAG";


    //SharedPrefRoot

    public static  String USERPREF_NAME = "UserSession";
    public static  String VENDORPREF_NAME = "VendorSession";



    //DecimalFormat
    public static String threeDecimalPattern = "###.###";
    public static String twoDecimalPattern = "#.##";
    public static String oneDecimalPattern = "###.#";
    public static String twoDecimalWithCommaPattern = "##,##,##,##,###.00";

    //Date
     public static String timeZone_Format = "Asia/Kolkata";
     public static String standardDateFormat = "dd/MM/yyyy";
    public static String standardDateTimeFormat = "dd/MM/yyyy HH:mm:ss";
    public static String standardTimeFormat = "HH:mm:ss";
    public static String readableDateTimeFormat = "EEE, d MMM yyyy HH:mm:ss";
    public static String firebaseDateTimeFormat = "yyyy-MM-ddTHH:mm:ssZ";

     public static String dateMonth = "ddMMM";


    public static String newDate_Time_Format = "yyyy-MM-dd HH:mm:ss";

    public static final String unitprice_pricetype = "UNITPRICE";
    public static final String priceperkg_pricetype = "PRICEPERKG";


    public static final String pdf_filetype = "PDF";
    public static final String xls_filetype = "XLS";


    public static final String today_filter = "TODAY";
    public static final String yesterday_filter = "YESTERDAY";
    public static final String last7day_filter = "LAST7DAY";
    public static final String buyerwise_filter = "BUYERWISE";
    public static final String customdatewise_filter = "CUSTOMDATE";



    public static final String success = "SUCCESS";
    public static final String failed = "FAILED";

    //UserDetails table
    public static final String admin_role = "ADMIN";
    public static final String staff_role = "STAFF";
    public static final String blocked_role = "BLOCKED";
    public static final boolean true_forcelogout = true;
    public static final boolean false_forcelogout = false;
    public static final String vendor_1_key_UserDetails = "vendor_1";
    public static final String vendor_1_name_UserDetails = "Ponrathi Traders";

    //orderdetails Table

    public static final String cash_payment_mode = "CASH";



    public static final String pending_status = "PENDING";

    public static final String created_pending_status = "PENDING";
    public static final String placed_pending_status = "ACCEPTED";

    public static final String rejected_status = "REJECTED";
    public static final String accepted_status = "ACCEPTED";
    public static final String cancelled_status = "CANCELLED";

    public static final String editapproved_dispatchstatus = "EDITAPPROVED";
    public static final String dispatched_dispatchstatus = "DISPATCHED";
    public static final String editrequested_dispatchstatus = "EDITREQUESTED";
    public static final String cancelled_dispatchstatus = "CANCELLED";

    public static final String share_file = "SHARE";
    public static final String view_file = "VIEW";

    //MenuItems table
    public static final String unitprice_itemtype = "UNITPRICE";
    public static final String priceperkg_itemtype = "PRICEPERKG";
    public static final boolean true_showforbilling = true;
    public static final boolean false_showforbilling = false;



    //reports feature
    public static final String datewiseConsolidatedPDF = "DATEWISE_CONSOLIDATED_PDF";
    public static final String buyerwiseConsolidatedPDF = "BUYERWISE_CONSOLIDATED_PDF";


    public static final String today_statuswise_pdf = "TODAY_STATUSWISE_PDF";
    public static final String week_statuswise_pdf = "WEEK_STATUSWISE_PDF";


    public static final String datewiseConsolidatedXLS = "DATEWISE_CONSOLIDATED_XLS";
    public static final String buyerwiseConsolidatedXLS = "BUYERWISE_CONSOLIDATED_XLS";


    // Constants for processing
    public static final String noDataAvailable = "No data available";
    public static final String createNewBuyer = "CreateNewBuyer";
    public static final String updateOldBuyer = "UpdateOldBuyer";
    public static final String processtodo = "ProcessToDo";

    public static final String  buyerkey = "BuyerKey";
    public static final String add = "ADD";
    public static final String update = "UPDATE";




}