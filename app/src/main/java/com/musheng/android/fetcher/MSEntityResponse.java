package com.musheng.android.fetcher;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/07/10 17:16
 * Description : 请求后返回的结果
 */
public interface MSEntityResponse<R extends MSEntityRequest, E> {
    void onNext(E entity, R request);
    void onError(MSEntityThrowable error);
    void onCancel();
}
