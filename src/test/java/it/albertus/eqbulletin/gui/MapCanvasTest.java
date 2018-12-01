package it.albertus.eqbulletin.gui;

import org.junit.Assert;
import org.junit.Test;

public class MapCanvasTest {

	@Test
	public void testGetZoomNearestValues() {
		Assert.assertArrayEquals(new int[] { 10, 15 }, MapCanvas.getZoomNearestValues(Integer.MIN_VALUE));
		Assert.assertArrayEquals(new int[] { 10, 15 }, MapCanvas.getZoomNearestValues(0));
		Assert.assertArrayEquals(new int[] { 10, 15 }, MapCanvas.getZoomNearestValues(1));
		Assert.assertArrayEquals(new int[] { 10, 15 }, MapCanvas.getZoomNearestValues(9));
		Assert.assertArrayEquals(new int[] { 10, 15 }, MapCanvas.getZoomNearestValues(10));
		Assert.assertArrayEquals(new int[] { 10, 15 }, MapCanvas.getZoomNearestValues(11));
		Assert.assertArrayEquals(new int[] { 10, 15 }, MapCanvas.getZoomNearestValues(14.9f));
		Assert.assertArrayEquals(new int[] { 10, 20 }, MapCanvas.getZoomNearestValues(15));
		Assert.assertArrayEquals(new int[] { 10, 20 }, MapCanvas.getZoomNearestValues(15f));
		Assert.assertArrayEquals(new int[] { 15, 20 }, MapCanvas.getZoomNearestValues(15.1f));

		Assert.assertArrayEquals(new int[] { 80, 100 }, MapCanvas.getZoomNearestValues(99));
		Assert.assertArrayEquals(new int[] { 80, 100 }, MapCanvas.getZoomNearestValues(99.9f));
		Assert.assertArrayEquals(new int[] { 80, 120 }, MapCanvas.getZoomNearestValues(100));
		Assert.assertArrayEquals(new int[] { 80, 120 }, MapCanvas.getZoomNearestValues(100f));
		Assert.assertArrayEquals(new int[] { 100, 120 }, MapCanvas.getZoomNearestValues(100.1f));
		Assert.assertArrayEquals(new int[] { 100, 120 }, MapCanvas.getZoomNearestValues(101));

		Assert.assertArrayEquals(new int[] { 300, 500 }, MapCanvas.getZoomNearestValues(400));
		Assert.assertArrayEquals(new int[] { 400, 500 }, MapCanvas.getZoomNearestValues(400.1f));
		Assert.assertArrayEquals(new int[] { 400, 500 }, MapCanvas.getZoomNearestValues(499.9f));
		Assert.assertArrayEquals(new int[] { 400, 500 }, MapCanvas.getZoomNearestValues(500));
		Assert.assertArrayEquals(new int[] { 400, 500 }, MapCanvas.getZoomNearestValues(500.1f));
		Assert.assertArrayEquals(new int[] { 400, 500 }, MapCanvas.getZoomNearestValues(Integer.MAX_VALUE));
	}
}
