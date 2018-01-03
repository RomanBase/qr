package com.ankhrom.base.interfaces.viewmodel;

import android.view.ViewGroup;

public interface MenuViewModel {

    /**
     * initial set up
     */
    void onInit();

    /**
     * build menu view and inflate it to given container
     */
    void onCreate();

    /**
     * sets specific menu state
     */
    void setMenuState(Object state);

    /**
     * @return parent of MenuModels layout
     */
    ViewGroup getContainer();

    /**
     * sets default ViewModelNavigation
     */
    void setNavigation(ViewModelNavigation navigation);

}
