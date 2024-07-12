package com.project.ordernote.viewmodel;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class Dashboard_ViewModel extends ViewModel {
    private final MutableLiveData<Fragment> selectedFragment = new MutableLiveData<>();

    public void setSelectedFragment(Fragment fragment) {
        selectedFragment.setValue(fragment);
    }

    public LiveData<Fragment> getSelectedFragment() {
        return selectedFragment;
    }
}