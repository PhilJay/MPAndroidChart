package com.github.mikephil.charting.formatter;

import java.text.Format;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Tony Patino on 6/29/16.
 *
 *      COST : Frequently the V type, and the K type will often be passed as primitives (float / int / double)
 *             This will incur an auto-boxing penalty, and an instantiation on each call.
 *
 *      BENEFIT : Formatting of Strings is one of the costliest operations remaining, and it is larger than the boxing penalty.
 *                Eliminating redundant formats helps more than boxing hurts.
 *
 *      Possibly want to make some explicit primitive enabled caches, though they can be ugly.
 *
 */
public class FormattedStringCache {

    protected Format mFormat;

    public Format getFormat(){
        return mFormat;
    }

    public FormattedStringCache(Format format){
        this.mFormat = format;
    }


    /**
     * Cache a Formatted String, derived from formatting value V in the Format passed to the constructor.
     * Use the object K as a way to quickly look up the string in the cache.
     * If value of V has changed since the last time the cache was accessed, re-format the string.
     *
     * NOTE: This version relies on auto-boxing of primitive values.  As a result, it has worse memory performance
     * than the Prim versions.
     *
     * @param <K>
     * @param <V>
     */
    public static class Generic<K,V> extends FormattedStringCache{

        private HashMap<K, String> mCachedStringsHashMap = new HashMap<>();
        private HashMap<K, V> mCachedValuesHashMap = new HashMap<>();

        public Generic(Format format){
            super(format);
        }

        public String getFormattedValue(V value, K key){

            // If we can't find the value at all, create an entry for it, and format the string.
            if(!mCachedValuesHashMap.containsKey(key)){
                mCachedStringsHashMap.put(key, mFormat.format(value));
                mCachedValuesHashMap.put(key, value);
            }

            // If the old value and the new one don't match, format the string and store it, because the string's value will be different now.
            if(!value.equals(mCachedValuesHashMap.get(key))){
                mCachedStringsHashMap.put(key, mFormat.format(value));
                mCachedValuesHashMap.put(key, value);
            }

            String result = mCachedStringsHashMap.get(key);

            return result;
        }
    }

    /**
     * Cache a Formatted String, derived from formatting float value in the Format passed to the constructor.
     * Use the integer as a way to quickly look up the string in the cache.
     * If value of V has changed since the last time the cache was accessed, re-format the string.
     *
     */
    public static class PrimIntFloat extends FormattedStringCache{

        public PrimIntFloat(Format format){
            super(format);
        }

        private ArrayList<Float> cachedValues = new ArrayList<>();
        private ArrayList<String> cachedStrings = new ArrayList<>();

        public String getFormattedValue(float value, int key) {

            boolean hasValueAtdataSetIndex = true;
            if(cachedValues.size() <= key){
                int p = key;
                while(p >= 0){
                    if(p == 0){
                        cachedValues.add(value);
                        cachedStrings.add("");
                    }else{
                        cachedValues.add(Float.NaN);
                        cachedStrings.add("");
                    }
                    p--;
                }
                hasValueAtdataSetIndex = false;
            }

            if(hasValueAtdataSetIndex) {
                Float cachedValue = cachedValues.get(key);
                hasValueAtdataSetIndex = !(cachedValue == null || cachedValue != value);
            }

            if(!hasValueAtdataSetIndex){
                cachedValues.set(key, value);
                cachedStrings.set(key, mFormat.format(value));
            }

            return cachedStrings.get(key);
        }

    }

    /**
     * Cache a Formatted String, derived from formatting float value in the Format passed to the constructor.
     */
    public static class PrimFloat extends FormattedStringCache{

        public PrimFloat(Format format){
            super(format);
        }


        private ArrayList<Float> cachedValues = new ArrayList<>();
        private ArrayList<String> cachedStrings = new ArrayList<>();

        public String getFormattedValue(float value) {

            boolean alreadyHasValue = false;
            int vCount =  cachedValues.size();
            int sIndex = -1;
            for(int i = 0 ; i < vCount ; i++){
                if(cachedValues.get(i) == value){
                    sIndex = i;
                    alreadyHasValue = true;
                    break;
                }
            }
            if(!alreadyHasValue) {
                cachedValues.add(value);
                cachedStrings.add(mFormat.format(value));
                sIndex = cachedValues.size() - 1;
            }

            return cachedStrings.get(sIndex);
        }

    }

    /**
     * Cache a Formatted String, derived from formatting double value in the Format passed to the constructor.
     */
    public static class PrimDouble extends FormattedStringCache{

        public PrimDouble(Format format){
            super(format);
        }


        private ArrayList<Double> cachedValues = new ArrayList<>();
        private ArrayList<String> cachedStrings = new ArrayList<>();

        public String getFormattedValue(double value) {

            boolean alreadyHasValue = false;
            int vCount =  cachedValues.size();
            int sIndex = -1;
            for(int i = 0 ; i < vCount ; i++){
                if(cachedValues.get(i) == value){
                    sIndex = i;
                    alreadyHasValue = true;
                    break;
                }
            }
            if(!alreadyHasValue) {
                cachedValues.add(value);
                cachedStrings.add(mFormat.format(value));
                sIndex = cachedValues.size() - 1;
            }

            return cachedStrings.get(sIndex);
        }

    }



}
