
package com.github.mikephil.charting;

/**
 * Class that encapsulates information of a value that has been
 * selected/highlighted and its DataSet index. The SelInfo objects give
 * information about the value at the selected index and the DataSet it belongs
 * to. Needed only for highlighting onTouch().
 * 
 * @author Philipp Jahoda
 */
public class SelInfo {

    public float val;
    public int dataSetIndex;

    public SelInfo(float val, int dataSetIndex) {
        this.val = val;
        this.dataSetIndex = dataSetIndex;
    }
}
