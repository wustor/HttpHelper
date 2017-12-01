package com.wustor.httphelper.callback;


import com.wustor.httphelper.core.AbstractCallback;
import com.wustor.httphelper.error.AppException;


public abstract class StringCallback extends AbstractCallback<String> {

    @Override
    protected String bindData(String result) throws AppException {
        return result;
    }
}
