package com.musheng.android.common.mvp;

import com.musheng.android.fetcher.MSFetcherRequest;
import com.musheng.android.fetcher.MSFetcherResponse;
import com.musheng.android.fetcher.MSFetcherThrowable;


import java.util.List;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/9/30 14:18
 * Description :
 */
public abstract class LoadMoreFetcherResponse<R extends MSFetcherRequest, E> extends MSFetcherResponse<R, E> {

    private LoadMoreCallback loadMoreCallback;

    private String pageIndex;

    public LoadMoreFetcherResponse(LoadMoreCallback loadMoreCallback, String pageIndex) {
        this.loadMoreCallback = loadMoreCallback;
        this.pageIndex = pageIndex;
    }

    public abstract void onLoadMoreNext(E entity, R request);

    public abstract void onLoadMoreError(MSFetcherThrowable error);

    public abstract void onLoadMoreCancel();

    public List getList(E entity){
        return null;
    }

    public int getTotalCount(E entity){
        return 0;
    }

    @Override
    public void onNext(E entity, R request) {
        List source = getList(entity);
        if(source != null){
            List merge = loadMoreCallback.loadMoreSuccess(pageIndex, source, getTotalCount(entity));
            source.clear();
            source.addAll(merge);
        }
        onLoadMoreNext(entity, request);
    }

    @Override
    public void onError(MSFetcherThrowable error) {
        loadMoreCallback.loadMoreFail(pageIndex, 0);
        onLoadMoreError(error);
    }

    @Override
    public void onCancel() {
        loadMoreCallback.loadMoreFail(pageIndex, 0);
        onLoadMoreCancel();
    }
}
