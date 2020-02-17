package com.musheng.android.common.util;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

/**
 * 2019/5/13
 * Author LDL
 **/
public class KeyboardUtil {

    public static void showKeyboard(final View view) {
        view.postDelayed(new Runnable() {
            @Override
            public void run() {
                InputMethodManager
	                imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null) {
                    view.requestFocus();
                    imm.showSoftInput(view, 0);
                }
            }
        },100);
    }

    public static void showSoftInput(Context context, View view) {
        try {

            InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (view != null && imm != null) {
                 imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
//                imm.showSoftInput(view, 0);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT);
            // 或者第二个参数传InputMethodManager.SHOW_IMPLICIT
        }
    }

    public static void hideKeyboard(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(),0);
        }
    }
    public static void  toggleSoftInput(View view){
        InputMethodManager imm = (InputMethodManager) view.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.toggleSoftInput(0,0);
        }
    }

}
