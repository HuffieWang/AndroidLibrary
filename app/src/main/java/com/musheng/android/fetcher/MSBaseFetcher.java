package com.musheng.android.fetcher;

import java.lang.reflect.Type;
import java.util.ArrayList;
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
public abstract class MSBaseFetcher<R extends MSFetcherRequest, E> {

    private LinkedHashMap<Type, MSFetcherEntityProvider<R, E>> providerMap = new LinkedHashMap<>();

    private List<MSFetcherResponse<R,E>> responseList = new ArrayList<>();

    private boolean isCancel;

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:23
     * Description : 默认添加缓存、网络、默认，优先缓存、其次网络，最后默认。    
     */
    public MSBaseFetcher() {
        providerMap.put(CacheProvider.class, new CacheProvider());
        providerMap.put(NetworkProvider.class, new NetworkProvider());
        providerMap.put(DefaultProvider.class, new DefaultProvider());
    }
    
    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:22
     * Description : 添加自定义的数据提供器   
     */
    public MSBaseFetcher<R, E> addProvider(Type type, MSFetcherEntityProvider<R, E> provider){
        providerMap.put(type, provider);
        return this;
    }
    
    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:22
     * Description : 移除指定种类的数据提供器   
     */
    public MSBaseFetcher<R, E> removeProvider(Type type){
        providerMap.remove(type);
        return this;
    }

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:26
     * Description : 设置数据提供器的调用顺序，未被提及的会被移除
     * @param types : 期望的调用顺序
     */
    public MSBaseFetcher<R, E> sortProvider(Type... types) {
        LinkedHashMap<Type, MSFetcherEntityProvider<R, E>> temp = new LinkedHashMap<>();
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
    public MSBaseFetcher<R, E> setProviderForceTrigger(Type type, boolean isForceTrigger){
        MSFetcherEntityProvider<R, E> provider = providerMap.get(type);
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
    public E execute(R request) throws MSFetcherThrowable {
        
        Collection<MSFetcherEntityProvider<R, E>> entityProviders = providerMap.values();
        if(entityProviders.isEmpty()) {
            throw new MSFetcherThrowable( "EntityHandler not found");
        }

        int index = 0;
        int handleIndex = -1;
        E entity = null;
        for(MSFetcherEntityProvider<R, E> provider : entityProviders){
            
            //当已经获取到数据时，跳过不是强制触发的提供器
            if(entity != null && !provider.isForceTrigger()){
                continue;
            }
            try {
                entity = provider.getEntity(request);
            } catch (Exception e) {
               throw new MSFetcherThrowable(e.getMessage());
            }

            if(entity != null){
                handleIndex = index;
            }
            index++;
        }
        if (entity == null) {
            throw new MSFetcherThrowable( "Entity is null");
        }
        //从最终获取到数据的提供器向前遍历，调用设置数据方法
        index = 0;
        for(MSFetcherEntityProvider<R, E> provider : entityProviders){
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
    public Disposable enqueue(final R request, final MSFetcherResponse<R, E> response){
        return enqueue(request, response, null);
    }


    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/19 0019 下午 5:52
     * Description : 异步调用支持的数据提供器，结果回调在主线程
     * @param request : 请求参数，根据需要自行定义
     * @param response : 返回结果，成功和失败都回调在主线程
     * @param indicator : 指示器，可以控制请求是否被取消
     */
    public Disposable enqueue(final R request, final MSFetcherResponse<R, E> response, final MSFetcherIndicator indicator){

        isCancel = false;

        for(MSFetcherResponse<R,E> resp : responseList){
            resp.onStart();
        }
        response.onStart();

        return Observable.create(new ObservableOnSubscribe<E>() {
            @Override
            public void subscribe(ObservableEmitter<E> emitter) throws Exception {
                Collection<MSFetcherEntityProvider<R, E>> entityProviders = providerMap.values();
                if(entityProviders.isEmpty()) {
                    emitter.onError(new MSFetcherThrowable( "未知错误"));
                    return;
                }
                
                int index = 0;
                int handleIndex = -1;
                E entity = null;
                for(MSFetcherEntityProvider<R, E> provider : entityProviders){
                    
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
                    emitter.onError(new MSFetcherThrowable( "请求失败"));
                    return;
                }
                
                //从最终获取到数据的提供器向前遍历，调用设置数据方法
                index = 0;
                for(MSFetcherEntityProvider<R, E> provider : entityProviders){
                    if(index++ < handleIndex){
                        provider.setEntity(request, entity);
                    }
                }
            }
        }).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Consumer<E>() {
            @Override
            public void accept(E entity) throws Exception {
                if(!isCancel && (indicator == null || !indicator.isCancel())){
                    for(MSFetcherResponse<R,E> resp : responseList){
                        resp.onNext(entity, request);
                    }
                    response.onNext(entity, request);
                }
            }
        }, new Consumer<Throwable>() {

            @Override
            public void accept(Throwable throwable) throws Exception {
                if(!isCancel && (indicator == null || !indicator.isCancel())){
                    if(throwable instanceof MSFetcherThrowable){
                        response.onError((MSFetcherThrowable)throwable);
                    } else {
                        response.onError(new MSFetcherThrowable(throwable.getMessage()));
                    }
                    for(MSFetcherResponse<R,E> resp : responseList){
                        if(throwable instanceof MSFetcherThrowable){
                            resp.onError((MSFetcherThrowable)throwable);
                        } else {
                            resp.onError(new MSFetcherThrowable(throwable.getMessage()));
                        }
                    }
                }
            }
        });
    }

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/19 0019 下午 5:52
     * Description : 添加异步请求回调，以满足多个地方需要回调通知的情况
     * @param response : 返回结果，成功和失败都回调在主线程
     */
    public void addEntityResponse(MSFetcherResponse<R, E> response){
        responseList.add(response);
    }

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/19 0019 下午 5:52
     * Description : 移除额外添加的异步请求回调
     * @param response : 返回结果，成功和失败都回调在主线程
     */
    public void removeEntityResponse(MSFetcherResponse<R, E> response){
        responseList.remove(response);
    }

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/19 0019 下午 5:52
     * Description : 移除所有额外添加的异步请求回调
     */
    public void removeAllEntityResponse(){
        responseList.clear();
    }

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/19 0019 下午 5:52
     * Description : 取消异步请求，并没有真正取消，只是关掉了回调
     */
    public void cancel(){
        isCancel = true;
        for(MSFetcherResponse<R,E> resp : responseList){
            resp.onCancel();
        }
        responseList.clear();
    }

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:39
     * Description : 获取网络数据
     */
    public abstract E fetchNetwork(R request) throws Exception;

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:43
     * Description : 获取持久化数据    
     */
    public abstract E fetchCache(R request) throws Exception;

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:43
     * Description : 将数据持久化   
     */
    public abstract void writeCache(R request, E entity) throws Exception;

    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:39
     * Description : 默认数据
     */
    public abstract E fetchDefault(R request) throws Exception;
    
    
    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/19 0019 下午 5:54
     * Description : 默认数据提供器，推荐扩展时继承，可省去写isForceTrigger()等实现和泛型<R,E>的代码      
     */
    public class DefaultProvider implements MSFetcherEntityProvider<R, E> {

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
        public E getEntity(R request) throws Exception{
            return fetchDefault(request);
        }

        @Override
        public void setEntity(R request, E entity) {
        }
    }
    
    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:19
     * Description : 网络数据提供器，从服务器获取数据    
     */
    public class NetworkProvider extends DefaultProvider{
        @Override
        public E getEntity(R request) throws Exception{
            return fetchNetwork(request);
        }
    }
    
    /**
     * Author      : MuSheng
     * CreateDate  : 2019/7/31 0031 下午 3:19
     * Description : 缓存数据提供器，从数据库、文件等持久化组件获取数据      
     */
    public class CacheProvider extends DefaultProvider {

        @Override
        public E getEntity(R request) throws Exception{
            return fetchCache(request);
        }

        @Override
        public void setEntity(R request, E entity) {
            try {
                writeCache(request, entity);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
