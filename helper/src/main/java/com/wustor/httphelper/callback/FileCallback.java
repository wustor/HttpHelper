package com.wustor.httphelper.callback;


import com.wustor.httphelper.core.AbstractCallback;
import com.wustor.httphelper.error.AppException;

public abstract class FileCallback extends AbstractCallback<String> {

    @Override
    protected String bindData(String path) throws AppException {
        return path;
    }
}
