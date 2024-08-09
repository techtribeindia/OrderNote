package com.project.ordernote.viewmodel;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import androidx.lifecycle.MutableLiveData;

import com.project.ordernote.data.local.LocalDataManager;
import com.project.ordernote.data.model.Buyers_Model;
import com.project.ordernote.data.remote.FirestoreService;
import com.project.ordernote.data.repository.Buyers_Repository;
import com.project.ordernote.utils.ApiResponseState_Enum;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Buyers_ViewModel extends AndroidViewModel {

    private  Buyers_Repository repository = null;
    private MutableLiveData<ApiResponseState_Enum<List<Buyers_Model>>> buyersListLiveData;
    private MutableLiveData<Buyers_Model> selectedBuyerLiveData;
    private MutableLiveData<String> selectedBuyerPositionStringLiveData;

    public Buyers_ViewModel(@NonNull Application application) {
        super(application);
        if(repository == null ){
            repository = new Buyers_Repository();

        }
        if(buyersListLiveData == null){
            buyersListLiveData = new MutableLiveData<>();

        }
        if(selectedBuyerLiveData == null){
            selectedBuyerLiveData = new MutableLiveData<Buyers_Model>();

        }
        if(selectedBuyerPositionStringLiveData == null){
            selectedBuyerPositionStringLiveData = new MutableLiveData<String>();

        }

    }



    public void clearSelectedLiveData() {
        if(selectedBuyerLiveData != null){
            selectedBuyerLiveData .setValue( new Buyers_Model());

        }
        if(selectedBuyerPositionStringLiveData != null){
            selectedBuyerPositionStringLiveData .setValue("");

        }
    }

        public void getBuyersListFromRepository(String vendorKey) {
        buyersListLiveData = repository.getBuyersList(vendorKey);
    }

    public void setBuyersListinMutableLiveData(List<Buyers_Model> buyerlist) {
        buyersListLiveData.setValue(ApiResponseState_Enum.success(buyerlist));

    }


    public void setSelectedBuyerLiveData(Buyers_Model selectedBuyerData) {
        this.selectedBuyerLiveData.setValue( selectedBuyerData);
    }

    public LiveData<Buyers_Model> getSelectedBuyersDetailsFromViewModel() {
        if(selectedBuyerLiveData == null){
            selectedBuyerLiveData = new MutableLiveData<>(new Buyers_Model());
        }
        return selectedBuyerLiveData;
    }

    public LiveData<ApiResponseState_Enum<List<Buyers_Model>>> getBuyersListFromViewModel() {
        if(buyersListLiveData == null){
            buyersListLiveData = new MutableLiveData<>();
        }
        return buyersListLiveData;
    }

    public LiveData<String> getSelectedBuyerPositionStringLiveData() {
        if(selectedBuyerPositionStringLiveData == null){
            selectedBuyerPositionStringLiveData = new MutableLiveData<>();
        }
        return selectedBuyerPositionStringLiveData;
    }

    public void setSelectedBuyerPositionStringLiveData(String selectedBuyerPositionString) {
        this.selectedBuyerPositionStringLiveData .setValue( selectedBuyerPositionString);
    }

    public Buyers_Model getBuyerDataFromViewModelUsingBuyerName(String selectedBuyer) {
        if(buyersListLiveData == null){
            buyersListLiveData = new MutableLiveData<>();
        }
        List<Buyers_Model> buyersModelList = new ArrayList<>(Objects.requireNonNull(buyersListLiveData.getValue()).data);
        if(buyersModelList.size()>0){
            for(int itera = 0 ; itera < buyersModelList.size(); itera++){
                if(selectedBuyer .equals(buyersModelList.get(itera).getName())){

                    return buyersModelList.get(itera);

                }
            }

        }
        return new Buyers_Model();
    }



    public Buyers_Model getBuyerDataFromViewModelUsingBuyerKey(String selectedBuyerKey) {
        if(buyersListLiveData == null){
            buyersListLiveData = new MutableLiveData<>();
        }
        List<Buyers_Model> buyersModelList = new ArrayList<>(Objects.requireNonNull(buyersListLiveData.getValue()).data);
        if(buyersModelList.size()>0){
            for(int itera = 0 ; itera < buyersModelList.size(); itera++){
                if(selectedBuyerKey .equals(buyersModelList.get(itera).getUniquekey())){

                    return buyersModelList.get(itera);

                }
            }

        }
        return new Buyers_Model();
    }


    public void deleteBuyerDetailsFromDB(String buyerkey, FirestoreService.FirestoreCallback<Void> firestoreCallback) {

        repository.deleteBuyerDetails(buyerkey, firestoreCallback);

    }



    public void addBuyerInDB(Buyers_Model buyersModel , FirestoreService.FirestoreCallback<Void> callback) {
        repository.addBuyerInDB(buyersModel, callback);

    }


    public boolean checkIfBuyerIsPresentInViewModelBuyerList(String mobileNo) {

        if(buyersListLiveData.getValue()!=null){
            if(buyersListLiveData.getValue().data!=null){
                if(!buyersListLiveData.getValue().data.isEmpty()){

                    for(int i = 0 ; i < buyersListLiveData.getValue().data.size() ; i++) {
                        if (mobileNo.equals(buyersListLiveData.getValue().data.get(i).getMobileno())) {
                            return true;

                        }
                        else {
                            if ((buyersListLiveData.getValue().data.size() - 1) == i) {
                                return false;
                            }
                        }


                    }
                }
                else{
                    return false;
                }
            }

        }

        return false;


    }

    public void updateBuyerDataInViewModelAndLocalArray(Buyers_Model buyersModel) {

        if(buyersListLiveData == null){
            buyersListLiveData = new MutableLiveData<>();

        }
        if(Objects.requireNonNull(buyersListLiveData.getValue()).data==null){
            buyersListLiveData.setValue(ApiResponseState_Enum.success(new ArrayList<>()));
        }

            List<Buyers_Model> buyersModelArrayList = new ArrayList<>(Objects.requireNonNull(buyersListLiveData.getValue().data));
           // buyersListLiveData.getValue().data.add(buyersModel);
             buyersModelArrayList.add(buyersModel);
            buyersListLiveData.setValue(ApiResponseState_Enum.success(buyersModelArrayList));
            LocalDataManager.getInstance().setBuyers(buyersModelArrayList);


    }

    public void deleteBuyerDetailsFromViewModelAndDB(String buyerkey) {
        if(buyersListLiveData == null){
            buyersListLiveData = new MutableLiveData<>();

        }
        if(buyersListLiveData.getValue().data==null){
            buyersListLiveData.setValue(ApiResponseState_Enum.success(new ArrayList<>()));
        }

        if(buyersListLiveData.getValue().data.size() > 0) {
            List<Buyers_Model> buyersModelArrayList = new ArrayList<>(Objects.requireNonNull(buyersListLiveData.getValue().data));
            for(int i = 0 ; i < buyersModelArrayList.size(); i++){
                if(buyersModelArrayList.get(i).getUniquekey().equals(buyerkey)){
                    buyersModelArrayList.remove(i);
                    buyersListLiveData.setValue(ApiResponseState_Enum.success(buyersModelArrayList));
                    LocalDataManager.getInstance().setBuyers(buyersModelArrayList);
                }
            }

        }
        else{
            buyersListLiveData.setValue(ApiResponseState_Enum.success(new ArrayList<>()));
        }

    }

    public void updateBuyerInDB(Buyers_Model buyersModel, FirestoreService.FirestoreCallback<Void> callback) {
        repository.updateBuyerInDB(buyersModel, callback);


    }
}

