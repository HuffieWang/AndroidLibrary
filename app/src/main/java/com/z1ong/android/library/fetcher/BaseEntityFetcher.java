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
public abstract class BaseEntityFetcher<R extends IEntityRequest, E> {

    private LinkedHashMap<Type, IEntityProvider<R, E>> providerMap = new LinkedHashMap<>();

    /**
     * Author      : FJ
     * CreateDate  : 2019/7/31 0031 下午 3:23
     * Description : 默认添加缓存、网络、默认，优先缓存、其次网络，最后默认。    
     */
    public BaseEntityFetcher() {
        providerMap.put(CacheProvider.class, new CacheProvider());
        providerMap.put(NetworkProvider.class, new NetworkProvider());
        providerMap.put(DefaultProvider.class, new DefaultProvider());
    }
    
    /**
     * Author      : FJ
     * CreateDate  : 2019/7/31 0031 下午 3:22
     * Description : 添加自定义的数据提供器   
     */
    public BaseEntityFetcher<R, E> addProvider(Type type, IEntityProvider<R, E> provider){
        providerMap.put(type, provider);
        return this;
    }
    
    /**
     * Author      : FJ
     * CreateDate  : 2019/7/31 0031 下午 3:22
     * Description : 移除指定种类的数据提供器   
     */
    public BaseEntityFetcher<R, E> removeProvider(Type type){
        providerMap.remove(type);
        return this;
    }

    /**
     * Author      : FJ
     * CreateDate  : 2019/7/31 0031 下午 3:26
     * Description : 设置数据提供器的调用顺序，未被提及的会被移除
     * @param types : 期望的调用顺序
     */
    public BaseEntityFetcher<R, E> sortProvider(Type... types) {
        LinkedHashMap<Type, IEntityProvider<R, E>> temp = new LinkedHashMap<>();
        if(types != null && types.length > 0){
            for (Type type : types) {
                if (providerMap.containsKey(type)) {
                    temp.put(type, providerMap.get(type));
                }
            }
        }
        providerMap = temp;
        return this;
    }
    
    /**
     * Author      : FJ
     * CreateDate  : 2019/7/31 0031 下午 3:32
     * Description :    
     */
    public BaseEntityFetcher<R, E> setProviderForceTrigger(Type type, boolean isForceTrigger){
        IEntityProvider<R, E> provider = providerMap.get(type);
        if(provider != null){
            provider.setForceTrigger(isForceTrigger);
        }
        return this;
    }
    
    /**
     * Author      : FJ
     * CreateDate  : 2019/7/31 0031 下午 4:05
     * Description : 同步调用支持的数据提供器   
     * @param request : 请求参数，根据需要自行定义
     */
    public List<E> execute(R request) throws BaseEntityThrowable{
        
        Collection<IEntityProvider<R, E>> entityProviders = providerMap.values();
        if(entityProviders.isEmpty()) {
            throw new BaseEntityThrowable( "EntityHandler not found");
        }

        int index = 0;
        int handleIndex = -1;
        List<E> entity = null;
        for(IEntityProvider<R, E> provider : entityProviders){
            
            //当已经获取到数据时，跳过不是强制触发的提供器
            if(entity != null && !provider.isForceTrigger()){
                continue;
            }
            entity = provider.getEntity(request);
            
            if(entity != null){
                handleIndex = index;
            }
            index++;
        }
        if (entity == null) {
            throw new BaseEntityThrowable( "Entity is null");
        }
        //从最终获取到数据的提供器向前遍历，调用设置数据方法
        index = 0;
        for(IEntityProvider<R, E> provider : entityProviders){
            if(index++ < handleIndex){
                provider.setEntity(request, entity);
            }
        }
        return entity;
    }
    
    /**
     * Author      : FJ
     * CreateDate  : 2019/7/19 0019 下午 5:52
     * Description : 异步调用支持的数据提供器，结果回调在主线程
     * @param request : 请求参数，根据需要自行定义
     * @param response : 返回结果，成功和失败都回调在主线程
     */
    public Disposable enqueue(final R request, final IEntityResponse<R, E> response){

        return Observable.create(new ObservableOnSubscribe<List<E>>() {
            @Override
            public void subscribe(ObservableEmitter<List<E>> emitter) throws Exception {

                Collection<IEntityProvider<R, E>> entityProviders = providerMap.values();
                if(entityProviders.isEmpty()) {
                    emitter.onError(new BaseEntityThrowable( "EntityHandler not found"));
                    return;
                }
                
                int index = 0;
                int handleIndex = -1;
                List<E> entity = null;
                for(IEntityProvider<R, E> provider : entityProviders){
                    
                    //当已经获取到数据时，跳过不是强制触发的提供器
                    if(entity != null && !provider.isForceTrigger()){
                        continue;
                    }
                    entity = provider.getEntity(request);
                    if(entity != null){
                        handleIndex = index;
                        emitter.onNext(entity);
                    }
                    index++;
                }
                if (entity == null) {
                    emitter.onError(new BaseEntityThrowable( "Entity is null"));
                    return;
                }
                
                //从最终获取到数据的提供器向前遍历，调用设置数据方法
                index = 0;
                for(IEntityProvider<R, E> provider : entityProviders){
                    if(index++ < handleIndex){
                        provider.setEntity(request, entity);
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<List<E>>() {
            @Override
            public void accept(List<E> entity) throws Exception {
                response.onNext(entity, request);
            }
        }, new Consumer<Throwable>() {
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

    /**
     * Author      : FJ
     * CreateDate  : 2019/7/31 0031 下午 3:39
     * Description : 获取网络数据
     */
    public abstract List<E> fetchNetwork(R request);

    /**
     * Author      : FJ
     * CreateDate  : 2019/7/31 0031 下午 3:43
     * Description : 获取持久化数据    
     */
    public abstract List<E> fetchCache(R request);

    /**
     * Author      : FJ
     * CreateDate  : 2019/7/31 0031 下午 3:43
     * Description : 将数据持久化   
     */
    public abstract void writeCache(R request, List<E> entity);

    /**
     * Author      : FJ
     * CreateDate  : 2019/7/31 0031 下午 3:39
     * Description : 默认数据
     */
    public abstract List<E> fetchDefault(R request);
    
    
    /**
     * Author      : FJ
     * CreateDate  : 2019/7/19 0019 下午 5:54
     * Description : 默认数据提供器，推荐扩展时继承，可省去写isForceTrigger()等实现和泛型<R,E>的代码      
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
            return fetchDefault(request);
        }

        @Override
        public void setEntity(R request, List<E> entity) {
        }
    }
    
    /**
     * Author      : FJ
     * CreateDate  : 2019/7/31 0031 下午 3:19
     * Description : 网络数据提供器，从服务器获取数据    
     */
    public class NetworkProvider extends DefaultProvider{
        @Override
        public List<E> getEntity(R request) {
            return fetchNetwork(request);
        }
    }
    
    /**
     * Author      : FJ
     * CreateDate  : 2019/7/31 0031 下午 3:19
     * Description : 缓存数据提供器，从数据库、文件等持久化组件获取数据      
     */
    public class CacheProvider extends DefaultProvider{
        @Override
        public List<E> getEntity(R request) {
            return fetchCache(request);
        }

        @Override
        public void setEntity(R request, List<E> entity) {
            writeCache(request, entity);
        }
    }
}
