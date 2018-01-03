package com.wustor.httphelper;

import android.content.Context;
import android.text.TextUtils;

import com.wustor.httphelper.util.ExecutorUtil;
import com.wustor.httphelper.util.HintUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Created by fatchao
 * 日期  2017-08-10.
 * 邮箱  fat_chao@163.com
 */
public class HttpHelper {
    private ThreadPoolExecutor executor;
    private volatile static HttpHelper httpHelper;
    private HashMap<String, ArrayList<Request>> mCachedRequest;

    private HttpHelper() {
        mCachedRequest = new HashMap<>();
        if (executor == null) {
            executor = ExecutorUtil.createThreadPool();
        }
    }

    public static HttpHelper getInstance() {
        if (httpHelper == null) {
            synchronized (HttpHelper.class) {
                if (httpHelper == null)
                    httpHelper = new HttpHelper();
            }
        }
        return httpHelper;
    }


    public void performRequest(Context context, Request request) {
        request.execute(context, executor);
        HintUtils.showDialog(context,"");
        if (!mCachedRequest.containsKey(request.tag)) {
            ArrayList<Request> requests = new ArrayList<>();
            mCachedRequest.put(request.tag, requests);
        }
        mCachedRequest.get(request.tag).add(request);
    }

    public void performRequest(Context context, Request request, boolean isShow) {
        request.execute(context, executor, isShow);
        if (!mCachedRequest.containsKey(request.tag)) {
            ArrayList<Request> requests = new ArrayList<>();
            mCachedRequest.put(request.tag, requests);
        }
        mCachedRequest.get(request.tag).add(request);
    }

    /**
     * @param tag 请求的tag
     */
    public void cancel(String tag) {
        if (TextUtils.isEmpty(tag))
            return;
        if (mCachedRequest.containsKey(tag)) {
            ArrayList<Request> requests = mCachedRequest.remove(tag);
            for (Request request : requests) {
                if (!request.isCompleted && !request.isCancelled) {
                    request.cancel(true);
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
