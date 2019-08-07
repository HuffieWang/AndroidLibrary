package com.musheng.android.view.loading;

import android.view.View;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/08/07 14:32
 * Description : 为加载提示提供自定义的View
 */
public interface MSLoadingViewProvider {
     /**
      * Author      : MuSheng
      * CreateDate  : 2019/8/7 14:47
      * Description : 创建自定义的View，推荐将layout设置背景色，并将长宽设置为MATCH_PARENT
      */
     View createView();
}
