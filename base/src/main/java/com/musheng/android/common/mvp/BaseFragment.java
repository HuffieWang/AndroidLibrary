package com.musheng.android.common.mvp;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.musheng.android.common.toast.MSToast;
import com.musheng.android.common.toast.MSToastContent;
import com.musheng.android.common.util.SharePreferenceUtil;
import com.musheng.android.router.MSBaseRouter;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshLoadMoreListener;

import java.util.Objects;

import butterknife.ButterKnife;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/8/29 11:01
 * Description :
 */
public abstract class BaseFragment <P extends IBasePresenter> extends Fragment implements IBaseView<P> {

    private View contentView;

    protected P presenter;

    protected abstract P initPresenter();

    protected abstract View getRootView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState);

    protected abstract void initWidget();

    private SmartRefreshLayout smartRefreshLayout;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        presenter = initPresenter();
        contentView =null;
        try {
            contentView = getRootView(inflater, container, savedInstanceState);
            ButterKnife.bind(this, contentView);
            initWidget();
        }catch (Exception e){
            e.printStackTrace();
        }
        return contentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (presenter != null) {
            presenter.onViewAttached(this);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        presenter.onViewRefresh();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(presenter != null){
            presenter.onViewInvisible();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (presenter != null) {
            presenter.onViewDetached();
        }
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
        return getActivity();
    }

    @Override
    public Intent getViewIntent() {
        return Objects.requireNonNull(getActivity()).getIntent();
    }

    @Override
    public void setViewIntent(Intent intent) {
        Objects.requireNonNull(getActivity()).setIntent(intent);
    }

    @Override
    public ViewGroup getViewGroup() {
        return (ViewGroup) contentView;
    }

    @Override
    public void finish(boolean isFinishAll, boolean containSelf) {
        if(!isFinishAll){
            Objects.requireNonNull(getActivity()).finish();
        }
    }

    @Override
    public void loadMoreComplete(int id, boolean isSuccess, boolean isNoMoreData) {
        if(smartRefreshLayout != null){
            smartRefreshLayout.finishRefresh(500, isSuccess, isNoMoreData);
            smartRefreshLayout.finishLoadMore(500, isSuccess, isNoMoreData);
        }
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

    public View getContentView(){
        return contentView;
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


    protected void enableByInput(final View view, final BaseActivity.OnEnableChangeListener listener, final EditText... inputs){
        if(inputs.length == 0){
            return;
        }
        for(EditText editText : inputs){
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    int i = 0;
                    for(; i < inputs.length; i++){
                        if(TextUtils.isEmpty(inputs[i].getText().toString())){
                            break;
                        }
                    }
                    boolean isEnable = i == inputs.length;
                    if(listener == null){
                        view.setEnabled(isEnable);
                    } else {
                        view.setEnabled(listener.onChange(isEnable));
                    }
                }
            });
        }
    }

    public interface OnEnableChangeListener{
        boolean onChange(boolean isEnable);
    }

}