package com.musheng.android.router;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/06/13 14:34
 * Description : 1. PATH即RouteTable中的路由地址，格式为"/path/+类名"
 *               2. 构造方法的输入参数即RouteExtra中的路由参数
 */

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

public abstract class MSBaseRouter implements Serializable {

    public static final String TAG = "ROUTER";

    private Class<? extends MSBaseRouter> backRouter;

    public Class<? extends MSBaseRouter> getBackRouter() {
        return backRouter;
    }

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/6/24 0024 上午 10:20
     * Description : 设置返回地址    
     */
    public void setBackRouter(Class<? extends MSBaseRouter> backRouter) {
        this.backRouter = backRouter;
    }
    
    /**
     * Author      : MuSheng
     * CreateDate  : 2019/6/24 0024 上午 10:20
     * Description : 获取设置的返回地址   
     */
    public String getBackRouterPath(){
        if(backRouter != null){
            try {
                return backRouter.newInstance().getPath();
            } catch (Exception e){
            }
        }
        return null;
    }
    
    public abstract String getPath();

    public String getTAG(){
        return TAG;
    }
    
    /**
     * Author      : MuSheng
     * CreateDate  : 2019/6/24 0024 上午 10:18
     * Description : 返回到设置的返回地址
     * Attention   : 返回地址要创建对应返回值的构造方法，参数顺序也要相同，否则不能返回
     */
    public boolean back(Object... objects){
        if(backRouter == null){
            return false;
        }
        try {
            Constructor<?> constructor = null;
            Constructor<?>[] declaredConstructors = backRouter.getDeclaredConstructors();
            for(int i = 0; i < declaredConstructors.length; i++){
                Constructor<?> c = declaredConstructors[i];
                Class<?>[] parameterTypes = c.getParameterTypes();
                if(parameterTypes.length == objects.length){
                    if(parameterTypes.length == 0) {
                        constructor = c;
                        break;
                    } else {
                        for(int j = 0; j < parameterTypes.length; j++){
                            if(parameterTypes[j] != objects[j].getClass()){
                                Field type = null;
                                try {
                                    type = objects[j].getClass().getDeclaredField("TYPE");
                                } catch (NoSuchFieldException ignored) {
                                }
                                if(type == null || (type).get(null) != parameterTypes[j]){
                                    break;
                                }
                            }
                            if(j == parameterTypes.length -1){
                                constructor = c;
                            }
                        }
                    }
                }
            }
            if(constructor != null){
                MSRouter.navigation((MSBaseRouter) constructor.newInstance(objects));
                return true;
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
