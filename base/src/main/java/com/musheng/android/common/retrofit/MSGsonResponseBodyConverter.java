package com.musheng.android.common.retrofit;


import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.ResponseBody;
import retrofit2.Converter;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/8/29 20:04
 * Description :
 */
public class MSGsonResponseBodyConverter<T> implements Converter<ResponseBody, T> {

    public static ResponseConverter converter;

    private final Gson gson;
    private final TypeAdapter<T> adapter;

    public MSGsonResponseBodyConverter(Gson gson, TypeAdapter<T> adapter) {
        this.gson = gson;
        this.adapter = adapter;
    }

    @Override
    public T convert(ResponseBody value) throws IOException {
        try {
            String response = value.string();
            JSONObject json = new JSONObject(response);
            Object convert = converter.convert(adapter, json);
            value.close();
            return (T)convert;
        } catch (JSONException e) {
            e.printStackTrace();
            throw new IOException("网络异常");
        }
    }

    public interface ResponseConverter<T>{
        T convert(TypeAdapter adapter, JSONObject json) throws IOException, JSONException;
    }
}
