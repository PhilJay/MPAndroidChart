package com.github.mikephil.charting.data;

import java.util.ArrayList;

import com.github.mikephil.charting.exception.DrawingDataSetNotCreatedException;
import com.github.mikephil.charting.interfaces.OnDrawListener;

public class DrawingContext {

	/** holds a DataSet that can be manipulated on the go, to allow users to draw into the chart */
	private LineDataSet mCurrentDrawingDataSet;

	private int mLastDrawnDataSetIndex = 0;

	private boolean mAutoFinishDrawing = false;

	private ArrayList<Entry> mCurrentDrawingEntries;
	private Entry mMovingEntry;

	private OnDrawListener mListener;

	/**
	 * Call this method to create a new drawing DataSet
	 * 
	 * @param type
	 *            the type of the new DataSet
	 */
	public void createNewDrawingDataSet(LineData chartData) {
		if (mCurrentDrawingDataSet != null && mCurrentDrawingEntries != null) {
			// if an old one exist, finish the other one first
			finishNewDrawingEntry(chartData);
		}

		mCurrentDrawingEntries = new ArrayList<Entry>();
		this.mCurrentDrawingDataSet = new LineDataSet(mCurrentDrawingEntries, "DS " + mLastDrawnDataSetIndex);
		
		ArrayList<LineDataSet> dataSets = (ArrayList<LineDataSet>) chartData.getDataSets();
		dataSets.add(mCurrentDrawingDataSet);
	}

	/**
	 * Add a new entry.
	 * 
	 * @param entry
	 * @return true if entry added, false if an entry on this x index already existed
	 */
	public boolean addNewDrawingEntry(Entry entry, ChartData data) {
		if (mCurrentDrawingDataSet != null && mCurrentDrawingEntries != null) {
			if (mCurrentDrawingEntries.size() > 0
					&& mCurrentDrawingEntries.get(mCurrentDrawingEntries.size() - 1).getXIndex() == entry.getXIndex()) {
				return false;
			}
			if (mCurrentDrawingEntries.size() > 0) {
				Entry prevEntry = mCurrentDrawingEntries.get(mCurrentDrawingEntries.size() - 1);
				fillData(prevEntry, entry);
			}
			// add new entry last to have correct order
			mCurrentDrawingEntries.add(entry);
			data.notifyDataForNewEntry(entry);
			if (mListener != null) {
				mListener.onEntryAdded(entry);
			}

			mCurrentDrawingDataSet.notifyDataSetChanged();
			return true;
		} else {
			// new data set has to be created first
			throw new DrawingDataSetNotCreatedException();
		}
	}

	/**
	 * Call this method to notify the drawing context about the entry being moved
	 * 
	 * @param data
	 */
	public void notifyEntryMoved(ChartData data) {
		data.notifyDataForNewEntry(mMovingEntry);

		if (mListener != null) {
			mListener.onEntryMoved(mMovingEntry);
		}
	}

	private void fillData(Entry prevEntry, Entry newEntry) {
		int skippedIndexes = newEntry.getXIndex() - prevEntry.getXIndex();
		int startIndex = prevEntry.getXIndex();
		if (skippedIndexes == -1 || skippedIndexes == 1) {
			// no index skipped
			return;
		}
		skippedIndexes--; // get real number of skipped indexes
		if (skippedIndexes < 0) {
			// new entry before prev entry
			skippedIndexes *= -1; // make positive
			for (int i = 1; i < skippedIndexes; i++) {
				// we do not need to correct data because same as new entry
				Entry entry = new Entry(newEntry.getVal(), startIndex - i);
				mCurrentDrawingEntries.add(entry);
				if (mListener != null) {
					mListener.onEntryAdded(entry);
				}
			}
		} else {
			// new entry after prev entry
			for (int i = 1; i <= skippedIndexes; i++) {
				Entry entry = new Entry(newEntry.getVal(), startIndex + i);
				mCurrentDrawingEntries.add(entry);
				if (mListener != null) {
					mListener.onEntryAdded(entry);
				}
			}
		}

	}

	/**
	 * Finishes a drawing entry and adds values at the beginning and the end to fill up the line
	 */
	public void finishNewDrawingEntry(LineData data) {
		if (mAutoFinishDrawing && mCurrentDrawingEntries.size() > 0) {
			Entry firstEntry = mCurrentDrawingEntries.get(0);
			int xIndex = 0;
			while (xIndex < firstEntry.getXIndex()) {
				Entry entry = new Entry(firstEntry.getVal(), xIndex);
				mCurrentDrawingEntries.add(xIndex, entry);
				data.notifyDataForNewEntry(entry);
				xIndex++;
			}
			Entry lastEntry = mCurrentDrawingEntries.get(mCurrentDrawingEntries.size() - 1);
			xIndex = lastEntry.getXIndex();
			while (xIndex < data.getXValCount()) {
				Entry entry = new Entry(lastEntry.getVal(), xIndex);
				mCurrentDrawingEntries.add(entry);
				data.notifyDataForNewEntry(entry);
				xIndex++;
			}
		} else if (mCurrentDrawingEntries.size() == 0) {
			// do not save a dataset with no entries
			deleteLastDrawingEntry(data);
			return;
		}
		mLastDrawnDataSetIndex++;
		mCurrentDrawingDataSet.notifyDataSetChanged();
		if (mListener != null) {
			mListener.onDrawFinished(mCurrentDrawingDataSet);
		}
		mCurrentDrawingDataSet = null;
		mCurrentDrawingEntries = null;
	}

	public void deleteLastDrawingEntry(LineData data) {
	    if(data == null) return;
		data.getDataSets().remove(mCurrentDrawingDataSet);
		mCurrentDrawingDataSet = null;
		mCurrentDrawingEntries = null;
	}

	public void init(OnDrawListener mListener, boolean autoFinish) {
		this.mListener = mListener;
		this.mAutoFinishDrawing = autoFinish;
	}

	/**
	 * Sets an entry that can be moved later by calling moveEntry()
	 * 
	 * @param entry
	 */
	public void setMovingEntry(Entry entry) {
		mMovingEntry = entry;
	}

	public Entry getMovingEntry() {
		return mMovingEntry;
	}
}
