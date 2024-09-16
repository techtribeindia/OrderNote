package com.project.ordernote.viewmodel;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.project.ordernote.data.model.OrderDetails_Model;
import com.project.ordernote.data.model.OrderItemDetails_Model;
import com.project.ordernote.data.model.ReportsFilterDetails_Model;
import com.project.ordernote.utils.PDFGenerator;
import com.project.ordernote.utils.PDFGeneratorListener;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class Reports_ViewModel extends AndroidViewModel {

    private MutableLiveData<ReportsFilterDetails_Model> filterDetailsModelMutableLiveData;
    private Observer<ReportsFilterDetails_Model> filterDetailsModelObserver;





    public Reports_ViewModel(@NonNull Application application) {
        super(application);
        initObserver();
    }
    private void initObserver() {
        filterDetailsModelObserver = state -> filterDetailsModelMutableLiveData.setValue(state);
    }


    public void applyFilter(ReportsFilterDetails_Model filterValue) {
        filterDetailsModelMutableLiveData = new MutableLiveData<>(new ReportsFilterDetails_Model());
        filterDetailsModelMutableLiveData.setValue(filterValue);

        LiveData<ReportsFilterDetails_Model> filterDetailsModel = filterDetailsModelMutableLiveData;
        filterDetailsModel = new MutableLiveData<>();
        filterDetailsModel.observeForever(filterDetailsModelObserver);

    }

    public LiveData<ReportsFilterDetails_Model> getFilteredReports() {

        if(filterDetailsModelMutableLiveData ==null){
            filterDetailsModelMutableLiveData = new MutableLiveData<>();
        }
        return filterDetailsModelMutableLiveData;

    }




    public void generatePDF(FragmentActivity fragmentActivity, Context context, PDFGeneratorListener listener , String pdfType , HashMap<String, OrderDetails_Model> orderDetails, List<OrderItemDetails_Model> orderItems) {

        new PDFGenerator(fragmentActivity,context, pdfType , orderDetails ,orderItems, filterDetailsModelMutableLiveData.getValue(),listener);

    }

    public void generateOrderDetailsPDF(FragmentActivity fragmentActivity, Context context, PDFGeneratorListener listener, String pdfType, HashMap<String, JSONObject> statuswisetotalcountdetailsjson, HashMap<String, List<OrderItemDetails_Model>> orderwiseOrderItemDetails, HashMap<String, List<String>> statuswiseOrderid) {

        new PDFGenerator(fragmentActivity,context, pdfType , statuswisetotalcountdetailsjson  ,orderwiseOrderItemDetails, statuswiseOrderid , listener);

    }
}
