package com.zayditech.cricerzfantasy.ui.yourTeam;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class YourTeamViewModel extends ViewModel {

    private MutableLiveData<String> mText;

    public YourTeamViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is slideshow fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}