package com.musheng.android.java;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

/**
 * Author      : MuSheng
 * CreateDate  : 2019/12/2 11:37
 * Description :
 */
public class MSQueue<T> {

    private BlockingQueue<T> queue;

    private int capacity;

    public MSQueue(int capacity) {
        this.capacity = capacity;
        queue = new ArrayBlockingQueue<>(capacity);
    }

    public boolean put(T t){
        if(!queue.offer(t)){
            queue.remove();
            return queue.offer(t);
        }
        return true;
    }

    public void remove(T t){
        queue.remove(t);
    }

    public void clear(){
        queue.clear();
    }

    public List<T> toList(){
        return new ArrayList<>(queue);
    }
}
