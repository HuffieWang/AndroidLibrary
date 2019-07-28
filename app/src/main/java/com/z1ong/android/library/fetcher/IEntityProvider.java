package com.z1ong.android.library.fetcher;

import java.util.List;

public interface IEntityProvider<R extends IEntityRequest, E> {
    
    /**
     * Author      : FJ
     * CreateDate  : 2019/7/19 0019 下午 5:47
     * Description : 是否强制触发该数据提供器，为true时即使其他的获取器已经返回了数据，也会继续执行和回调
     */
    boolean isForceTrigger();

    /**
     * Author      : FJ
     * CreateDate  : 2019/7/19 0019 下午 5:47
     * Description : 设置是否强制触发该数据提供器
     * @param isForceTrigger 为true时即使其他的获取器已经返回了数据，也会继续执行和回调
     */
    void setForceTrigger(boolean isForceTrigger);
    
    /**
     * Author      : FJ
     * CreateDate  : 2019/7/19 0019 下午 5:48
     * Description : 获取数据（同步）
     * @param request : 请求参数（根据需要自行定义），会传入数据提供器的List<E> getEntity(R request)方法
     * @return : 获取到的数据，即使只有一条数据也封装至List中，当获取失败时返回null
     */
    List<E> getEntity(R request);
    
    /**
     * Author      : FJ
     * CreateDate  : 2019/7/19 0019 下午 5:50
     * Description : 设置数据（同步，非必须），例如基于数据库的数据提供器，需要在这里对数据进行存库，
     *               但是基于网络的数据提供器则不需要，空着即可
     * @param request : 请求参数
     * @param entity : 请求到数据
     */
    void setEntity(R request, List<E> entity);
}
