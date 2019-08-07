package com.musheng.android.fetcher;

import java.util.List;

public interface IEntityResponse<R extends IEntityRequest, E> {
    void onNext(List<E> entity, R request);
    void onError(BaseEntityThrowable error);
}
