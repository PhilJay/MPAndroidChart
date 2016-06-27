
package com.github.mikephil.charting.utils;

import java.util.List;

/**
 * Immutable class for describing width and height dimensions in some arbitrary
 * unit. Replacement for the android.Util.SizeF which is available only on API >= 21.
 */
public final class FSize extends ObjectPool.Poolable {

    private float width;
    private float height;

    private static ObjectPool<FSize> pool;

    static {
        pool = ObjectPool.create(500, new FSize(0,0));
    }


    ObjectPool.Poolable instantiate(){
        return new FSize(0,0);
    }

    public static FSize getInstance(final float width, final float height){
        FSize result = pool.get();
        result.width = width;
        result.height = height;
        return result;
    }

    public static void recycleInstance(FSize instance){
        pool.recycle(instance);
    }

    public static void recycleInstances(List<FSize> instances){
        pool.recycle(instances);
    }

    private FSize(final float width, final float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == null) {
            return false;
        }
        if (this == obj) {
            return true;
        }
        if (obj instanceof FSize) {
            final FSize other = (FSize) obj;
            return getWidth() == other.getWidth() && getHeight() == other.getHeight();
        }
        return false;
    }

    @Override
    public String toString() {
        return getWidth() + "x" + getHeight();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Float.floatToIntBits(getWidth()) ^ Float.floatToIntBits(getHeight());
    }


    // 'Immutability' offered via get/set methods due to this object being poolable.
    // Sacrifice performance gain of direct field access for offering only a getter.
    // NOTE : Holding onto an instance that has been pooled will mean that the underlying
    // value may change under your nose if the instance is dispensed by a second pool.get()
    // call.

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }
}
