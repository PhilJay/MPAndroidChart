package com.github.mikephil.charting.test

import com.github.mikephil.charting.utils.ObjectPool
import com.github.mikephil.charting.utils.ObjectPool.Poolable
import org.junit.Assert
import org.junit.Test

class ObjectPoolTest {
    internal class TestPoolable private constructor(var foo: Int, var bar: Int) : Poolable() {
        override fun instantiate(): Poolable {
            return TestPoolable(0, 0)
        }

        companion object {
            private val pool: ObjectPool<TestPoolable> = ObjectPool.create(4, TestPoolable(0, 0)) as ObjectPool<TestPoolable>

            fun getInstance(foo: Int, bar: Int): TestPoolable {
                val result = pool.get()
                result.foo = foo
                result.bar = bar
                return result
            }

            fun recycleInstance(instance: TestPoolable) {
                pool.recycle(instance)
            }

            fun recycleInstances(instances: List<TestPoolable>?) {
                pool.recycle(instances)
            }

            fun getPool(): ObjectPool<*> {
                return pool
            }
        }
    }

    @Test
    fun testObjectPool() {
        var poolCapacity = TestPoolable.getPool().poolCapacity
        var poolCount = TestPoolable.getPool().poolCount
        val testPoolables = ArrayList<TestPoolable>()

        Assert.assertEquals(4, poolCapacity)
        Assert.assertEquals(4, poolCount)

        var testPoolable = TestPoolable.getInstance(6, 7)
        Assert.assertEquals(6, testPoolable.foo)
        Assert.assertEquals(7, testPoolable.bar)

        poolCapacity = TestPoolable.getPool().poolCapacity
        poolCount = TestPoolable.getPool().poolCount

        Assert.assertEquals(4, poolCapacity)
        Assert.assertEquals(3, poolCount)

        TestPoolable.recycleInstance(testPoolable)

        poolCapacity = TestPoolable.getPool().poolCapacity
        poolCount = TestPoolable.getPool().poolCount
        Assert.assertEquals(4, poolCapacity)
        Assert.assertEquals(4, poolCount)


        testPoolable = TestPoolable.getInstance(20, 30)
        Assert.assertEquals(20, testPoolable.foo)
        Assert.assertEquals(30, testPoolable.bar)

        TestPoolable.recycleInstance(testPoolable)

        poolCapacity = TestPoolable.getPool().poolCapacity
        poolCount = TestPoolable.getPool().poolCount
        Assert.assertEquals(4, poolCapacity)
        Assert.assertEquals(4, poolCount)

        testPoolables.add(TestPoolable.getInstance(12, 24))
        testPoolables.add(TestPoolable.getInstance(1, 2))
        testPoolables.add(TestPoolable.getInstance(3, 5))
        testPoolables.add(TestPoolable.getInstance(6, 8))

        poolCapacity = TestPoolable.getPool().poolCapacity
        poolCount = TestPoolable.getPool().poolCount
        Assert.assertEquals(4, poolCapacity)
        Assert.assertEquals(0, poolCount)


        TestPoolable.recycleInstances(testPoolables)
        poolCapacity = TestPoolable.getPool().poolCapacity
        poolCount = TestPoolable.getPool().poolCount
        Assert.assertEquals(4, poolCapacity)
        Assert.assertEquals(4, poolCount)

        testPoolables.clear()


        testPoolables.add(TestPoolable.getInstance(12, 24))
        testPoolables.add(TestPoolable.getInstance(1, 2))
        testPoolables.add(TestPoolable.getInstance(3, 5))
        testPoolables.add(TestPoolable.getInstance(6, 8))
        testPoolables.add(TestPoolable.getInstance(8, 9))
        Assert.assertEquals(12, testPoolables[0].foo)
        Assert.assertEquals(24, testPoolables[0].bar)
        Assert.assertEquals(1, testPoolables[1].foo)
        Assert.assertEquals(2, testPoolables[1].bar)
        Assert.assertEquals(3, testPoolables[2].foo)
        Assert.assertEquals(5, testPoolables[2].bar)
        Assert.assertEquals(6, testPoolables[3].foo)
        Assert.assertEquals(8, testPoolables[3].bar)
        Assert.assertEquals(8, testPoolables[4].foo)
        Assert.assertEquals(9, testPoolables[4].bar)


        poolCapacity = TestPoolable.getPool().poolCapacity
        poolCount = TestPoolable.getPool().poolCount
        Assert.assertEquals(4, poolCapacity)
        Assert.assertEquals(3, poolCount)

        TestPoolable.recycleInstances(testPoolables)
        poolCapacity = TestPoolable.getPool().poolCapacity
        poolCount = TestPoolable.getPool().poolCount
        Assert.assertEquals(8, poolCapacity)
        Assert.assertEquals(8, poolCount)

        testPoolables.clear()


        testPoolables.add(TestPoolable.getInstance(0, 0))
        testPoolables.add(TestPoolable.getInstance(6, 8))
        testPoolables.add(TestPoolable.getInstance(1, 2))
        testPoolables.add(TestPoolable.getInstance(3, 5))
        testPoolables.add(TestPoolable.getInstance(8, 9))
        testPoolables.add(TestPoolable.getInstance(12, 24))
        testPoolables.add(TestPoolable.getInstance(12, 24))
        testPoolables.add(TestPoolable.getInstance(12, 24))
        testPoolables.add(TestPoolable.getInstance(6, 8))
        testPoolables.add(TestPoolable.getInstance(6, 8))
        Assert.assertEquals(0, testPoolables[0].foo)
        Assert.assertEquals(0, testPoolables[0].bar)
        Assert.assertEquals(6, testPoolables[1].foo)
        Assert.assertEquals(8, testPoolables[1].bar)
        Assert.assertEquals(1, testPoolables[2].foo)
        Assert.assertEquals(2, testPoolables[2].bar)
        Assert.assertEquals(3, testPoolables[3].foo)
        Assert.assertEquals(5, testPoolables[3].bar)
        Assert.assertEquals(8, testPoolables[4].foo)
        Assert.assertEquals(9, testPoolables[4].bar)
        Assert.assertEquals(12, testPoolables[5].foo)
        Assert.assertEquals(24, testPoolables[5].bar)
        Assert.assertEquals(12, testPoolables[6].foo)
        Assert.assertEquals(24, testPoolables[6].bar)
        Assert.assertEquals(12, testPoolables[7].foo)
        Assert.assertEquals(24, testPoolables[7].bar)
        Assert.assertEquals(6, testPoolables[8].foo)
        Assert.assertEquals(8, testPoolables[8].bar)
        Assert.assertEquals(6, testPoolables[9].foo)
        Assert.assertEquals(8, testPoolables[9].bar)

        for (p in testPoolables) {
            TestPoolable.recycleInstance(p)
        }

        poolCapacity = TestPoolable.getPool().poolCapacity
        poolCount = TestPoolable.getPool().poolCount
        Assert.assertEquals(16, poolCapacity)
        Assert.assertEquals(16, poolCount)

        testPoolable = TestPoolable.getInstance(9001, 9001)
        Assert.assertEquals(9001, testPoolable.foo)
        Assert.assertEquals(9001, testPoolable.bar)

        poolCapacity = TestPoolable.getPool().poolCapacity
        poolCount = TestPoolable.getPool().poolCount
        Assert.assertEquals(16, poolCapacity)
        Assert.assertEquals(15, poolCount)


        TestPoolable.recycleInstance(testPoolable)

        poolCapacity = TestPoolable.getPool().poolCapacity
        poolCount = TestPoolable.getPool().poolCount
        Assert.assertEquals(16, poolCapacity)
        Assert.assertEquals(16, poolCount)

        var e: Exception? = null
        try {
            // expect an exception.
            TestPoolable.recycleInstance(testPoolable)
        } catch (ex: Exception) {
            e = ex
        } finally {
            Assert.assertEquals(e!!.message, true, true)
        }

        testPoolables.clear()

        TestPoolable.getPool().replenishPercentage = 0.5f
        var i = 16
        while (i > 0) {
            testPoolables.add(TestPoolable.getInstance(0, 0))
            i--
        }

        poolCapacity = TestPoolable.getPool().poolCapacity
        poolCount = TestPoolable.getPool().poolCount
        Assert.assertEquals(16, poolCapacity)
        Assert.assertEquals(0, poolCount)

        testPoolables.add(TestPoolable.getInstance(0, 0))

        poolCapacity = TestPoolable.getPool().poolCapacity
        poolCount = TestPoolable.getPool().poolCount
        Assert.assertEquals(16, poolCapacity)
        Assert.assertEquals(7, poolCount)
    }
}
