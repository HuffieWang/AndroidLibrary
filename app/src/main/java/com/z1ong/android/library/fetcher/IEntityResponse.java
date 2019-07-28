package com.z1ong.android.library.fetcher;

import java.util.List;

public interface IEntityResponse<R extends IEntityRequest, E> {
    void onNext(List<E> entity, R request);
    void onError(BaseEntityThrowable error);
}
