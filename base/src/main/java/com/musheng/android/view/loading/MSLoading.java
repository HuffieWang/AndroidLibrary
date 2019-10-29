package com.musheng.android.view.loading;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.musheng.android.library.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/08/07 14:32
 * Description : 加载提示，支持绑定其他组件的生命周期，支持自定义显示内容
 */
public class MSLoading {

    private static List<MSLoadingLifeCycleProvider> lifeCycleProviders = new ArrayList<>();
    private static MSLoadingViewProvider contentProvider;

    private WeakReference<ViewGroup> viewGroupWeakReference;
    private View loadingView;

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/8/7 14:20
     * Description : 添加支持绑定生命周期的组件，在MSLoadingLifeCycleProvider中应合理调用show()和
     *               和hide()
     */
    public static void addLifeCycleProvider(MSLoadingLifeCycleProvider provider){
        lifeCycleProviders.add(provider);
    }


    /**
     * Author      : MuSheng
     * CreateDate  : 2019/8/7 14:32
     * Description : 设置自定义的显示内容，如不调用此方法，将显示默认的加载动画
     */
    public static void setViewProvider(MSLoadingViewProvider provider){
        contentProvider = provider;
    }

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/8/7 14:33
     * Description : 将加载器绑定于指定组件的生命周期，组件类型需要提前在addLifeCycleProvider()注册
     * @param viewGroup : 加载提示显示的位置
     * @param t : 用于绑定生命周期的组件
     */
    public static <T> T bindLifeCycle(ViewGroup viewGroup, T t){
        for(MSLoadingLifeCycleProvider provider : lifeCycleProviders){
            if(provider.isInstance(t)){
                MSLoading msLoading = new MSLoading(viewGroup);
                return (T) provider.create(msLoading, t);
            }
        }
        return t;
    }

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/8/7 14:33
     * Description : 将加载器绑定于指定组件的生命周期，组件类型需要提前在addLifeCycleProvider()注册
     * @param loading : 初始化好的loading
     * @param t : 用于绑定生命周期的组件
     */
    public static <T> T bindLifeCycle(MSLoading loading, T t){
        for(MSLoadingLifeCycleProvider provider : lifeCycleProviders){
            if(provider.isInstance(t)){
                return (T) provider.create(loading, t);
            }
        }
        return t;
    }

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/8/7 14:36
     * Description : 加载提示可以直接实例化使用
     * @param viewGroup : 加载提示显示的位置
     */
    public MSLoading(ViewGroup viewGroup) {
        viewGroupWeakReference = new WeakReference<>(viewGroup);
    }

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/8/7 14:37
     * Description : 显示加载提示
     */
    public void show(){
        ViewGroup viewGroup = viewGroupWeakReference.get();
        if(viewGroup != null){
            if(loadingView != null){
                viewGroup.removeView(loadingView);
                loadingView = null;
            }
            if (contentProvider != null){
                loadingView = contentProvider.createView(viewGroup);
                viewGroup.addView(loadingView, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            } else {
                RelativeLayout layout = new RelativeLayout(viewGroup.getContext());
                layout.setBackgroundColor(Color.parseColor("#FFFFFF"));
                ImageView imageView = new ImageView(viewGroup.getContext());
                RequestOptions options = new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
                Glide.with(viewGroup.getContext()).load(R.drawable.ic_loading_default).apply(options).into(imageView);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(108*2, 144*2);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                layout.addView(imageView, layoutParams);
                viewGroup.addView(layout, new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
                loadingView = layout;
            }
        }
    }

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/8/7 14:38
     * Description : 隐藏加载提示
     */
    public void hide(){
        ViewGroup viewGroup = viewGroupWeakReference.get();
        if(viewGroup != null && loadingView != null){
            viewGroup.removeView(loadingView);
            loadingView = null;
        }
    }
}
