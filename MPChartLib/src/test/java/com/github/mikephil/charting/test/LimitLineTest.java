package com.github.mikephil.charting.test;

import com.github.mikephil.charting.components.LimitLine;

import org.junit.Test;
import static junit.framework.Assert.assertEquals;

public class LimitLineTest {
    @Test
    public void LimitLineFSMTest() {
        // start
        // construct
        LimitLine limitLine = new LimitLine(0);
        assertEquals(LimitLine.LimitLabelPosition.RIGHT_TOP, limitLine.getLabelPosition());

        // right top -> right top
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        assertEquals(LimitLine.LimitLabelPosition.RIGHT_TOP, limitLine.getLabelPosition());

        // right top <-> left top
        // right top -> left top
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        assertEquals(LimitLine.LimitLabelPosition.LEFT_TOP, limitLine.getLabelPosition());

        // left top -> right top
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        assertEquals(LimitLine.LimitLabelPosition.RIGHT_TOP, limitLine.getLabelPosition());

        // right top <-> left bottom
        // right top -> left bottom
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        assertEquals(LimitLine.LimitLabelPosition.LEFT_BOTTOM, limitLine.getLabelPosition());

        // left bottom -> right top
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        assertEquals(LimitLine.LimitLabelPosition.RIGHT_TOP, limitLine.getLabelPosition());

        // right top <-> right bottom
        // right top -> right bottom
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        assertEquals(LimitLine.LimitLabelPosition.RIGHT_BOTTOM, limitLine.getLabelPosition());

        // right bottom -> right top
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_TOP);
        assertEquals(LimitLine.LimitLabelPosition.RIGHT_TOP, limitLine.getLabelPosition());

        // start from right bottom
        // right top -> right bottom
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        assertEquals(LimitLine.LimitLabelPosition.RIGHT_BOTTOM, limitLine.getLabelPosition());

        // right bottom -> right bottom
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        assertEquals(LimitLine.LimitLabelPosition.RIGHT_BOTTOM, limitLine.getLabelPosition());

        // right bottom <-> left top
        // right bottom -> left top
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        assertEquals(LimitLine.LimitLabelPosition.LEFT_TOP, limitLine.getLabelPosition());

        // left top -> right bottom
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        assertEquals(LimitLine.LimitLabelPosition.RIGHT_BOTTOM, limitLine.getLabelPosition());

        // right bottom <-> left bottom
        // right bottom -> left bottom
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        assertEquals(LimitLine.LimitLabelPosition.LEFT_BOTTOM, limitLine.getLabelPosition());

        // left bottom -> right bottom
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
        assertEquals(LimitLine.LimitLabelPosition.RIGHT_BOTTOM, limitLine.getLabelPosition());

        // start from left top
        // right bottom -> left top
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        assertEquals(LimitLine.LimitLabelPosition.LEFT_TOP, limitLine.getLabelPosition());

        // left top -> left top
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        assertEquals(LimitLine.LimitLabelPosition.LEFT_TOP, limitLine.getLabelPosition());

        // left top <-> left bottom
        // left top -> left bottom
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        assertEquals(LimitLine.LimitLabelPosition.LEFT_BOTTOM, limitLine.getLabelPosition());

        // left bottom -> left top
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_TOP);
        assertEquals(LimitLine.LimitLabelPosition.LEFT_TOP, limitLine.getLabelPosition());


        // start from left bottom
        // left top -> left bottom
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        assertEquals(LimitLine.LimitLabelPosition.LEFT_BOTTOM, limitLine.getLabelPosition());

        // left bottom -> left bottom
        limitLine.setLabelPosition(LimitLine.LimitLabelPosition.LEFT_BOTTOM);
        assertEquals(LimitLine.LimitLabelPosition.LEFT_BOTTOM, limitLine.getLabelPosition());

    }
}
