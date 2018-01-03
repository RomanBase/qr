package com.ankhrom.base;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.lang.reflect.Constructor;

import com.ankhrom.base.common.statics.ArgsHelper;
import com.ankhrom.base.custom.args.InitArgs;
import com.ankhrom.base.interfaces.Initializable;
import com.ankhrom.base.interfaces.ObjectFactory;
import com.ankhrom.base.interfaces.viewmodel.ViewModel;

public class BaseFactory implements ObjectFactory {

    @Nullable
    @Override
    public <T> T construct(@NonNull Class<T> clazz, Object... args) {

        T object = null;

        try {
            if (Initializable.class.isAssignableFrom(clazz)) {
                object = newInstance(clazz);
                ((Initializable) object).init(new InitArgs(object, args));
            } else {
                object = newInstance(clazz, args);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return object;
    }

    @Nullable
    @Override
    public <T extends ViewModel> T getViewModel(@NonNull Class<T> viewModelClass, Object... args) {

        return construct(viewModelClass, args);
    }

    @SuppressWarnings("unchecked")
    public <T> T newInstance(Class<T> clazz, Object... args) throws Exception {

        if (args == null || args.length == 0) {
            return construct(clazz);
        }

        Constructor[] constructors = clazz.getConstructors();
        args = ArgsHelper.preSortArgs(args);

        for (Constructor constructor : constructors) {
            Class[] params = constructor.getParameterTypes();
            Object[] sortedArgs = ArgsHelper.strictSortArgs(params, args);
            if (sortedArgs != null) {
                return (T) constructor.newInstance(sortedArgs);
            }
        }

        return construct(clazz);
    }

    @SuppressWarnings("unchecked")
    private <T> T construct(Class<T> clazz) throws Exception {

        Constructor<?> constructor = clazz.getConstructors()[0];
        Class[] params = constructor.getParameterTypes();

        if (params.length == 0) {
            return clazz.getConstructor().newInstance();
        } else {
            return (T) constructor.newInstance(new Object[params.length]);
        }
    }
}
