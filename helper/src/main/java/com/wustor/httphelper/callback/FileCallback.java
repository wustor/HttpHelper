package com.wustor.httphelper.callback;


import com.wustor.httphelper.AppException;

public abstract class FileCallback extends AbstractCallback<String> {

    @Override
    protected String bindData(String path) throws AppException {
        return path;
    }
}
