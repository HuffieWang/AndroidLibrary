package com.musheng.android.common.wx;

import android.content.Context;

import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/12/9 17:19
 * Description :
 */
public class WXApi {

    private static IWXAPI wxapi;

    public static void init(Context context, String appID) {
        wxapi = WXAPIFactory.createWXAPI(context, appID, true);
        wxapi.registerApp(appID);
    }

    public static IWXAPI getWxapi() {
        return wxapi;
    }
}
