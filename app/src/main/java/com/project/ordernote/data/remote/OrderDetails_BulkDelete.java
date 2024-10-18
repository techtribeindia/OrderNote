package com.project.ordernote.data.remote;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.google.firebase.Timestamp;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.project.ordernote.utils.DateParserClass;

import org.bouncycastle.util.Times;

import java.sql.Time;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class OrderDetails_BulkDelete extends Worker
{


    public OrderDetails_BulkDelete(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }



    // Function to delete old orders based on cutoff timestamp and vendorkey
    public void orderDetails_BulkDelete(Timestamp cutoffTimestamp, String vendorkey) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        long cutoffMillis = cutoffTimestamp.toDate().getTime();
        // Prepare the data to send to the Cloud Function
        Map<String, Object> data = new HashMap<>();
        data.put("cutoffTimestamp", cutoffMillis);  // Pass the timestamp
        data.put("vendorkey", vendorkey);              // Pass the vendorkey

        // Call the Cloud Function
        functions.getHttpsCallable("deleteOldOrders")
                .call(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Function successfully executed
                        HttpsCallableResult result = task.getResult();
                        HashMap<String ,String> message = (HashMap<String, String>) result.getData();
                        System.out.println("Success: " + message);
                    } else {
                        // Handle the error
                        Exception e = task.getException();
                        System.err.println("Error calling function: " + e.getMessage());
                    }
                });
    }
    public void orderItemDetails_BulkDelete(Timestamp cutoffTimestamp, String vendorkey) {
        FirebaseFunctions functions = FirebaseFunctions.getInstance();
        long cutoffMillis = cutoffTimestamp.toDate().getTime();
        // Prepare the data to send to the Cloud Function
        Map<String, Object> data = new HashMap<>();
        data.put("cutoffTimestamp", cutoffMillis);  // Pass the timestamp
        data.put("vendorkey", vendorkey);              // Pass the vendorkey

        // Call the Cloud Function
        functions.getHttpsCallable("deleteOldOrderItems")
                .call(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Function successfully executed
                        HttpsCallableResult result = task.getResult();
                        HashMap<String ,String> message = (HashMap<String, String>) result.getData();
                        System.out.println("deleteOldOrderItems Success: " + message);
                    } else {
                        // Handle the error
                        Exception e = task.getException();
                        System.err.println("Error calling deleteOldOrderItems function: " + e.getMessage());
                    }
                });
    }

    public void checkIfWeNeedToTriggerDeleteFunction(int orderExpiryDays, int orderDeletionIntervalDays, Timestamp lastlyTriggeredOn, String vendorkey) {
        // Get today's timestamp
        Timestamp todaysTimestamp = DateParserClass.convertGivenDateToTimeStamp(DateParserClass.getDateTimeInStandardFormat());
        Date todayDate = todaysTimestamp.toDate();
        Date lastTriggeredDate = lastlyTriggeredOn.toDate();

        // Calculate the time difference in milliseconds
        long timeDifferenceMillis = todayDate.getTime() - lastTriggeredDate.getTime();

        // Convert deletion interval days to milliseconds
        long deletionIntervalMillis = orderDeletionIntervalDays * 24L * 60L * 60L * 1000L; // Convert days to milliseconds

        // Check if the time difference is greater than or equal to the deletion interval
        if (timeDifferenceMillis >= deletionIntervalMillis) {
            // If the condition is met, trigger the bulk delete function
             Timestamp date = null;
             date = DateParserClass.convertGivenDateToTimeStamp(DateParserClass.getDateAndTimeOfNo_ofDaysBack(orderExpiryDays));


            orderDetails_BulkDelete(date, vendorkey);
            //orderItemDetails_BulkDelete(date, vendorkey);
            System.out.println("order deletion check date : "+ date);

            System.out.println("order deletion check  Order deletion triggered.");
        } else {
            System.out.println("order deletion check  Order deletion not triggered. Time interval has not yet passed.");
        }
    }


    @NonNull
    @Override
    public Result doWork() {


        // Get input data
        int orderExpiryDays = getInputData().getInt("orderExpiryDays", 0);
        int orderDeletionIntervalDays = getInputData().getInt("orderDeletionIntervalDays", 0);
        long lastlyTriggeredOnMillis = getInputData().getLong("lastlyTriggeredOn", 0);
        String vendorkey = getInputData().getString("vendorkey");

        // Convert millis to Timestamp
        Timestamp lastlyTriggeredOn = new Timestamp(new Date(lastlyTriggeredOnMillis));

        // Trigger the delete check function

        checkIfWeNeedToTriggerDeleteFunction(orderExpiryDays, orderDeletionIntervalDays, lastlyTriggeredOn, vendorkey);

        return Result.success();


    }


}
