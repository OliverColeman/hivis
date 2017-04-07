package hivis.data.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import hivis.common.HV;
import hivis.common.ListSet;
import hivis.data.DataSeries;
import hivis.data.DataSeriesDouble;
import hivis.data.DataSeriesFloat;
import hivis.data.DataSeriesGeneric;
import hivis.data.DataSeriesInteger;
import hivis.data.DataSeriesLong;

/**
 * Tests for {@link SortedSeries}.
 *
 * @author O. J. Coleman
 */
public class TestSortedSeries {
	@DataProvider(name = "data")
	public Object[][] dataListSet() {
		int[][] dataNumeric = new int[][] {
			{},
			{3},
			{3, 45, 21, 9, 3},
			{3, 45, 21, 45, 9, 3, 3}
		};
		String[][] dataStrings = new String[][] {
			{},
			{"d"},
			{"ea", "c", "d", "eb"},
		};
		
		List<DataSeries<?>> outList = new ArrayList<>();
		
		for (int i = 0; i < dataNumeric.length; i++) {
			outList.add(new DataSeriesInteger(dataNumeric[i]));
		}
		for (int i = 0; i < dataNumeric.length; i++) {
			outList.add(new DataSeriesLong(Arrays.stream(dataNumeric[i]).mapToLong(e->e).toArray()));
		}
		for (int i = 0; i < dataNumeric.length; i++) {
			float[] dataFloats = new float[dataNumeric[i].length];
			for (int j = 0; j < dataNumeric[i].length; j++) { dataFloats[j] = dataNumeric[i][j]; }
			outList.add(new DataSeriesFloat(dataFloats));
		}
		for (int i = 0; i < dataNumeric.length; i++) {
			outList.add(new DataSeriesDouble(Arrays.stream(dataNumeric[i]).mapToDouble(e->e).toArray()));
		}
		for (int i = 0; i < dataStrings.length; i++) {
			outList.add(new DataSeriesGeneric<String>(dataStrings[i]));
		}
		
		int len = outList.size();
		Object[][] out = new Object[len * 2][3];
		
		for (int i = 0; i < len; i++) {
			DataSeries<?> ds = outList.get(i);
			
			// Test sorted according to natural ordering.
			out[i][0] = ds;
			out[i][1] = ds.sort();
			if (!(out[i][1] instanceof SortedSeries)) {
				throw new RuntimeException("Trying to test SortedSeries but generated DataSeries sort() method returned unexpected implementation: " + out[i][1].getClass());
			}
			out[i][2] = false;
			
			// Test sorted according to comparator (reverse ordering).
			out[i+len][0] = ds;
			if (ds.isNumeric()) {
				out[i+len][1] = ((DataSeries<Double>) ds).sort(new reverseComparator<Double>());
			}
			else {
				out[i+len][1] = ((DataSeries<String>) ds).sort(new reverseComparator<String>());
				}
			out[i+len][2] = true;
		}
		
		return out;
	}
	
	private class reverseComparator<V extends Comparable<V>> implements Comparator<V> {
		public int compare(V x, V y) {
			return y.compareTo(x);
		}
	}
	
	private void checkEquals(Object[] originalVals, Object[] sortedVals, boolean isReverse) {
		Assert.assertEquals(originalVals.length, sortedVals.length);
		
		for (int i = 0; i < originalVals.length; i++) {
			Object o = originalVals[isReverse ? originalVals.length - i - 1 : i];
			Object s = sortedVals[i];
			Assert.assertEquals(o, s);
		}
	}
	
	@Test(dataProvider = "data")
	public void initialSorting(DataSeries<?> original, SeriesView<?> sorted, boolean isReverse) {
		Assert.assertEquals(original.length(), sorted.length());
		Object[] originalVals = original.asArray();
		Arrays.sort(originalVals);
		Object[] sortedVals = sorted.asArray();
		checkEquals(originalVals, sortedVals, isReverse);
	}
	
	@Test(dataProvider = "data")
	public void updatedSortingResize(DataSeries<?> original, SeriesView<?> sorted, boolean isReverse) {
		int origLen = original.length();
		for (int s = origLen - 1; s >= 0; s--) {
			original.resize(s);
			Object[] originalVals = original.asArray();
			Arrays.sort(originalVals);
			Object[] sortedVals = sorted.asArray();
			checkEquals(originalVals, sortedVals, isReverse);
		}
	}

	@Test(dataProvider = "data")
	public void updatedSortingRemove(DataSeries<?> original, SeriesView<?> sorted, boolean isReverse) {
		while (original.length() > 1) {
			original.remove(1);
			Object[] originalVals = original.asArray();
			Arrays.sort(originalVals);
			Object[] sortedVals = sorted.asArray();
			checkEquals(originalVals, sortedVals, isReverse);
		}
		if (original.length() > 0) {
			original.remove(0);
			Object[] originalVals = original.asArray();
			Arrays.sort(originalVals);
			Object[] sortedVals = sorted.asArray();
			checkEquals(originalVals, sortedVals, isReverse);
		}
	}
	
	@Test(dataProvider = "data")
	public void updatedSortingSet(DataSeries<?> original, SeriesView<?> sorted, boolean isReverse) {
		if (original.length() > 0) {
			original.set(0, original.isNumeric() ? 200 : "o");
			original.set(original.length()/2, original.isNumeric() ? 100 : "a");
			original.set(original.length()-1, original.isNumeric() ? 300 : "z");
			
			Object[] originalVals = original.asArray();
			Arrays.sort(originalVals);
			Object[] sortedVals = sorted.asArray();
			checkEquals(originalVals, sortedVals, isReverse);
		}
	}

	@Test(dataProvider = "data")
	public void updatedSortingAppend(DataSeries<?> original, SeriesView<?> sorted, boolean isReverse) {
		original.append(original.isNumeric() ? 200 : "o");
		original.append(original.isNumeric() ? 100 : "a");
		original.append(original.isNumeric() ? 300 : "z");
		
		Object[] originalVals = original.asArray();
		Arrays.sort(originalVals);
		Object[] sortedVals = sorted.asArray();
		checkEquals(originalVals, sortedVals, isReverse);
	}
}
