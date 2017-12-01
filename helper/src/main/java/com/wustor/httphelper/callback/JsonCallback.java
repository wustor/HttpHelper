package com.wustor.httphelper.callback;

import android.util.Log;

import com.google.gson.Gson;
import com.wustor.httphelper.core.AbstractCallback;
import com.wustor.httphelper.error.AppException;

import java.lang.reflect.ParameterizedType;


public abstract class JsonCallback<T> extends AbstractCallback<T> {
    @Override
    protected T bindData(String result) throws AppException {
        try {
            Log.d("result---->", result);
            Class<T> entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            Gson gson = new Gson();
            return gson.fromJson(result, entityClass);
        } catch (Exception e) {
            throw new AppException(AppException.ErrorType.JSON, e.getMessage());
        }
    }
}
