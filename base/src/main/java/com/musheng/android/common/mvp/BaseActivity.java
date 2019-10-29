package com.musheng.android.common.mvp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.alibaba.android.arouter.launcher.ARouter;
import com.jaeger.library.StatusBarUtil;
import com.musheng.android.common.toast.MSToast;
import com.musheng.android.common.toast.MSToastContent;
import com.musheng.android.common.util.NavigationBarUtil;
import com.musheng.android.router.MSBaseRouter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.ArrayList;

import butterknife.ButterKnife;

public abstract class BaseActivity <P extends IBasePresenter> extends AppCompatActivity implements IBaseView<P> {

    protected P presenter;

    protected abstract P initPresenter();

    protected abstract void setRootView(Bundle savedInstanceState);

    protected abstract void initWidget();

    private static ArrayList<BaseActivity> activities = new ArrayList<>();

    private static BaseActivity topActivity;

    private SmartRefreshLayout smartRefreshLayout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = initPresenter();
        ARouter.getInstance().inject(this);
        NavigationBarUtil.assistActivity(findViewById(android.R.id.content));
        StatusBarUtil.setTranslucent(this);

        setRootView(savedInstanceState);
        ButterKnife.bind(this);
        initWidget();
        activities.add(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (presenter != null) {
            presenter.onViewAttached(this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        topActivity = this;
        presenter.onViewRefresh();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (presenter != null) {
            presenter.onViewInvisible();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onViewDetached();
        }
        activities.remove(this);
    }

    @Override
    public P getPresenter() {
        return presenter;
    }

    @Override
    public void showTips(String tips) {
        Toast toast = MSToast.makeToast(getViewContext(), tips, BasePresenter.sToastSuccessIcon);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    @Override
    public void showErrorTips(String tips) {
        Toast toast = MSToast.makeToast(getViewContext(), tips, BasePresenter.sToastFailIcon);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


    @Override
    public void showCustomTips(MSToastContent tips) {
        Toast toast = MSToast.makeToast(tips);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    @Override
    public Context getViewContext() {
        return this;
    }

    @Override
    public Intent getViewIntent() {
        return getIntent();
    }

    @Override
    public void setViewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    public ViewGroup getViewGroup() {
        return findViewById(android.R.id.content);
    }

    @Override
    public void finish(boolean isFinishAll, boolean containSelf) {
        if(isFinishAll){
            if (!activities.isEmpty()) {
                for (BaseActivity activity : activities) {
                    if (containSelf || !activity.equals(BaseActivity.this)) {
                        activity.finish();
                    }
                }
            }
        } else {
            finish();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        super.onNewIntent(intent);
    }

    @Override
    public void loadMoreComplete(int id, boolean isSuccess, boolean isNoMoreData) {
        if(smartRefreshLayout != null){
            smartRefreshLayout.finishLoadMore(500);
            smartRefreshLayout.finishRefresh(500);
            smartRefreshLayout.setNoMoreData(isNoMoreData);
        }
    }

    public static BaseActivity getTopActivity(){
        return topActivity;
    }

    public void setSmartRefreshLayout(SmartRefreshLayout refreshLayout){
        smartRefreshLayout = refreshLayout;
        refreshLayout.setOnRefreshLoadMoreListener(new OnRefreshLoadMoreListener() {
            @Override
            public void onLoadMore(@NonNull RefreshLayout refreshLayout) {
                getPresenter().loadMore();
            }

            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                getPresenter().refresh();
            }
        });
    }

    public <T> T getExtra(Class<T> type){
        try {
            return (T)(getViewIntent().getSerializableExtra(MSBaseRouter.TAG));
        } catch (Exception e){
        }
        return null;
    }

    protected void onViewClicked(View view){

    }

    protected void onTextChanged(Editable editable){

    }
}