package com.wustor.httphelper.itf;


import com.wustor.httphelper.error.AppException;


public interface OnGlobalExceptionListener {

    boolean handleException(AppException exception);
}
