package com.github.mikephil.charting.test

import com.github.mikephil.charting.utils.ObjectPool
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertNotNull
import org.junit.Test

class ObjectPoolTest {

    class TestPoolable private constructor(var foo: Int, var bar: Int) : ObjectPool.Poolable() {

        override fun instantiate(): ObjectPool.Poolable = TestPoolable(0, 0)

        companion object {
            val pool: ObjectPool<TestPoolable> = ObjectPool.create(4, TestPoolable(0, 0))

            fun getInstance(foo: Int, bar: Int): TestPoolable {
                val result = pool.get()
                result.foo = foo
                result.bar = bar
                return result
            }

            fun recycleInstance(instance: TestPoolable) = pool.recycle(instance)

            fun recycleInstances(instances: List<TestPoolable>) = pool.recycle(instances)
        }
    }

    private fun assertPool(capacity: Int, count: Int) {
        assertEquals(capacity, TestPoolable.pool.poolCapacity)
        assertEquals(count, TestPoolable.pool.poolCount)
    }

    @Test
    fun poolGrowsRecyclesAndRefills() {
        assertPool(4, 4)

        var testPoolable = TestPoolable.getInstance(6, 7)
        assertEquals(6, testPoolable.foo)
        assertEquals(7, testPoolable.bar)
        assertPool(4, 3)

        TestPoolable.recycleInstance(testPoolable)
        assertPool(4, 4)

        testPoolable = TestPoolable.getInstance(20, 30)
        assertEquals(20, testPoolable.foo)
        assertEquals(30, testPoolable.bar)
        TestPoolable.recycleInstance(testPoolable)
        assertPool(4, 4)

        val testPoolables = mutableListOf<TestPoolable>()
        testPoolables.add(TestPoolable.getInstance(12, 24))
        testPoolables.add(TestPoolable.getInstance(1, 2))
        testPoolables.add(TestPoolable.getInstance(3, 5))
        testPoolables.add(TestPoolable.getInstance(6, 8))
        assertPool(4, 0)

        TestPoolable.recycleInstances(testPoolables)
        assertPool(4, 4)
        testPoolables.clear()

        testPoolables.add(TestPoolable.getInstance(12, 24))
        testPoolables.add(TestPoolable.getInstance(1, 2))
        testPoolables.add(TestPoolable.getInstance(3, 5))
        testPoolables.add(TestPoolable.getInstance(6, 8))
        testPoolables.add(TestPoolable.getInstance(8, 9))
        assertEquals(12, testPoolables[0].foo)
        assertEquals(24, testPoolables[0].bar)
        assertEquals(1, testPoolables[1].foo)
        assertEquals(2, testPoolables[1].bar)
        assertEquals(3, testPoolables[2].foo)
        assertEquals(5, testPoolables[2].bar)
        assertEquals(6, testPoolables[3].foo)
        assertEquals(8, testPoolables[3].bar)
        assertEquals(8, testPoolables[4].foo)
        assertEquals(9, testPoolables[4].bar)
        assertPool(4, 3)

        TestPoolable.recycleInstances(testPoolables)
        assertPool(8, 8)
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
        assertEquals(0, testPoolables[0].foo)
        assertEquals(6, testPoolables[1].foo)
        assertEquals(1, testPoolables[2].foo)
        assertEquals(3, testPoolables[3].foo)
        assertEquals(8, testPoolables[4].foo)
        assertEquals(12, testPoolables[5].foo)
        assertEquals(12, testPoolables[6].foo)
        assertEquals(12, testPoolables[7].foo)
        assertEquals(6, testPoolables[8].foo)
        assertEquals(6, testPoolables[9].foo)

        for (p in testPoolables) TestPoolable.recycleInstance(p)
        assertPool(16, 16)

        testPoolable = TestPoolable.getInstance(9001, 9001)
        assertEquals(9001, testPoolable.foo)
        assertEquals(9001, testPoolable.bar)
        assertPool(16, 15)

        TestPoolable.recycleInstance(testPoolable)
        assertPool(16, 16)

        var thrown: Exception? = null
        try {
            TestPoolable.recycleInstance(testPoolable)
        } catch (ex: Exception) {
            thrown = ex
        }
        assertNotNull(thrown)

        testPoolables.clear()
        TestPoolable.pool.replenishPercentage = 0.5f
        repeat(16) { testPoolables.add(TestPoolable.getInstance(0, 0)) }
        assertPool(16, 0)

        testPoolables.add(TestPoolable.getInstance(0, 0))
        assertPool(16, 7)
    }

    private class CappedPoolable(var value: Int) : ObjectPool.Poolable() {
        override fun instantiate(): ObjectPool.Poolable = CappedPoolable(0)
    }

    @Test
    fun poolStopsGrowingAtItsMaximum() {
        val pool = ObjectPool.create(4, CappedPoolable(0))
        pool.maxCapacity = 8

        // Recycling instances the pool never handed out is how a caller could grow it without limit.
        repeat(40) { pool.recycle(CappedPoolable(it)) }

        assertEquals(8, pool.poolCount)
        assertTrue(pool.poolCapacity <= 16)
    }
}
