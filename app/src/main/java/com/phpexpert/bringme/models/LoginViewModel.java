package com.phpexpert.bringme.models;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class LoginViewModel extends ViewModel {

    private final MutableLiveData<Event<Boolean>> backPressed = new MutableLiveData<>();
    public void backPressed() {
        backPressed.setValue(new Event<>(true));
    }
    public LiveData<Event<Boolean>> getBackPressedEvent() {
        return backPressed;
    }

    private final MutableLiveData<Event<Boolean>> openLoginScreen = new MutableLiveData<>();
    public void setContinueWithFragment() {
        openLoginScreen.setValue(new Event<>(true));
    }
    public LiveData<Event<Boolean>> getContinueWithFragment() {
        return openLoginScreen;
    }


    private final MutableLiveData<Event<Object[]>> openOtpFragment = new MutableLiveData<>();
    public void setOpenVerifyOtpfragment(Object[] objects) {
        openOtpFragment.setValue(new Event<>(objects));
    }
    public LiveData<Event<Object[]>> getOpenVerifyOtpScreen() {
        return openOtpFragment;
    }


    private final MutableLiveData<Event<Boolean>> phoneVarificationFrag = new MutableLiveData<>();
    public void setPhoneVarificationFrag() {
        phoneVarificationFrag.setValue(new Event<>(true));
    }
    public LiveData<Event<Boolean>> getPhoneVarificationFrag() {
        return phoneVarificationFrag;
    }
    private final MutableLiveData<Event<Boolean>> registerFrag = new MutableLiveData<>();
    public void setRegisterFrag() {
        registerFrag.setValue(new Event<>(true));
    }
    public LiveData<Event<Boolean>> getRegisterFrag() {
        return registerFrag;
    }






}
