package com.z1ong.android.library.router;

import android.app.Activity;

import com.alibaba.android.arouter.launcher.ARouter;

/**
 * Author      : FJ
 * CreateDate  : 2019/06/13 14:32
 * Description :
 */

public class Router {

    /**
     * @param router 即将跳转到的目标地址
     * Author      : FJ
     * CreateDate  : 2019/6/24 0024 上午 10:20
     * Description : 普通跳转    
     */
    public static void navigation(AbstractRouter router){
        ARouter.getInstance().build(router.getPath())
                .withSerializable(router.getTAG(), router)
                .navigation();
    }
    
    /**
     * @param router 即将跳转到的目标地址
     * @param clazz  在目标地址中调用AbstractRouter.back()后需要跳转的地址
     * Author      : FJ
     * CreateDate  : 2019/6/24 0024 上午 9:35
     * Attention   : 返回地址要创建对应返回值的构造方法，参数顺序也要相同，否则不能返回
     */
    public static void navigation(AbstractRouter router, Class<? extends AbstractRouter> clazz){
        router.setBackRouter(clazz);
        ARouter.getInstance().build(router.getPath())
                .withSerializable(router.getTAG(), router)
                .navigation();
    }

    /**
     * Author      : FJ
     * CreateDate  : 2019/6/24 0024 上午 10:20
     * Description : 支持Activity.setResult()的跳转
     * @param router 即将跳转到的目标地址
     * @param context 上下文
     * @param requestCode 同startActivityForResult的requestCode
     */
    public static void navigation(AbstractRouter router, Activity context, int requestCode){
        ARouter.getInstance().build(router.getPath())
                .withSerializable(router.getTAG(), router)
                .navigation(context, requestCode);
    }
}
