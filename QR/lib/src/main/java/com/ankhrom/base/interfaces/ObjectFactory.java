package com.ankhrom.base.interfaces;

import android.support.annotation.Nullable;

import com.ankhrom.base.interfaces.viewmodel.ViewModel;

public interface ObjectFactory {

    @Nullable
    <T> T construct(Class<T> clazz, Object... args);

    @Nullable
    <T extends ViewModel> T getViewModel(Class<T> viewModelClass, Object... args);
}
