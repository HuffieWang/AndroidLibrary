package com.musheng.android.common.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/11/29 10:55
 * Description :
 */
public class MoneyVoiceUtils {

    private static final char[] NUM = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static final char[] CHINESE_UNIT = {'元', '拾', '佰', '仟', '万', '拾', '佰', '仟', '亿', '拾', '佰', '仟'};

    /**
     * 返回关于钱的中文式大写数字,支仅持到亿
     */
    public static String readInt(int moneyNum) {
        String res = "";
        int i = 0;
        if (moneyNum == 0) {
            return "0";
        }

        if (moneyNum == 10) {
            return "拾";
        }

        if (moneyNum > 10 && moneyNum < 20) {
            return "拾" + moneyNum % 10;
        }

        while (moneyNum > 0) {
            res = CHINESE_UNIT[i++] + res;
            res = NUM[moneyNum % 10] + res;
            moneyNum /= 10;
        }

        return res.replaceAll("0[拾佰仟]", "0")
                .replaceAll("0+亿", "亿")
                .replaceAll("0+万", "万")
                .replaceAll("0+元", "元")
                .replaceAll("0+", "0")
                .replace("元", "");
    }


    /**
     * 返回数字对应的音频
     *
     * @param integerPart
     * @return
     */
    private static List<String> readIntPart(String integerPart) {
        List<String> result = new ArrayList<>();
        String intString = readInt(Integer.parseInt(integerPart));
        int len = intString.length();
        for (int i = 0; i < len; i++) {
            char current = intString.charAt(i);
            if (current == '拾') {
//                result.add(VoiceConstants.TEN);
            } else if (current == '佰') {
//                result.add(VoiceConstants.HUNDRED);
            } else if (current == '仟') {
//                result.add(VoiceConstants.THOUSAND);
            } else if (current == '万') {
//                result.add(VoiceConstants.TEN_THOUSAND);
            } else if (current == '亿') {
//                result.add(VoiceConstants.TEN_MILLION);
            } else {
                result.add(String.valueOf(current));
            }
        }
        return result;

    }
}
