package com.musheng.android.fetcher;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/07/10 17:16
 * Description : 请求后返回的结果
 */
public abstract class MSEntityResponse<R extends MSEntityRequest, E> {
    public void onStart(){

    }
    public abstract void onNext(E entity, R request);
    public abstract void onError(MSEntityThrowable error);
    public abstract void onCancel();
}
