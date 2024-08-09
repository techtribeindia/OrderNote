package com.project.ordernote.utils;


public class Constants{

    //DecimalFormat
    public static String threeDecimalPattern = "###.###";
    public static String twoDecimalPattern = "#.##";
    public static String oneDecimalPattern = "###.#";
    public static String twoDecimalWithCommaPattern = "##,##,##,##,##0.00";

    //Date
    public static String readableDate_Format = "EEE, d MMM yyyy";
    public static String timeZone_Format = "Asia/Kolkata";



    public static final String unitprice_pricetype = "UNITPRICE";
    public static final String priceperkg_pricetype = "PRICEPERKG";

    public static final String created_orderstatus = "CREATED";



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
    public static final String created_status = "CREATED";
    public static final String rejected_status = "REJECTED";
    public static final String placed_status = "PLACED";
    public static final String editapproved_dispatchstatus = "EDITAPPROVED";
    public static final String dispatched_dispatchstatus = "DISPATCHED";
    public static final String editrequested_dispatchstatus = "EDITREQUESTED";
    public static final String cancelled_dispatchstatus = "CANCELLED";



    // Constants for processing
    public static final String noDataAvailable = "No data available";
    public static final String createNewBuyer = "CreateNewBuyer";
    public static final String updateOldBuyer = "UpdateOldBuyer";
    public static final String processtodo = "ProcessToDo";

    public static final String  buyerkey = "BuyerKey";
    public static final String add = "ADD";
    public static final String update = "UPDATE";

}