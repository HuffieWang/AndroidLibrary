package com.musheng.android.java;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MSThreadPool {

    private static ExecutorService cachedThreadPool = Executors.newCachedThreadPool();

    public static ExecutorService cached(){
        return cachedThreadPool;
    }
}
