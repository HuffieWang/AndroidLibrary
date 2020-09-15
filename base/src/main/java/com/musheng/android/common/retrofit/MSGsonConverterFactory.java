package com.musheng.android.common.retrofit;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/8/29 19:27
 * Description :
 */
public class MSGsonConverterFactory extends Converter.Factory {

    private static GsonProvider gsonProvider;

    private final Gson gson;

    public static MSGsonConverterFactory create() {
        if(gsonProvider != null){
            return new MSGsonConverterFactory(gsonProvider.create());
        }
        return new MSGsonConverterFactory(new Gson());
    }

    public static void setGsonProvider(GsonProvider gsonProvider) {
        MSGsonConverterFactory.gsonProvider = gsonProvider;
    }

    private MSGsonConverterFactory(Gson gson) {
        if (gson == null) {
            throw new NullPointerException("gson == null");
        }
        this.gson = gson;
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations,
                                                            Retrofit retrofit) {
        TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
        return new MSGsonResponseBodyConverter<>(gson, adapter);
    }

    @Override
    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {
        return new MSGsonRequestBodyConverter<>(gson);
    }

    public interface GsonProvider{
        Gson create();
    }

}
