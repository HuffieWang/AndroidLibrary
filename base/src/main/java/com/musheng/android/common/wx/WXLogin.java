package com.musheng.android.common.wx;

import com.tencent.mm.opensdk.modelmsg.SendAuth;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/11/27 14:23
 * Description :
 */
public class WXLogin {

    public WXLogin() {
    }

    public void auth() {
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "lianhe_mall";
        WXApi.getWxapi().sendReq(req);
    }
}
