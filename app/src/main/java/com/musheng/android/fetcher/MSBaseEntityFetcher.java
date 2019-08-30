package com.musheng.android.fetcher;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/07/10 17:16
 * Description : 链式数据获取器，可为Entity自定义多种数据提供器（网络、数据库、缓存）
 */
public abstract class MSBaseEntityFetcher<R extends MSEntityRequest, E> {

    private LinkedHashMap<Type, MSEntityProvider<R, E>> providerMap = new LinkedHashMap<>();

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:23
     * Description : 默认添加缓存、网络、默认，优先缓存、其次网络，最后默认。    
     */
    public MSBaseEntityFetcher() {
        providerMap.put(CacheProvider.class, new CacheProvider());
        providerMap.put(NetworkProvider.class, new NetworkProvider());
        providerMap.put(DefaultProvider.class, new DefaultProvider());
    }
    
    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:22
     * Description : 添加自定义的数据提供器   
     */
    public MSBaseEntityFetcher<R, E> addProvider(Type type, MSEntityProvider<R, E> provider){
        providerMap.put(type, provider);
        return this;
    }
    
    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:22
     * Description : 移除指定种类的数据提供器   
     */
    public MSBaseEntityFetcher<R, E> removeProvider(Type type){
        providerMap.remove(type);
        return this;
    }

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:26
     * Description : 设置数据提供器的调用顺序，未被提及的会被移除
     * @param types : 期望的调用顺序
     */
    public MSBaseEntityFetcher<R, E> sortProvider(Type... types) {
        LinkedHashMap<Type, MSEntityProvider<R, E>> temp = new LinkedHashMap<>();
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
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:32
     * Description :    
     */
    public MSBaseEntityFetcher<R, E> setProviderForceTrigger(Type type, boolean isForceTrigger){
        MSEntityProvider<R, E> provider = providerMap.get(type);
        if(provider != null){
            provider.setForceTrigger(isForceTrigger);
        }
        return this;
    }
    
    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 4:05
     * Description : 同步调用支持的数据提供器   
     * @param request : 请求参数，根据需要自行定义
     */
    public List<E> execute(R request) throws MSEntityThrowable {
        
        Collection<MSEntityProvider<R, E>> entityProviders = providerMap.values();
        if(entityProviders.isEmpty()) {
            throw new MSEntityThrowable( "EntityHandler not found");
        }

        int index = 0;
        int handleIndex = -1;
        List<E> entity = null;
        for(MSEntityProvider<R, E> provider : entityProviders){
            
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
            throw new MSEntityThrowable( "Entity is null");
        }
        //从最终获取到数据的提供器向前遍历，调用设置数据方法
        index = 0;
        for(MSEntityProvider<R, E> provider : entityProviders){
            if(index++ < handleIndex){
                provider.setEntity(request, entity);
            }
        }
        return entity;
    }
    
    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/19 0019 下午 5:52
     * Description : 异步调用支持的数据提供器，结果回调在主线程
     * @param request : 请求参数，根据需要自行定义
     * @param response : 返回结果，成功和失败都回调在主线程
     */
    public Disposable enqueue(final R request, final MSEntityResponse<R, E> response){

        return Observable.create(new ObservableOnSubscribe<List<E>>() {
            @Override
            public void subscribe(ObservableEmitter<List<E>> emitter) throws Exception {

                Collection<MSEntityProvider<R, E>> entityProviders = providerMap.values();
                if(entityProviders.isEmpty()) {
                    emitter.onError(new MSEntityThrowable( "EntityHandler not found"));
                    return;
                }
                
                int index = 0;
                int handleIndex = -1;
                List<E> entity = null;
                for(MSEntityProvider<R, E> provider : entityProviders){
                    
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
                    emitter.onError(new MSEntityThrowable( "Entity is null"));
                    return;
                }
                
                //从最终获取到数据的提供器向前遍历，调用设置数据方法
                index = 0;
                for(MSEntityProvider<R, E> provider : entityProviders){
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
                if(throwable instanceof MSEntityThrowable){
                    response.onError((MSEntityThrowable)throwable);
                } else {
                    response.onError(new MSEntityThrowable(throwable.getMessage()));
                }
            }
        });
    }

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:39
     * Description : 获取网络数据
     */
    public abstract List<E> fetchNetwork(R request) throws Exception;

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:43
     * Description : 获取持久化数据    
     */
    public abstract List<E> fetchCache(R request) throws Exception;

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:43
     * Description : 将数据持久化   
     */
    public abstract void writeCache(R request, List<E> entity) throws Exception;

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:39
     * Description : 默认数据
     */
    public abstract List<E> fetchDefault(R request) throws Exception;
    
    
    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/19 0019 下午 5:54
     * Description : 默认数据提供器，推荐扩展时继承，可省去写isForceTrigger()等实现和泛型<R,E>的代码      
     */
    public class DefaultProvider implements MSEntityProvider<R, E> {

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
            try {
                return fetchDefault(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void setEntity(R request, List<E> entity) {
        }
    }
    
    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:19
     * Description : 网络数据提供器，从服务器获取数据    
     */
    public class NetworkProvider extends DefaultProvider{
        @Override
        public List<E> getEntity(R request) {
            try {
                return fetchNetwork(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
    }
    
    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:19
     * Description : 缓存数据提供器，从数据库、文件等持久化组件获取数据      
     */
    public class CacheProvider extends DefaultProvider{
        @Override
        public List<E> getEntity(R request) {
            try {
                return fetchCache(request);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        public void setEntity(R request, List<E> entity) {
            try {
                writeCache(request, entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}