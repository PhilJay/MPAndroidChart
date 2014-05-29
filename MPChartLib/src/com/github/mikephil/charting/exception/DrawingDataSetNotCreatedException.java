package com.github.mikephil.charting.exception;

public class DrawingDataSetNotCreatedException extends RuntimeException {

	public DrawingDataSetNotCreatedException() {
		super("Have to create a new drawing set first. Call ChartData's createNewDrawingDataSet() method");
	}

}
