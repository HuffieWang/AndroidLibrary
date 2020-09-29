package com.musheng.android.common.retrofit;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.reflect.TypeToken;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;
import retrofit2.Converter;
import retrofit2.Retrofit;

/**
 * Author      : MuSheng
 * CreateDate  : 2020/9/25 16:44
 * Description :
 */
public class MSRetrofit2 {

    private static HashMap<Class, Object> apiMap = new HashMap<>();

    public static  <T> T getApi(Class<T> type){
        Object api = apiMap.get(type);
        if(api == null){
            throw new RuntimeException("MSRetrofit2 " + type.getName()
                    + " not found, have you register it? \n" +
                    "look ->public static <T> void addRetrofit2Provider(Class<T> clazz, final Retrofit2Provider provider)");
        }
        return (T) api;
    }

    public static <T> void addRetrofit2Provider(Class<T> clazz, final Retrofit2Provider provider){

        OkHttpClient.Builder okhttpBuilder = new OkHttpClient.Builder()
                .connectTimeout(provider.getConnectTimeout(), TimeUnit.SECONDS)
                .readTimeout(provider.getReadTimeout(), TimeUnit.SECONDS)
                .writeTimeout(provider.getWriteTimeout(), TimeUnit.SECONDS);

        okhttpBuilder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request.Builder builder = originalRequest.newBuilder();
                Map<String, String> headers = provider.getHeaders();
                Set<String> ks = headers.keySet();
                for(String k : ks) {
                    builder.header(k, headers.get(k));
                }
                Request updateRequest = builder.build();
                return chain.proceed(updateRequest);
            }
        });

        okhttpBuilder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                okhttp3.Response originalResponse = chain.proceed(chain.request());
                return originalResponse;
            }
        });

        final Gson gson = new Gson();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(provider.getBaseUrl())
                .addConverterFactory(new Converter.Factory() {
                    @Override
                    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
                        final TypeAdapter<?> adapter = gson.getAdapter(TypeToken.get(type));
                        return new Converter<ResponseBody, Object>() {
                            @Override
                            public Object convert(ResponseBody value) throws IOException {
                                try {
                                    String response = value.string();
                                    JSONObject json = new JSONObject(response);
                                    Object convert = provider.getResponse(adapter, json);
                                    value.close();
                                    return convert;
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    throw new IOException("Network Error");
                                }
                            }
                        };
                    }

                    @Override
                    public Converter<?, RequestBody> requestBodyConverter(Type type, Annotation[] parameterAnnotations, Annotation[] methodAnnotations, Retrofit retrofit) {

                        return new Converter<Object, RequestBody>() {
                            @Override
                            public RequestBody convert(Object value) throws IOException {
                                return RequestBody.create(okhttp3.MediaType.parse("application/json; charset=utf-8"), gson.toJson(value));
                            }
                        };
                    }
                })
                .client(okhttpBuilder.build())
                .build();

        apiMap.put(clazz, retrofit.create(clazz));
    }

    public static abstract class Retrofit2Provider{
        public abstract String getBaseUrl();
        public abstract Map<String, String> getHeaders();
        public abstract Object getResponse(TypeAdapter adapter, JSONObject json) throws IOException, JSONException;
        public long getConnectTimeout(){
            return 15;
        }
        public long getReadTimeout(){
            return 15;
        }
        public long getWriteTimeout(){
            return 15;
        }
    }
}
