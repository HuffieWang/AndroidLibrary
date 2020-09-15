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
    public static RequestConverter requestConverter;

    public MSGsonRequestBodyConverter(Gson gson) {
        this.gson = gson;
    }

    @Override
    public RequestBody convert(T value) throws IOException {
        return requestConverter.convert(value);
    }

    public interface RequestConverter<T>{
        RequestBody convert(T value);
    }

}
