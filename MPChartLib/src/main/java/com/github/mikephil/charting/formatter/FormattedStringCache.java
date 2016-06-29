package com.github.mikephil.charting.formatter;

import java.text.Format;
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
public class FormattedStringCache<K, V> {

    private Format mFormat;
    private HashMap<K, String> mCachedStringsHashMap = new HashMap<>();
    private HashMap<K, V> mCachedValuesHashMap = new HashMap<>();

    public Format getFormat(){
        return mFormat;
    }

    public FormattedStringCache(Format format){
        this.mFormat = format;
    }

    public String getFormattedString(V value, K key){

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
