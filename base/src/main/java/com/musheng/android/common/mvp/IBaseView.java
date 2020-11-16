package com.musheng.android.common.mvp;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;

import com.musheng.android.common.toast.MSToastContent;

public interface IBaseView<P extends IBasePresenter> {

    Context getViewContext();
    Intent getViewIntent();
    ViewGroup getViewGroup();
    P getPresenter();
    void setViewIntent(Intent intent);
    void showTips(String tips);
    void showErrorTips(String tips);
    void showCustomTips(MSToastContent tips);
    void finish(boolean isFinishAll, boolean containSelf);
    void loadMoreComplete(int id, boolean isSuccess, boolean isNoMoreData);
}
