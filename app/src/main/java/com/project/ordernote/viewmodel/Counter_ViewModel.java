package com.project.ordernote.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.project.ordernote.data.repository.Counter_Repository;
import com.project.ordernote.utils.ApiResponseState_Enum;

public class Counter_ViewModel extends ViewModel{

    private MutableLiveData<ApiResponseState_Enum<Long>> orderNumberLiveData;
    private Observer<ApiResponseState_Enum<Long>> orderNoObserver;

    private Counter_Repository counterRepository;

        public Counter_ViewModel() {
            orderNumberLiveData = new MutableLiveData<>();
            counterRepository = new Counter_Repository();
            initObserver();
        }


    private void initObserver() {
        orderNoObserver = state -> orderNumberLiveData.setValue(state);
    }
    public LiveData<ApiResponseState_Enum<Long>> getOrderNumberLiveData() {
        if(orderNumberLiveData == null){
            orderNumberLiveData = new MutableLiveData<>();
        }
        return orderNumberLiveData;
    }



        public void incrementOrderCounter(String vendorkey) {
            try {

                LiveData<ApiResponseState_Enum<Long>> source =  counterRepository.incrementOrderCounter(vendorkey);
                source.observeForever(orderNoObserver);

             //   orderNumberLiveData = counterRepository.incrementOrderCounter(vendorkey);
            }
            catch (Exception e){
                e.printStackTrace();
            }
        }

    public void getOrderCounterAndIncrementLocally(String vendorkey) {

        LiveData<ApiResponseState_Enum<Long>> source =  counterRepository.getOrderCounterAndIncrementLocally(vendorkey);
        source.observeForever(orderNoObserver);


        /*MutableLiveData<ApiResponseState_Enum<Long>> source = new MutableLiveData<>();
        try {
            //orderNumberLiveData = orderRepository.getOrderCounterAndIncrementLocally(vendorkey);


            source = orderRepository.getOrderCounterAndIncrementLocally(vendorkey);


            try {
                Log.i("orderno : ", String.valueOf(Objects.requireNonNull(source.getValue()).data));
            }
            catch (Exception c ){
                c.printStackTrace();
            }
            if(source==null){
                source = new MutableLiveData<>();

            }
            if(source.getValue() ==null){
                source = new MutableLiveData<>();

            }
            try{
                if(Objects.requireNonNull(source.getValue()).data !=null) {

                    long ordernoValue = source.getValue().data;
                    ordernoValue = ordernoValue  + 1;
                    source .setValue(ApiResponseState_Enum.success(ordernoValue));

                }
            }
            catch (Exception e){
                source.setValue(ApiResponseState_Enum.error("Error while Locally incrementing1 "+String.valueOf(e.getMessage()),null));
                e.printStackTrace();
            }


        }
        catch (Exception e){
            source.setValue(ApiResponseState_Enum.error("Error while Locally incrementing2 "+String.valueOf(e.getMessage()),null));

            e.printStackTrace();
        }

        source.observeForever(orderNoObserver);

         */

    }


    @Override
    protected void onCleared() {
        super.onCleared();

        orderNumberLiveData.removeObserver(orderNoObserver);
    }

}
