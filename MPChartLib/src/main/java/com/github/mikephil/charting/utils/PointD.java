
package com.github.mikephil.charting.utils;

import java.util.List;

/**
 * Point encapsulating two double values.
 *
 * @author Philipp Jahoda
 */
public class PointD extends ObjectPool.Poolable {

    private static ObjectPool<PointD> pool;

    static {
        pool = ObjectPool.create(64, new PointD(0,0));
    }

    public static PointD getInstance(double x, double y){
        PointD result = pool.get();
        result.x = x;
        result.y = y;
        return result;
    }

    public static void recycleInstance(PointD instance){
        pool.recycle(instance);
    }

    public static void recycleInstances(List<PointD> instances){
        pool.recycle(instances);
    }

    public double x;
    public double y;

    protected ObjectPool.Poolable instantiate(){
        return new PointD(0,0);
    }

    private PointD(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * returns a string representation of the object
     */
    public String toString() {
        return "PointD, x: " + x + ", y: " + y;
    }
}