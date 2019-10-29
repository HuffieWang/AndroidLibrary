package com.musheng.android.common.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/10 11:34
 * Description :
 */
public class ViewUtil {

    public static void setChildText(View view, int childId, String text){
        View viewById = view.findViewById(childId);
        if(viewById instanceof TextView){
           TextView textView = (TextView) viewById;
           textView.setText(text);
        }
    }

    public static void setChildTextColor(View view, int childId, int color){
        View viewById = view.findViewById(childId);
        if(viewById instanceof TextView){
           TextView textView = (TextView) viewById;
           textView.setTextColor(color);
        }
    }

    public static void setChildImage(View view, int childId, int resourceId){
        if(resourceId == 0){
            setChildVisibility(view, childId, View.GONE);
            return;
        }
        View viewById = view.findViewById(childId);
        if(viewById instanceof ImageView){
           ImageView textView = (ImageView) viewById;
           textView.setImageResource(resourceId);
        }
    }

    public static void setChildVisibility(View view, int childId, int visibility){
        View viewById = view.findViewById(childId);
        if(viewById != null){
            viewById.setVisibility(visibility);
        }
    }

    public static void setChildOnClickListener(View view, int childId, View.OnClickListener listener){
        View viewById = view.findViewById(childId);
        if(viewById != null){
            viewById.setOnClickListener(listener);
        }
    }

    public static boolean copyToClipboard(Context context, String copyStr) {
        try {
            //获取剪贴板管理器
            ClipboardManager cm = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
            // 创建普通字符型ClipData
            ClipData mClipData = ClipData.newPlainText("Label", copyStr);
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
