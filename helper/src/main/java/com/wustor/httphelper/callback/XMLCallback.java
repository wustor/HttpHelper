package com.wustor.httphelper.callback;


import com.wustor.httphelper.core.AbstractCallback;
import com.wustor.httphelper.error.AppException;


public abstract class XMLCallback<T> extends AbstractCallback<T> {

    @Override
    protected T bindData(String result) throws AppException {


        return null;
    }
}
