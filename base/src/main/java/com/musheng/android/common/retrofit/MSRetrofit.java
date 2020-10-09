package com.musheng.android.common.retrofit;

import android.text.TextUtils;

import com.musheng.android.common.util.SharePreferenceUtil;
import com.tencent.mmkv.MMKV;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import retrofit2.Retrofit;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/8/30 10:02
 * Description :
 */
public class MSRetrofit {

    public final static int CONNECT_TIMEOUT = 60;
    public final static int READ_TIME_OUT = 60;
    public final static int WRITE_TIME_OUT = 60;

    private static String baseUrl;
    private static MSRetrofit instance;
    private static RetrofitApiProvider sApiProvider;
    private static RetrofitHeaderProvider sHeaderProvider;
    private static OkHttpClient okHttpClient;

    private static Retrofit retrofit;

    private MSRetrofit(){
        if(okHttpClient == null){
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(MSGsonConverterFactory.create())
                    .client(new OkHttpClient.Builder()
                            .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                            .readTimeout(READ_TIME_OUT, TimeUnit.SECONDS)
                            .addNetworkInterceptor(new HeaderInterceptor())
                            .addInterceptor(new AuthInterceptor())
                            .writeTimeout(WRITE_TIME_OUT, TimeUnit.SECONDS).build())
                    .build();
        }else{
            retrofit = new Retrofit.Builder()
                    .baseUrl(baseUrl)
                    .addConverterFactory(MSGsonConverterFactory.create())
                    .client(okHttpClient)
                    .build();
        }
    }

    public interface RetrofitApiProvider {
        Object provideApi(Retrofit retrofit);
    }

    public interface RetrofitHeaderProvider{
        Map<String, String> provideHeader();
    }

    public static void setRetrofitUrl(String url){
        baseUrl = url;
    }

    public static void setOkhttpClient(OkHttpClient client){
        okHttpClient = client;
    }

    public static void setRetrofitApiProvider(RetrofitApiProvider apiProvider){
        sApiProvider = apiProvider;
    }

    public static void setRetrofitHeaderProvider(RetrofitHeaderProvider headerProvider){
        sHeaderProvider = headerProvider;
    }

    public static Object getApi(){
        if(instance == null){
            instance = new MSRetrofit();
        }
        return sApiProvider.provideApi(retrofit);
    }

    public static Retrofit getRetrofit(){
        return retrofit;
    }

    // 請求攔截
    public static class HeaderInterceptor implements Interceptor {

        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            Request originalRequest = chain.request();
            if (true) {
                String token = MMKV.defaultMMKV().decodeString("token");
                String regId = SharePreferenceUtil.getString("registration_id");
                if(TextUtils.isEmpty(regId)){
                    regId = "";
                }
                Request updateRequest = null;
                if(sHeaderProvider == null){
                    updateRequest = originalRequest.newBuilder()
                            .header("Authorization", token == null ? "" : token)
                            .header("registration_id",regId)
                            .build();
                } else {
                    Request.Builder builder = originalRequest.newBuilder();
                    Map<String, String> headers = sHeaderProvider.provideHeader();
                    Set<String> ks = headers.keySet();
                    for(String k : ks) {
                        builder.header(k, headers.get(k));
                    }
                    updateRequest = builder.build();
                }
                return chain.proceed(updateRequest);
            } else {
                return chain.proceed(originalRequest);
            }
        }
    }

    // 返回攔截
    private static class AuthInterceptor implements Interceptor {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());

            return originalResponse;
        }
    }
}
