package com.karankulx.von.Models;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    MutableLiveData<String> mutableLiveData = new MutableLiveData<>();

    //create setText method
    public void setText(String s) {
        mutableLiveData.setValue(s);
    }

    //create method for getValue
    public MutableLiveData<String> getText() {
        return mutableLiveData;
    }
}
