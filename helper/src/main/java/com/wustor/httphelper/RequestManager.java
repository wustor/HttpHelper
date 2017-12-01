package com.wustor.httphelper;

import android.content.Context;

import com.cmyy.chuangmei.executor.SmartExecutor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by fatchao
 * 日期  2017-08-10.
 * 邮箱  fat_chao@163.com
 */
public class RequestManager {
    private static RequestManager mInstance;
    private SmartExecutor smallExecutor;
    private HashMap<String, ArrayList<Request>> mCachedRequest;

    public static RequestManager getInstance() {
        if (mInstance == null) {
            mInstance = new RequestManager();
        }
        return mInstance;
    }

    private RequestManager() {
        mCachedRequest = new HashMap<>();
        //TODO 初始化线程池
        if (smallExecutor == null) {
            smallExecutor = new SmartExecutor();
        }
    }

    public void performRequest(Context context, Request request) {
        request.execute(context, smallExecutor);
        if (request.tag == null) {
            return;// no need to cache the request
        }
        if (!mCachedRequest.containsKey(request.tag)) {
            ArrayList<Request> requests = new ArrayList<>();
            mCachedRequest.put(request.tag, requests);
        }
        mCachedRequest.get(request.tag).add(request);
    }

    public void performRequest(Context context, Request request,boolean isShow) {
        request.execute(context, smallExecutor,isShow);
        if (request.tag == null) {
            return;// no need to cache the request
        }
        if (!mCachedRequest.containsKey(request.tag)) {
            ArrayList<Request> requests = new ArrayList<>();
            mCachedRequest.put(request.tag, requests);
        }
        mCachedRequest.get(request.tag).add(request);
    }

    public void cancelRequest(String tag) {
        cancelRequest(tag, false);
    }

    /**
     * @param tag
     * @param force true cancel task with no callback, false cancel task with callback as CancelException
     */
    public void cancelRequest(String tag, boolean force) {
        if (tag == null || "".equals(tag.trim())) {
            return;
        }
        if (mCachedRequest.containsKey(tag)) {
            ArrayList<Request> requests = mCachedRequest.remove(tag);
            for (Request request : requests) {
                if (!request.isCompleted && !request.isCancelled) {
                    request.cancel(force);
                }
            }
        }

    }

    public void cancelAll() {
        for (Map.Entry<String, ArrayList<Request>> entry : mCachedRequest.entrySet()) {
            ArrayList<Request> requests = entry.getValue();
            for (Request request : requests) {
                if (!request.isCompleted && !request.isCancelled) {
                    request.cancel(true);
                }
            }
        }
        mCachedRequest.clear();
    }


}
