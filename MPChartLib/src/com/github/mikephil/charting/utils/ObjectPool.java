package com.github.mikephil.charting.utils;

import java.util.ArrayList;
import java.util.List;

/**
 * An object pool for recycling of object instances implementing Poolable.
 *
 * Created by Tony Patino on 6/20/16.
 */
public class ObjectPool<T extends ObjectPool.Poolable> {

    private static int ids = 0;

    private int poolId;
    private int desiredCapacity;
    private Object[] objects;
    private int objectsPointer;
    private T modelObject;


    public int getPoolId(){
        return poolId;
    }

    public static synchronized ObjectPool create(int withCapacity, Poolable object){
        ObjectPool result = new ObjectPool(withCapacity, object);
        result.poolId = ids;
        ids++;

        return result;
    }

    private ObjectPool(int withCapacity, T object){
        this.desiredCapacity = withCapacity;
        this.objects = new Object[this.desiredCapacity];
        this.objectsPointer = 0;
        this.modelObject = object;
        this.refillPool();
    }

    private void refillPool(){
        for(int i = 0 ; i < desiredCapacity ; i++){
            this.objects[i] = (T)modelObject.instantiate();
        }
        objectsPointer = this.desiredCapacity - 1;
    }

    public synchronized T get(){

        if(this.objectsPointer == -1){
            this.refillPool();
        }

        T result = (T)objects[this.objectsPointer];
        result.currentOwnerId = Poolable.NO_OWNER;
        this.objectsPointer--;

        return result;
    }

    public synchronized void recycle(T object){
        if(object.currentOwnerId != Poolable.NO_OWNER){
            if(object.currentOwnerId == this.poolId){
                throw new IllegalArgumentException("The object passed is already stored in this pool!");
            }else {
                throw new IllegalArgumentException("The object to recycle already belongs to poolId " + object.currentOwnerId + ".  Object cannot belong to two different pool instances simultaneously!");
            }
        }

        this.objectsPointer++;
        if(this.objectsPointer >= objects.length){
            this.resizePool();
        }

        object.currentOwnerId = this.poolId;
        objects[this.objectsPointer] = object;

    }

    public synchronized void recycle(List<T> objects){
        while(objects.size() + this.objectsPointer + 1 > this.desiredCapacity){
            this.resizePool();
        }
        final int objectsListSize = objects.size();

        // Not relying on recycle(T object) because this is more performant.
        for(int i = 0 ; i < objectsListSize ; i++){
            T object = objects.get(i);
            if(object.currentOwnerId != Poolable.NO_OWNER){
                if(object.currentOwnerId == this.poolId){
                    throw new IllegalArgumentException("The object passed is already stored in this pool!");
                }else {
                    throw new IllegalArgumentException("The object to recycle already belongs to poolId " + object.currentOwnerId + ".  Object cannot belong to two different pool instances simultaneously!");
                }
            }
            object.currentOwnerId = this.poolId;
            this.objects[this.objectsPointer + 1 + i] = object;
        }
        this.objectsPointer += objectsListSize;
    }

    private void resizePool() {
        final int oldCapacity = this.desiredCapacity;
        this.desiredCapacity *= 2;
        Object[] temp = new Object[this.desiredCapacity];
        for(int i = 0 ; i < oldCapacity ; i++){
            temp[i] = this.objects[i];
        }
        this.objects = temp;
    }


    public static abstract class Poolable{

        public static int NO_OWNER = -1;
        int currentOwnerId = NO_OWNER;

        abstract Poolable instantiate();

    }
}
