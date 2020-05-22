package com.github.mikephil.charting.test;

import com.github.mikephil.charting.utils.ObjectPool;

import org.junit.Assert;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by otheruser on 6/28/16.
 */
public class ObjectPoolTest {

    static class TestPoolable extends ObjectPool.Poolable{

        private static ObjectPool<TestPoolable> pool;

        static {
            pool = ObjectPool.create(4, new TestPoolable(0,0));
        }

        public int foo = 0;
        public int bar = 0;

        protected ObjectPool.Poolable instantiate(){
            return new TestPoolable(0,0);
        }

        private TestPoolable(int foo, int bar){
            this.foo = foo;
            this.bar = bar;
        }

        public static TestPoolable getInstance(int foo, int bar){
            TestPoolable result = pool.get();
            result.foo = foo;
            result.bar = bar;
            return result;
        }

        public static void recycleInstance(TestPoolable instance){
            pool.recycle(instance);
        }

        public static void recycleInstances(List<TestPoolable> instances){
            pool.recycle(instances);
        }

        public static ObjectPool getPool(){
            return pool;
        }

    }

    @Test
    public void testObjectPool(){

        int poolCapacity = TestPoolable.getPool().getPoolCapacity();
        int poolCount = TestPoolable.getPool().getPoolCount();
        TestPoolable testPoolable;
        ArrayList<TestPoolable> testPoolables = new ArrayList<>();

        Assert.assertEquals(4, poolCapacity);
        Assert.assertEquals(4, poolCount);

        testPoolable = TestPoolable.getInstance(6,7);
        Assert.assertEquals(6, testPoolable.foo);
        Assert.assertEquals(7, testPoolable.bar);

        poolCapacity = TestPoolable.getPool().getPoolCapacity();
        poolCount = TestPoolable.getPool().getPoolCount();

        Assert.assertEquals(4, poolCapacity);
        Assert.assertEquals(3, poolCount);

        TestPoolable.recycleInstance(testPoolable);

        poolCapacity = TestPoolable.getPool().getPoolCapacity();
        poolCount = TestPoolable.getPool().getPoolCount();
        Assert.assertEquals(4, poolCapacity);
        Assert.assertEquals(4, poolCount);


        testPoolable = TestPoolable.getInstance(20,30);
        Assert.assertEquals(20, testPoolable.foo);
        Assert.assertEquals(30, testPoolable.bar);

        TestPoolable.recycleInstance(testPoolable);

        poolCapacity = TestPoolable.getPool().getPoolCapacity();
        poolCount = TestPoolable.getPool().getPoolCount();
        Assert.assertEquals(4, poolCapacity);
        Assert.assertEquals(4, poolCount);

        testPoolables.add(TestPoolable.getInstance(12,24));
        testPoolables.add(TestPoolable.getInstance(1,2));
        testPoolables.add(TestPoolable.getInstance(3,5));
        testPoolables.add(TestPoolable.getInstance(6,8));

        poolCapacity = TestPoolable.getPool().getPoolCapacity();
        poolCount = TestPoolable.getPool().getPoolCount();
        Assert.assertEquals(4, poolCapacity);
        Assert.assertEquals(0, poolCount);


        TestPoolable.recycleInstances(testPoolables);
        poolCapacity = TestPoolable.getPool().getPoolCapacity();
        poolCount = TestPoolable.getPool().getPoolCount();
        Assert.assertEquals(4, poolCapacity);
        Assert.assertEquals(4, poolCount);

        testPoolables.clear();


        testPoolables.add(TestPoolable.getInstance(12,24));
        testPoolables.add(TestPoolable.getInstance(1,2));
        testPoolables.add(TestPoolable.getInstance(3,5));
        testPoolables.add(TestPoolable.getInstance(6,8));
        testPoolables.add(TestPoolable.getInstance(8,9));
        Assert.assertEquals(12, testPoolables.get(0).foo);
        Assert.assertEquals(24, testPoolables.get(0).bar);
        Assert.assertEquals(1, testPoolables.get(1).foo);
        Assert.assertEquals(2, testPoolables.get(1).bar);
        Assert.assertEquals(3, testPoolables.get(2).foo);
        Assert.assertEquals(5, testPoolables.get(2).bar);
        Assert.assertEquals(6, testPoolables.get(3).foo);
        Assert.assertEquals(8, testPoolables.get(3).bar);
        Assert.assertEquals(8, testPoolables.get(4).foo);
        Assert.assertEquals(9, testPoolables.get(4).bar);


        poolCapacity = TestPoolable.getPool().getPoolCapacity();
        poolCount = TestPoolable.getPool().getPoolCount();
        Assert.assertEquals(4, poolCapacity);
        Assert.assertEquals(3, poolCount);

        TestPoolable.recycleInstances(testPoolables);
        poolCapacity = TestPoolable.getPool().getPoolCapacity();
        poolCount = TestPoolable.getPool().getPoolCount();
        Assert.assertEquals(8, poolCapacity);
        Assert.assertEquals(8, poolCount);

        testPoolables.clear();


        testPoolables.add(TestPoolable.getInstance(0,0));
        testPoolables.add(TestPoolable.getInstance(6,8));
        testPoolables.add(TestPoolable.getInstance(1,2));
        testPoolables.add(TestPoolable.getInstance(3,5));
        testPoolables.add(TestPoolable.getInstance(8,9));
        testPoolables.add(TestPoolable.getInstance(12,24));
        testPoolables.add(TestPoolable.getInstance(12,24));
        testPoolables.add(TestPoolable.getInstance(12,24));
        testPoolables.add(TestPoolable.getInstance(6,8));
        testPoolables.add(TestPoolable.getInstance(6,8));
        Assert.assertEquals(0, testPoolables.get(0).foo);
        Assert.assertEquals(0, testPoolables.get(0).bar);
        Assert.assertEquals(6, testPoolables.get(1).foo);
        Assert.assertEquals(8, testPoolables.get(1).bar);
        Assert.assertEquals(1, testPoolables.get(2).foo);
        Assert.assertEquals(2, testPoolables.get(2).bar);
        Assert.assertEquals(3, testPoolables.get(3).foo);
        Assert.assertEquals(5, testPoolables.get(3).bar);
        Assert.assertEquals(8, testPoolables.get(4).foo);
        Assert.assertEquals(9, testPoolables.get(4).bar);
        Assert.assertEquals(12, testPoolables.get(5).foo);
        Assert.assertEquals(24, testPoolables.get(5).bar);
        Assert.assertEquals(12, testPoolables.get(6).foo);
        Assert.assertEquals(24, testPoolables.get(6).bar);
        Assert.assertEquals(12, testPoolables.get(7).foo);
        Assert.assertEquals(24, testPoolables.get(7).bar);
        Assert.assertEquals(6, testPoolables.get(8).foo);
        Assert.assertEquals(8, testPoolables.get(8).bar);
        Assert.assertEquals(6, testPoolables.get(9).foo);
        Assert.assertEquals(8, testPoolables.get(9).bar);

        for(TestPoolable p : testPoolables){
            TestPoolable.recycleInstance(p);
        }

        poolCapacity = TestPoolable.getPool().getPoolCapacity();
        poolCount = TestPoolable.getPool().getPoolCount();
        Assert.assertEquals(16, poolCapacity);
        Assert.assertEquals(16, poolCount);

        testPoolable = TestPoolable.getInstance(9001,9001);
        Assert.assertEquals(9001, testPoolable.foo);
        Assert.assertEquals(9001, testPoolable.bar);

        poolCapacity = TestPoolable.getPool().getPoolCapacity();
        poolCount = TestPoolable.getPool().getPoolCount();
        Assert.assertEquals(16, poolCapacity);
        Assert.assertEquals(15, poolCount);


        TestPoolable.recycleInstance(testPoolable);

        poolCapacity = TestPoolable.getPool().getPoolCapacity();
        poolCount = TestPoolable.getPool().getPoolCount();
        Assert.assertEquals(16, poolCapacity);
        Assert.assertEquals(16, poolCount);

        Exception e = null;
        try{
            // expect an exception.
            TestPoolable.recycleInstance(testPoolable);
        }catch (Exception ex){
            e = ex;
        }finally{
            Assert.assertEquals(e.getMessage(), true, e != null);
        }

        testPoolables.clear();

        TestPoolable.getPool().setReplenishPercentage(0.5f);
        int i = 16;
        while(i > 0){
            testPoolables.add(TestPoolable.getInstance(0,0));
            i--;
        }

        poolCapacity = TestPoolable.getPool().getPoolCapacity();
        poolCount = TestPoolable.getPool().getPoolCount();
        Assert.assertEquals(16, poolCapacity);
        Assert.assertEquals(0, poolCount);

        testPoolables.add(TestPoolable.getInstance(0,0));

        poolCapacity = TestPoolable.getPool().getPoolCapacity();
        poolCount = TestPoolable.getPool().getPoolCount();
        Assert.assertEquals(16, poolCapacity);
        Assert.assertEquals(7, poolCount);


    }

}
