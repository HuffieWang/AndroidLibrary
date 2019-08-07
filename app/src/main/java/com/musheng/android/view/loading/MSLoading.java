package com.musheng.android.view.loading;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.z1ong.android.library.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

public class MSLoading {

    private static List<MSLoadingLifeCycleProvider> lifeCycleProviders = new ArrayList<>();
    private static MSLoadingViewProvider contentProvider;

    private WeakReference<ViewGroup> viewGroupWeakReference;
    private View loadingView;

    public static void addLifeCycleProvider(MSLoadingLifeCycleProvider provider){
        lifeCycleProviders.add(provider);
    }

    public static void setViewProvider(MSLoadingViewProvider provider){
        contentProvider = provider;
    }

    public static <T> T bindLifeCycle(ViewGroup viewGroup, T t){
        for(MSLoadingLifeCycleProvider provider : lifeCycleProviders){
            if(provider.isInstance(t)){
                MSLoading msLoading = new MSLoading(viewGroup);
                return (T) provider.create(msLoading, t);
            }
        }
        return t;
    }

    public MSLoading(ViewGroup viewGroup) {
        viewGroupWeakReference = new WeakReference<>(viewGroup);
    }

    public void show(){
        ViewGroup viewGroup = viewGroupWeakReference.get();
        if(viewGroup != null){
            if (contentProvider != null){
                loadingView = contentProvider.createView();
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

    public void hide(){
        ViewGroup viewGroup = viewGroupWeakReference.get();
        if(viewGroup != null && loadingView != null){
            viewGroup.removeView(loadingView);
        }
    }
}
