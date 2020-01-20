package com.github.mikephil.charting.highlight;

import android.util.Log;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class Highlights implements Iterable<Highlight> {

    /**
     * The collection of highlights.
     */
    private final Set<Highlight> mSet = new HashSet<>();

    /**
     * Are multiple concurrent highlights enabled for this instance?
     */
    private boolean mMultipleHighlightsEnabled = false;

    /**
     * Construct an empty Highlights
     */
    public Highlights() {  }

    /**
     * Constructs a Highlights from an array of Highlight's.
     * To support legacy code using Highlight[] for value highlights.
     *
     * @param ary
     */
    public Highlights(Highlight[] ary) {
        for (Highlight h : ary) {
            add(h);
        }
    }

    /**
     * Is it empty?
     *
     * @return true if there are no highlights
     */
    public boolean isEmpty() { return mSet.size() == 0; }

    /**
     * Are there any highlights?
     *
     * @return true if there is at least one highlight
     */
    public boolean hasHighlights() {
        return  !isEmpty();
    }

    public int size() {
        return mSet.size();
    }

    /**
     * Are multiple highlights enabled?
     *
     * @return true if multiple highlights enabled
     */
    public boolean isMultipleHighlightsEnabled() {
        return mMultipleHighlightsEnabled;
    }


    /**
     * Sets the multiple highlights enabled flag.
     * @param enabled enabled if true
     */
    public void setMultipleHighlightsEnabled(boolean enabled) {
        mMultipleHighlightsEnabled = enabled;
    }

    /**
     * Add a highlight to the set.
     *
     * @param highlight highlight to be added
     * @return true if added
     */
    public boolean add(@Nullable Highlight highlight) {
        if (highlight == null) return false;
        if (!mSet.isEmpty() && highlight.getType() != mSet.iterator().next().getType()) {
            throw new RuntimeException("attempt to add incompatible type " +
                    highlight.getType().name() + " to " + mSet.iterator().next().getType().name());
        }
        if (!isMultipleHighlightsEnabled())
            mSet.clear();

        return mSet.add(highlight);
    }

    /**
     * Add a Highlights to the set.
     * After adding, mMultipleHighlightsEnabled will be true only if
     * it is true in both this and in highlights.
     *
     * @param highlights highlights to be added
     * @return true if added
     */
    public boolean add(Highlights highlights) {
        if (highlights.isMultipleHighlightsEnabled())
            mMultipleHighlightsEnabled = highlights.isMultipleHighlightsEnabled();
        boolean result = true;
        Iterator<Highlight> itr = highlights.iterator();
        while (itr.hasNext())
            result = result & mSet.add(itr.next());
        return result;
    }

    /**
     * Removes the given highlight if it exists.
     *
     * @param highlight to be removed
     * @return true if highlight existed and was removed
     */
    public boolean remove(Highlight highlight) {
        return mSet.remove(highlight);
    }

    /**
     * Clear the highlights in this set.
     */
    public void clear() { mSet.clear(); }

    /**
     * Returns an iterator.
     * @return iterator
     */
    @NonNull
    @Override
    public Iterator<Highlight> iterator() {
        return mSet.iterator();
    }

    /**
     * Returns highlights as array to support legacy (pre-Highlights) code.
     *
     * @deprecated use Highlights
     * @return array of Highlight
     */
    @Deprecated
    public Highlight[] asArray() {
        return mSet.toArray(new Highlight[0]);
    }
}
