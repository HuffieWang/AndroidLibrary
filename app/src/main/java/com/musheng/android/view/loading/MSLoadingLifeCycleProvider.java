package com.musheng.android.view.loading;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/08/07 14:32
 * Description : 为了能够将加载提示与其他组件生命周期绑定，需要实现此生命周期提供器
 */
public interface MSLoadingLifeCycleProvider<T> {

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/8/7 14:43
     * Description : 将输入的组件进行动态代理，添加加载提示的show()和hide()，将代理对象返回
     */
    T create(MSLoading loading, T t);

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/8/7 14:44
     * Description : 判断输入的组件是否隶属于此生命周期提供器，用instance of判断即可
     */
    boolean isInstance(Object object);
}
