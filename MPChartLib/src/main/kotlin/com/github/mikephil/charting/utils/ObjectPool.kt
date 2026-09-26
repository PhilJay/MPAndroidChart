package com.github.mikephil.charting.utils

/**
 * Pool that hands out and takes back instances of one [Poolable] type to avoid allocations while drawing.
 *
 * Create a pool with [create]. [get] returns an instance and [recycle] takes it back. Each instance
 * remembers which pool holds it, so the pool can tell without searching whether an instance is already
 * stored, and it refuses instances that are stored in this or another pool. An empty pool creates new
 * instances from the model object, and a full pool doubles its capacity. Both operations cost time with
 * large capacities; lower [replenishPercentage] if refilling is a concern. [create], [get] and [recycle] are
 * synchronised; the capacity and count properties are read without a lock.
 */
public class ObjectPool<T : ObjectPool.Poolable> private constructor(
    private var desiredCapacity: Int,
    private val modelObject: T
) {

    /** Number that identifies this pool. Instances stored in the pool carry it as their owner. */
    public var poolId: Int = 0
        private set

    private var objects: Array<Any?>
    private var objectsPointer = 0

    /**
     * Fraction of the capacity that is created when the pool runs empty, between 0 and 1. Default 1.
     * Values outside that range are clamped. With 0 the pool never refills and [get] fails once empty.
     */
    public var replenishPercentage: Float = 1f
        set(value) {
            field = value.coerceIn(0f, 1f)
        }

    init {
        require(desiredCapacity > 0) { "Object Pool must be instantiated with a capacity greater than 0!" }
        objects = arrayOfNulls(desiredCapacity)
        refillPool()
    }

    private fun refillPool(percentage: Float = replenishPercentage) {
        val portionOfCapacity = (desiredCapacity * percentage).toInt().coerceIn(1, desiredCapacity)
        for (i in 0 until portionOfCapacity) {
            objects[i] = modelObject.instantiate()
        }
        objectsPointer = portionOfCapacity - 1
    }

    /**
     * Takes an instance out of the pool. The instance keeps whatever state it had before.
     *
     * Refills the pool first when it is empty and [replenishPercentage] is above 0.
     */
    @Synchronized
    public fun get(): T {
        if (objectsPointer == -1 && replenishPercentage > 0f) {
            refillPool()
        }
        @Suppress("UNCHECKED_CAST")
        val result = objects[objectsPointer] as T
        objects[objectsPointer] = null
        result.currentOwnerId = Poolable.NO_OWNER
        objectsPointer--
        return result
    }

    /**
     * Largest number of instances the pool keeps. Anything handed to [recycle] beyond that is dropped and
     * collected normally, so a caller that recycles more instances than it takes cannot grow the pool forever.
     */
    public var maxCapacity: Int = 4096
        set(value) {
            field = value.coerceAtLeast(1)
        }

    /**
     * Puts [obj] back into the pool. The pool grows when it is full, up to [maxCapacity], and drops the instance
     * once it is.
     *
     * @throws IllegalArgumentException if [obj] is already stored in this pool or in another pool.
     */
    @Synchronized
    public fun recycle(obj: T) {
        checkNotOwned(obj)
        if (objectsPointer + 1 >= maxCapacity) return
        objectsPointer++
        if (objectsPointer >= objects.size) {
            resizePool()
        }
        obj.currentOwnerId = poolId
        objects[objectsPointer] = obj
    }

    /**
     * Puts all [objects] back into the pool. The pool grows when it is full, up to [maxCapacity], and the
     * instances that no longer fit are dropped.
     *
     * @throws IllegalArgumentException if one of them is already stored in this pool or in another pool.
     */
    @Synchronized
    public fun recycle(objects: List<T>) {
        val room = maxCapacity - (objectsPointer + 1)
        if (room <= 0) return
        val taken = objects.take(room)

        while (taken.size + objectsPointer + 1 > desiredCapacity) {
            resizePool()
        }
        for ((i, obj) in taken.withIndex()) {
            checkNotOwned(obj)
            obj.currentOwnerId = poolId
            this.objects[objectsPointer + 1 + i] = obj
        }
        objectsPointer += taken.size
    }

    private fun checkNotOwned(obj: T) {
        if (obj.currentOwnerId == Poolable.NO_OWNER) return
        if (obj.currentOwnerId == poolId) {
            throw IllegalArgumentException("The object passed is already stored in this pool!")
        }
        throw IllegalArgumentException(
            "The object to recycle already belongs to poolId ${obj.currentOwnerId}.  Object cannot belong to two different pool instances simultaneously!"
        )
    }

    private fun resizePool() {
        val oldCapacity = desiredCapacity
        desiredCapacity *= 2
        val temp = arrayOfNulls<Any?>(desiredCapacity)
        for (i in 0 until oldCapacity) {
            temp[i] = objects[i]
        }
        objects = temp
    }

    /** Number of instances the pool can hold before it has to grow. */
    public val poolCapacity: Int
        get() = objects.size

    /** Number of instances currently stored in the pool and available to [get]. */
    public val poolCount: Int
        get() = objectsPointer + 1

    /**
     * Base class for objects that can be stored in an [ObjectPool].
     *
     * Subclasses implement [instantiate] so the pool can create new instances from a model object.
     */
    public abstract class Poolable {

        internal var currentOwnerId = NO_OWNER

        /** Creates a new, blank instance of the same type. */
        public abstract fun instantiate(): Poolable

        public companion object {
            /** Owner id of an instance that is not stored in any pool. */
            public const val NO_OWNER: Int = -1
        }
    }

    public companion object {

        private var ids = 0

        /**
         * Creates a pool with a unique [poolId] that starts filled to [withCapacity].
         *
         * @param withCapacity initial capacity, must be greater than 0.
         * @param obj model object whose [Poolable.instantiate] creates the pooled instances.
         * @throws IllegalArgumentException if [withCapacity] is 0 or negative.
         */
        @Synchronized
        public fun <T : Poolable> create(withCapacity: Int, obj: T): ObjectPool<T> {
            val result = ObjectPool(withCapacity, obj)
            result.poolId = ids
            ids++
            return result
        }
    }
}
