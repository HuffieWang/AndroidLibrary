package com.musheng.android.common.mvp;


import com.musheng.android.common.toast.MSToast;
import com.musheng.android.fetcher.MSFetcherIndicator;
import com.musheng.android.library.R;
import com.musheng.android.router.MSBaseRouter;
import com.musheng.android.view.loading.MSLoading;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/8/29 11:01
 * Description :
 */
public abstract class BasePresenter<V extends IBaseView> implements IBasePresenter<V>, MSFetcherIndicator, LoadMoreCallback {

    public static int sLoadMorePageOffset = 0;
    public static int sLoadMorePageSize = 20;

    public static int sToastLayout = R.layout.view_ms_toast;
    public static int sToastView = R.id.ms_toast_message;
    public static int sToastSuccessIcon = R.mipmap.ms_ic_toast_success;
    public static int sToastFailIcon = R.mipmap.ms_ic_toast_fail;

    protected WeakReference<V> viewReference;

    protected boolean isActive;

    protected boolean isVisible;

    protected MSLoading loading;

    protected int page;

    protected HashMap<Integer, List> loadMoreMap = new HashMap<>();

    static {
        MSToast.toastLayout = sToastLayout;
        MSToast.toastView = sToastView;
    }

    @Override
    public void onViewAttached(V view) {
        viewReference = new WeakReference<V>(view);
        loading = new MSLoading(view.getViewGroup());
        this.isActive = true;
    }

    @Override
    public void onViewDetached() {
        if(viewReference != null) {
            viewReference.clear();
            viewReference = null;
        }
        if(loading != null){
            loading.hide();
            loading = null;
        }
        this.isActive = false;
    }

    @Override
    public void onViewInvisible() {
        isVisible = false;
        if(loading != null){
            loading.hide();
        }
    }

    @Override
    public void onViewRefresh() {
        isVisible = true;
        refresh();
    }

    @Override
    public boolean isActive() {
        V view = getView();
        return view != null && isActive;
    }

    @Override
    public boolean isCancel() {
        return !isVisible;
    }

    @Override
    public V getView() {
        if(viewReference != null) {
            return viewReference.get();
        }
        return null;
    }

    @Override
    public  void loadMore(){
        List objects;
        try {
            objects = loadMoreMap.get(0);
            if(objects == null){
                page = sLoadMorePageOffset;
                objects = new ArrayList<>();
                loadMoreMap.put(0, objects);
            }
        } catch (Exception ignore){
            page = sLoadMorePageOffset;
            objects = new ArrayList<>();
            loadMoreMap.put(0, objects);
        }
        onLoadMore(page+1, getPageSize(), objects);
    }

    @Override
    public void refresh(){
        List objects = new ArrayList<>();
        loadMoreMap.put(0, objects);
        onLoadMore(sLoadMorePageOffset, getPageSize(), objects);
    }

    public void onLoadMore(int page, int pageSize, List list){

    }

    public int getPageSize(){
        return sLoadMorePageSize;
    }

    @Override
    public  List loadMoreSuccess(int page, List list, int totalPage){
        this.page = page;
        List cache = loadMoreMap.get(0);
        if(cache != null){
            if(sLoadMorePageOffset == page){
                cache.clear();
            }
            cache.addAll(list);
        }
        getView().loadMoreComplete(0, true, (page + 1 - sLoadMorePageOffset) >= totalPage);
        return cache;
    }

    @Override
    public void loadMoreFail(int page, int totalPage){
        getView().loadMoreComplete(0, false, (page + 1 - sLoadMorePageOffset) >= totalPage);
    }

    public <T> T bindLoading(T t){
        return MSLoading.bindLifeCycle(loading, t);
    }

    public <T> T getExtra(Class<T> type){
        try {
            return (T)(getView().getViewIntent().getSerializableExtra(MSBaseRouter.TAG));
        } catch (Exception e){
        }
        return null;
    }

    public String getString(int resId){
        return getView().getViewContext().getResources().getString(resId);
    }
}
