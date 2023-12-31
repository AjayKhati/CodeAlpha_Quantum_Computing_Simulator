package com.ajkhati.quantumcomputingsimulator.tools;


import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

/**
 * Needed to keep the QuantumView state after a configuration change
 */
public class QuantumViewModel extends ViewModel {
    private MutableLiveData<QuantumViewData> mutableLiveData;

    public LiveData<QuantumViewData> get() {
        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<QuantumViewData>();
        }
        return mutableLiveData;
    }

    public void set(QuantumViewData quantumViewData) {
        if (mutableLiveData == null) {
            mutableLiveData = new MutableLiveData<QuantumViewData>();
        }
        mutableLiveData.setValue(quantumViewData);
    }
}
