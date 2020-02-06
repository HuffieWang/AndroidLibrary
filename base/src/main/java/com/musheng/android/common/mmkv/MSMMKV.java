package com.musheng.android.common.mmkv;

import com.tencent.mmkv.MMKV;

public abstract class MSMMKV {

    public boolean encode(Object key, boolean value) {
        return type(key).encode(key.toString(), value);
    }

    public boolean decodeBool(Object key){
        return type(key).decodeBool(key.toString());
    }

    public boolean encode(Object key, int value) {
        return type(key).encode(key.toString(), value);
    }

    public int decodeInt(Object key){
        return type(key).decodeInt(key.toString());
    }

    public boolean encode(Object key, long value) {
        return type(key).encode(key.toString(), value);
    }

    public long decodeLong(Object key){
        return type(key).decodeLong(key.toString());
    }

    public boolean encode(Object key, float value) {
        return type(key).encode(key.toString(), value);
    }

    public float decodeFloat(Object key){
        return type(key).decodeFloat(key.toString());
    }

    public boolean encode(Object key, double value) {
        return type(key).encode(key.toString(), value);
    }

    public double decodeDouble(Object key){
        return type(key).decodeDouble(key.toString());
    }

    public boolean encode(Object key, String value) {
        return type(key).encode(key.toString(), value);
    }

    public String decodeString(Object key){
        return type(key).decodeString(key.toString());
    }

    public abstract MMKV type(Object key);
}
