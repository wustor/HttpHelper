package com.wustor.httphelper.utils;

import android.content.Context;
import android.util.Log;

import com.kaopiz.kprogresshud.KProgressHUD;

public class HintUtils {
    private static KProgressHUD progressDialog;

    //弹出正在加载的对话框
    public static void showDialog(Context context, String msg) {
        if (progressDialog == null && context != null) {
            Log.d("go---->", "show");
            progressDialog = KProgressHUD.create(context)
                    .setStyle(KProgressHUD.Style.SPIN_INDETERMINATE);
            progressDialog.show();
        }

    }

    //关闭dialog
    public static void closeDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }



}
