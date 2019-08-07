package com.musheng.android.view.loading;

public interface MSLoadingLifeCycleProvider<T> {
    T create(MSLoading loading, T t);
    boolean isInstance(Object object);
}
