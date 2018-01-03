package com.ankhrom.base.interfaces.viewmodel;


public interface ViewModelNavigation {

    void setViewModel(ViewModel viewModel, boolean clearBackStack);

    void addViewModel(ViewModel viewModel, boolean asRootContent);

    void navigateBack();

    void setPreviousViewModel();

    <T extends ViewModelObserver> T getObserver();

}
