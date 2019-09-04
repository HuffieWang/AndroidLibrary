package com.musheng.android.fetcher;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/07/10 17:16
 * Description : 请求后返回的结果
 */
public abstract class MSFetcherResponse<R extends MSFetcherRequest, E> {
    public void onStart(){

    }
    public abstract void onNext(E entity, R request);
    public abstract void onError(MSFetcherThrowable error);
    public abstract void onCancel();
}
