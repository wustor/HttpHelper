package com.wustor.httphelper;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.wustor.httphelper.callback.StringCallback;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initRequest();
    }

    private void initRequest() {
        String url = "http://gank.io/api/search/query/listview/category/Android/count/10/page/1";
        RequestManager requestManager = new RequestManager(url, null, RequestManager.RequestMethod.GET);
        requestManager.setCallback(new StringCallback() {
            @Override
            public void onSuccess(String result) {
                Log.d("d---->", result);
            }

            @Override
            public void onFailure(AppException e) {
                Log.d("d---->", e.toString());

            }
        });

        HttpHelper.getInstance().execute(this, requestManager);//发起请求
    }
}
