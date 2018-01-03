package com.ankhrom.base.common.statics;

import java.util.ArrayList;
import java.util.List;

import com.ankhrom.base.interfaces.ObjectConvertor;

public final class ObjectHelper {

    public static boolean equals(Object o, Object t) {

        return (o == null) ? (t == null) : o.equals(t);
    }

    public static <T, E> List<T> convert(List<E> items, ObjectConvertor<T, E> convertor) {

        List<T> list = new ArrayList<>(items.size());

        for (E item : items) {
            list.add(convertor.convert(item));
        }

        return list;
    }
}
