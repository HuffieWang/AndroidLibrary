package com.musheng.android.common.encrypt;

import android.util.Base64;

import com.musheng.android.common.log.MSLog;

import java.io.ByteArrayOutputStream;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/10 16:11
 * Description :
 */
public class EncryptUtil {

    public static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCXJHnrGrapvDALUXWK0VHuQB3123RtLAkaKrcLWmoZvYC63TdpxWGwOQARSCTr0OIzrouhdNKDFVMzGdhkStQNtNlaqTuPKR/Y8bA2LgiPLDdVsZYLqfpsu7RnKWj4LNMpc2ocKY6Md5DdGJyIscCzn9Dio4AF9K4TyFOMNB0xyQIDAQAB";

    public static String encrypt(String content) {
        try {
            return encryptRSABase64(PUBLIC_KEY, content);
        } catch (Exception e) {
            MSLog.d("encrypt error:" + e.getMessage());
        }
        return content;
    }
    public static String decryptRSABase64(String privateKey, String content) throws Exception{
        byte[] decoded = Base64.decode(privateKey, Base64.NO_WRAP);
        RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
        byte[] inputByte = Base64.decode(content.getBytes("UTF-8"), Base64.NO_WRAP);
        return new String(decryptByPrivateKey(priKey, inputByte));
    }

    public static String encryptRSABase64(String publicKey, String content) throws Exception{
        byte[] decoded = Base64.decode(publicKey, Base64.NO_WRAP);
        RSAPublicKey pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
        String outStr = Base64.encodeToString(encryptByPublicKey(pubKey, content.getBytes()), Base64.NO_WRAP);
        return outStr;
    }

    public static byte[] decryptByPrivateKey(PrivateKey privateKey, byte[] encryptedData) throws Exception {
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(2, privateKey);
        int inputLen = encryptedData.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;

        for(int i = 0; inputLen - offSet > 0; offSet = i * 128) {
            byte[] cache;
            if(inputLen - offSet > 128) {
                cache = cipher.doFinal(encryptedData, offSet, 128);
            } else {
                cache = cipher.doFinal(encryptedData, offSet, inputLen - offSet);
            }

            out.write(cache, 0, cache.length);
            ++i;
        }

        byte[] decryptedData = out.toByteArray();
        out.close();
        return decryptedData;
    }

    public static byte[] encryptByPublicKey(PublicKey publicKey, byte[] data) throws Exception {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(1, publicKey);
        int inputLen = data.length;
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        int offSet = 0;

        for(int i = 0; inputLen - offSet > 0; offSet = i * 117) {
            byte[] cache;
            if(inputLen - offSet > 117) {
                cache = cipher.doFinal(data, offSet, 117);
            } else {
                cache = cipher.doFinal(data, offSet, inputLen - offSet);
            }

            out.write(cache, 0, cache.length);
            ++i;
        }

        byte[] encryptedData = out.toByteArray();
        out.close();
        return encryptedData;
    }
}
