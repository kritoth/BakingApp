package com.tiansirk.bakingapp.model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

public class FavoriteViewModelFactory implements ViewModelProvider.Factory {

    private Application mApplication;

    public FavoriteViewModelFactory(Application mApplication) {
        this.mApplication = mApplication;
    }

    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new FavoriteViewModel(mApplication);
    }
}
