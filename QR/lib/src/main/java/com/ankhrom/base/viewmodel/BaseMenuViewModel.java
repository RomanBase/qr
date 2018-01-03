package com.ankhrom.base.viewmodel;

import android.app.Activity;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ViewDataBinding;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.ankhrom.base.interfaces.viewmodel.MenuViewModel;
import com.ankhrom.base.interfaces.viewmodel.ViewModel;
import com.ankhrom.base.interfaces.viewmodel.ViewModelNavigation;

public abstract class BaseMenuViewModel<T extends ViewDataBinding> implements MenuViewModel {

    private ViewModelNavigation navigation;
    private final Context context;
    private final ViewGroup container;

    public BaseMenuViewModel(Context context, int container) {
        this(context, (ViewGroup) ((Activity) context).findViewById(container));
    }

    public BaseMenuViewModel(Context context, ViewGroup container) {
        this.context = context;
        this.container = container;
    }

    protected <V extends ViewDataBinding> V inflateSubmenu(int container, int layoutResource) {

        return inflateSubmenu((ViewGroup) ((Activity) context).findViewById(container), layoutResource);
    }

    protected <V extends ViewDataBinding> V inflateSubmenu(ViewGroup container, int layoutResource) {

        return DataBindingUtil.inflate(LayoutInflater.from(context), layoutResource, container, true);
    }

    protected abstract int getLayoutResource();

    @Override
    public void onInit() {

    }

    @Override
    public void onCreate() {

        T binding = DataBindingUtil.inflate(LayoutInflater.from(context), getLayoutResource(), container, true);

        onBindingCreated(binding);
    }

    @Override
    public void setMenuState(Object state) {

    }

    @Override
    public ViewGroup getContainer() {
        return container;
    }

    @Override
    public void setNavigation(ViewModelNavigation navigation) {
        this.navigation = navigation;
    }

    protected void onBindingCreated(T binding) {

    }

    protected void navigateFromNewStack(ViewModel vm) {

        navigateTo(vm, true);
    }

    protected void navigateAtCurrentStack(ViewModel vm) {

        navigateTo(vm, false);
    }

    protected void navigateTo(ViewModel vm, boolean clearBackStack) {

        navigation.setViewModel(vm, clearBackStack);
    }

    public ViewModelNavigation getNavigation() {
        return navigation;
    }

    public Context getContext() {
        return context;
    }
}
