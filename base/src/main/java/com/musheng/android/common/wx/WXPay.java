package com.musheng.android.common.wx;

import com.musheng.android.common.log.MSLog;
import com.tencent.mm.opensdk.modelpay.PayReq;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/12/9 17:25
 * Description :
 */
public class WXPay {

    public void pay(String appId, String partnerId, String prepayId, String packageValue,
                    String nonceStr, String timeStamp, String sign){

        PayReq request = new PayReq();
        request.appId = appId;
        request.partnerId = partnerId;
        request.prepayId= prepayId;
        request.packageValue = packageValue;
        request.nonceStr= nonceStr;
        request.timeStamp= timeStamp;
        request.sign= sign;
        WXApi.getWxapi().sendReq(request);

        MSLog.d("appId " + appId);
        MSLog.d("partnerId " + partnerId);
        MSLog.d("prepayId " + prepayId);
        MSLog.d("packageValue " + packageValue);
        MSLog.d("nonceStr " + nonceStr);
        MSLog.d("timeStamp " + timeStamp);
        MSLog.d("sign " + sign);
    }
}
