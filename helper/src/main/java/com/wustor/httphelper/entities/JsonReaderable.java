package com.wustor.httphelper.entities;

import com.google.gson.stream.JsonReader;
import com.wustor.httphelper.error.AppException;

public interface JsonReaderable {
    void readFromJson(JsonReader reader) throws AppException;
}
