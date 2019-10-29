package com.musheng.android.common.share;

import android.content.Context;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

public class WXShare {

    private static final int TIMELINE_SUPPORTED_VERSION = 0x21020001;
    private static String appID = "wxbe3077f095916455";

    private static Context mContext;
    private static IWXAPI wxapi;

    public static void init(Context context){
        mContext = context;
        wxapi = WXAPIFactory.createWXAPI(mContext, appID, true);
        wxapi.registerApp(appID);
    }

    private static String buildTransaction(String type) {
        return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
    }

    private static void shareUrlToWx(String url, String title, String desc, final int wxSceneSession) {

        WXWebpageObject webpage = new WXWebpageObject();
        webpage.webpageUrl = url;
        final WXMediaMessage msg = new WXMediaMessage(webpage);
        msg.title = title;
        msg.description = desc;
        new Thread(new Runnable() {
            @Override
            public void run() {
                SendMessageToWX.Req req = new SendMessageToWX.Req();
                req.transaction = buildTransaction("webpage");
                req.message = msg;
                req.scene = wxSceneSession;
                wxapi.sendReq(req);
            }
        }).start();
    }

    public static void shareUrltoWx(String url, String title, String desc){
        shareUrlToWx(url, title, desc, 0);
    }

    public static void shareUrltoWxCircle(String url, String title, String desc){
        shareUrlToWx(url, title, desc, 1);
    }
}
