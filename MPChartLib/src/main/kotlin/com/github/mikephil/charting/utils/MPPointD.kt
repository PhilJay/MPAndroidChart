package com.github.mikephil.charting.utils

/**
 * Mutable point with double coordinates, used where chart values and pixel positions need the
 * extra precision, for example by [Transformer].
 *
 * Instances handed out by [getInstance] come from a shared pool and should go back through
 * [recycleInstance] once no longer needed. Points returned by public chart getters are fresh
 * instances that you may keep or recycle as you like. Recycling the same instance twice throws.
 *
 * @property x the horizontal coordinate.
 * @property y the vertical coordinate.
 */
public class MPPointD(@JvmField public var x: Double, @JvmField public var y: Double) : ObjectPool.Poolable() {

    override fun instantiate(): ObjectPool.Poolable = MPPointD(0.0, 0.0)

    override fun toString(): String = "MPPointD, x: $x, y: $y"

    public companion object {

        private val pool: ObjectPool<MPPointD> = ObjectPool.create(64, MPPointD(0.0, 0.0)).apply {
            replenishPercentage = 0.5f
        }

        /** Takes a point from the pool and sets it to ([x], [y]). */
        public fun getInstance(x: Double, y: Double): MPPointD {
            val result = pool.get()
            result.x = x
            result.y = y
            return result
        }

        /**
         * Returns [instance] to the pool.
         *
         * @throws IllegalArgumentException if [instance] is already in the pool.
         */
        public fun recycleInstance(instance: MPPointD) {
            pool.recycle(instance)
        }

        /**
         * Returns all [instances] to the pool.
         *
         * @throws IllegalArgumentException if one of them is already in the pool.
         */
        public fun recycleInstances(instances: List<MPPointD>) {
            pool.recycle(instances)
        }
    }
}
