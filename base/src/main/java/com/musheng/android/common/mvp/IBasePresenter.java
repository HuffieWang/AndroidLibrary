package com.musheng.android.common.mvp;

public interface IBasePresenter<V extends IBaseView> {
    boolean isActive();
    void onViewAttached(V view);
    void onViewDetached();
    void onViewInvisible();
    void onViewRefresh();
    void loadMore();
    void refresh();
    V getView();
}