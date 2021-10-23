package com.github.albertus82.eqbulletin.gui;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class MapCanvasTest {

	@Test
	void testGetZoomNearestValues() {
		Assertions.assertArrayEquals(new int[] { 10, 12 }, MapCanvas.getZoomNearestValues(Integer.MIN_VALUE));
		Assertions.assertArrayEquals(new int[] { 10, 12 }, MapCanvas.getZoomNearestValues(0));
		Assertions.assertArrayEquals(new int[] { 10, 12 }, MapCanvas.getZoomNearestValues(1));
		Assertions.assertArrayEquals(new int[] { 10, 12 }, MapCanvas.getZoomNearestValues(9));
		Assertions.assertArrayEquals(new int[] { 10, 12 }, MapCanvas.getZoomNearestValues(10));
		Assertions.assertArrayEquals(new int[] { 10, 12 }, MapCanvas.getZoomNearestValues(11));
		Assertions.assertArrayEquals(new int[] { 10, 12 }, MapCanvas.getZoomNearestValues(11.9f));
		Assertions.assertArrayEquals(new int[] { 10, 15 }, MapCanvas.getZoomNearestValues(12));
		Assertions.assertArrayEquals(new int[] { 10, 15 }, MapCanvas.getZoomNearestValues(12f));
		Assertions.assertArrayEquals(new int[] { 12, 15 }, MapCanvas.getZoomNearestValues(12.1f));

		Assertions.assertArrayEquals(new int[] { 80, 100 }, MapCanvas.getZoomNearestValues(99));
		Assertions.assertArrayEquals(new int[] { 80, 100 }, MapCanvas.getZoomNearestValues(99.9f));
		Assertions.assertArrayEquals(new int[] { 80, 120 }, MapCanvas.getZoomNearestValues(100));
		Assertions.assertArrayEquals(new int[] { 80, 120 }, MapCanvas.getZoomNearestValues(100f));
		Assertions.assertArrayEquals(new int[] { 100, 120 }, MapCanvas.getZoomNearestValues(100.1f));
		Assertions.assertArrayEquals(new int[] { 100, 120 }, MapCanvas.getZoomNearestValues(101));

		Assertions.assertArrayEquals(new int[] { 300, 500 }, MapCanvas.getZoomNearestValues(400));
		Assertions.assertArrayEquals(new int[] { 400, 500 }, MapCanvas.getZoomNearestValues(400.1f));
		Assertions.assertArrayEquals(new int[] { 400, 500 }, MapCanvas.getZoomNearestValues(499.9f));
		Assertions.assertArrayEquals(new int[] { 400, 500 }, MapCanvas.getZoomNearestValues(500));
		Assertions.assertArrayEquals(new int[] { 400, 500 }, MapCanvas.getZoomNearestValues(500.1f));
		Assertions.assertArrayEquals(new int[] { 400, 500 }, MapCanvas.getZoomNearestValues(Integer.MAX_VALUE));
	}
}
