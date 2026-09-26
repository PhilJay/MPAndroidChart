package com.github.mikephil.charting.utils

/**
 * Mutable point with float coordinates, usually a pixel position.
 *
 * Instances handed out by [getInstance] come from a shared pool and should go back through
 * [recycleInstance] once no longer needed. Points returned by public chart getters are fresh
 * instances that you may keep or recycle as you like. Recycling the same instance twice throws.
 *
 * @property x the horizontal coordinate.
 * @property y the vertical coordinate.
 */
public class MPPointF(@JvmField public var x: Float = 0f, @JvmField public var y: Float = 0f) : ObjectPool.Poolable() {

    override fun instantiate(): ObjectPool.Poolable = MPPointF(0f, 0f)

    public companion object {

        private val pool: ObjectPool<MPPointF> = ObjectPool.create(32, MPPointF(0f, 0f)).apply {
            replenishPercentage = 0.5f
        }

        /** Takes a point from the pool and sets it to ([x], [y]). */
        public fun getInstance(x: Float, y: Float): MPPointF {
            val result = pool.get()
            result.x = x
            result.y = y
            return result
        }

        /** Takes a point from the pool. Its coordinates are whatever it held before. */
        public fun getInstance(): MPPointF = pool.get()

        /** Takes a point from the pool and sets it to the coordinates of [copy]. */
        public fun getInstance(copy: MPPointF): MPPointF {
            val result = pool.get()
            result.x = copy.x
            result.y = copy.y
            return result
        }

        /**
         * Returns [instance] to the pool.
         *
         * @throws IllegalArgumentException if [instance] is already in the pool.
         */
        public fun recycleInstance(instance: MPPointF) {
            pool.recycle(instance)
        }

        /**
         * Returns all [instances] to the pool.
         *
         * @throws IllegalArgumentException if one of them is already in the pool.
         */
        public fun recycleInstances(instances: List<MPPointF>) {
            pool.recycle(instances)
        }
    }
}
