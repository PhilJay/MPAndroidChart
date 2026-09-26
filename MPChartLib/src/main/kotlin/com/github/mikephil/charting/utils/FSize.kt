package com.github.mikephil.charting.utils

/**
 * Mutable width and height pair, usually a size in pixels such as measured text bounds.
 *
 * Instances handed out by [getInstance] come from a shared pool and should go back through
 * [recycleInstance] once no longer needed. Recycling the same instance twice throws.
 * Two sizes are equal when width and height match.
 *
 * @property width the horizontal extent.
 * @property height the vertical extent.
 */
public class FSize(@JvmField public var width: Float = 0f, @JvmField public var height: Float = 0f) : ObjectPool.Poolable() {

    override fun instantiate(): ObjectPool.Poolable = FSize(0f, 0f)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is FSize) return false
        return width == other.width && height == other.height
    }

    override fun hashCode(): Int = width.toBits() xor height.toBits()

    override fun toString(): String = "${width}x$height"

    public companion object {

        private val pool: ObjectPool<FSize> = ObjectPool.create(256, FSize(0f, 0f)).apply {
            replenishPercentage = 0.5f
        }

        /** Takes a size from the pool and sets it to [width] by [height]. */
        public fun getInstance(width: Float, height: Float): FSize {
            val result = pool.get()
            result.width = width
            result.height = height
            return result
        }

        /**
         * Returns [instance] to the pool.
         *
         * @throws IllegalArgumentException if [instance] is already in the pool.
         */
        public fun recycleInstance(instance: FSize) {
            pool.recycle(instance)
        }

        /**
         * Returns all [instances] to the pool.
         *
         * @throws IllegalArgumentException if one of them is already in the pool.
         */
        public fun recycleInstances(instances: List<FSize>) {
            pool.recycle(instances)
        }
    }
}
