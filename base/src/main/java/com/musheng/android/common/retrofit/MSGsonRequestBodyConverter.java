package com.musheng.android.common.retrofit;

import com.google.gson.Gson;

import java.io.IOException;

import okhttp3.RequestBody;
import retrofit2.Converter;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/8/29 20:04
 * Description :
 */
public class MSGsonRequestBodyConverter<T> implements Converter<T, RequestBody> {

    private final Gson gson;

    public MSGsonRequestBodyConverter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), gson.toJson(value));
    }
}
