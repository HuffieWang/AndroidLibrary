package com.z1ong.android.library.fetcher;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Author      : FJ
 * CreateDate  : 2019/07/10 17:16
 * Description : 链式数据获取器，可为Entity自定义多种数据提供器（网络、数据库、缓存）
 */
public class BaseEntityFetcher<R extends IEntityRequest, E> {

    private LinkedList<IEntityProvider<R, E>> entityProviders = new LinkedList<>();

    /**
     * Author      : FJ
     * CreateDate  : 2019/7/19 0019 下午 5:54
     * Description : 推荐开发时继承这个默认数据提供器，可省去写isForceTrigger()等实现和泛型<R,E>的代码      
     */
    public class DefaultProvider implements IEntityProvider<R, E> {
        
        private boolean isForceTrigger;
        
        @Override
        public boolean isForceTrigger() {
            return isForceTrigger;
        }

        @Override
        public void setForceTrigger(boolean isForceTrigger) {
            this.isForceTrigger = isForceTrigger;
        }

        @Override
        public List<E> getEntity(R request) {
            return null;
        }

        @Override
        public void setEntity(R request, List<E> entity) {
        }
    }

    /**
     * Author      : FJ
     * CreateDate  : 2019/7/19 0019 下午 5:57
     * Description : 设置支持的数据提供器
     * @param entityProvider : 数据提供器，可设置多个（参数顺序即为调用顺序） 
     */
    public void setEntityProvider(IEntityProvider<R, E>...entityProvider) {
        entityProviders.clear();
        if(entityProvider != null && entityProvider.length > 0){
            Collections.addAll(entityProviders, entityProvider);
        }
    }
    
    /**
     * Author      : FJ
     * CreateDate  : 2019/7/19 0019 下午 5:52
     * Description : 异步调用支持的数据提供器，结果回调在主线程
     * @param request : 请求参数（根据需要自行定义），会传入数据提供器的List<E> getEntity(R request)方法
     * @param response : 返回结果，成功和失败都回调在主线程
     */
    public Disposable getEntity(final R request, final IEntityResponse<R, E> response){

        return Observable.create(new ObservableOnSubscribe<List<E>>() {
            @Override
            public void subscribe(ObservableEmitter<List<E>> emitter) throws Exception {
                if(entityProviders == null || entityProviders.isEmpty()) {
                    emitter.onError(new BaseEntityThrowable( "EntityHandler not found"));
                    return;
                }
                int index = 0;
                int handleIndex = 0;
                List<E> entity = null;
                for(; index < entityProviders.size(); index++){
                    IEntityProvider<R, E> handler = entityProviders.get(index);
                    if(entity != null && !handler.isForceTrigger()){
                        continue;
                    }
                    entity = handler.getEntity(request);
                    if(entity != null){
                        handleIndex = index;
                        emitter.onNext(entity);
                    }
                }
                if (entity == null) {
                    emitter.onError(new BaseEntityThrowable( "Entity is null"));
                    return;
                }
                if(handleIndex-- > 0){
                    for(int i = handleIndex; i >= 0; i--){
                        entityProviders.get(i).setEntity(request, entity);
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<E>>() {
            @Override
            public void accept(List<E> entity) throws Exception {
                response.onNext(entity, request);
            }
        }, new Consumer<Throwable>()  {
            @Override
            public void accept(Throwable throwable) throws Exception {
                if(throwable instanceof BaseEntityThrowable){
                    response.onError((BaseEntityThrowable)throwable);
                } else {
                    response.onError(new BaseEntityThrowable(throwable.getMessage()));
                }
            }
        });
    }
}
