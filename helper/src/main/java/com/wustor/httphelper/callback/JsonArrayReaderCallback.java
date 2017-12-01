package com.wustor.httphelper.callback;


import com.wustor.httphelper.core.AbstractCallback;
import com.wustor.httphelper.entities.JsonReaderable;
import com.wustor.httphelper.error.AppException;

import java.io.FileReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

public abstract class JsonArrayReaderCallback<T extends JsonReaderable> extends AbstractCallback<ArrayList<T>> {
    @Override
    protected ArrayList<T> bindData(String path) throws AppException {
        try {

            Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];
            ArrayList<T> ts = new ArrayList<T>();
            T t  ;

            FileReader in = new FileReader(path);
            com.google.gson.stream.JsonReader reader = new com.google.gson.stream.JsonReader(in);
            String node;
            reader.beginObject();
            while(reader.hasNext()){
                node = reader.nextName();
                if ("data".equalsIgnoreCase(node)){
                    reader.beginArray();
                    while(reader.hasNext()){
                        t = ((Class<T>)type).newInstance();
                        t.readFromJson(reader);
                        ts.add(t);
                    }
                    reader.endArray();
                }else {
                    reader.skipValue();
                }
            }
            reader.endObject();
            return ts;

        } catch (Exception e) {
            throw new AppException(AppException.ErrorType.JSON,e.getMessage());
        }
    }
}
