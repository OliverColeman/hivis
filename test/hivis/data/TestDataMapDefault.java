package hivis.data;

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

import hivis.common.ListSet;

/**
 * Tests for {@link GroupedSeries}.
 *
 * @author O. J. Coleman
 */
public class TestDataMapDefault {
	@DataProvider(name = "data")
	public Object[][] dataListSet() {
		Double[][] data = new Double[][] {{}, {1001d}, {null}, {1002d, 1003d, 1004d}, {null, 1003d, 1004d}, {1002d, null, 1004d}, {1002d, 1003d, null}};
		Object[][] out = new Object[data.length][2];
		for (int i = 0; i < data.length; i++) {
			out[i][0] = data[i];
			//out[i][1] = newListSet(data[i]);
		}
		return out;
	}
	
	
	@Test(dataProvider = "data")
	public void initialGrouping(Double[] raw, List<Double> ls) {
	}
	
	@Test(dataProvider = "data")
	public void updatedGroupingClear(Double[] raw, List<Double> ls) {
	}

	@Test(dataProvider = "data")
	public void updatedGroupingRemove(Double[] raw, List<Double> ls) {
	}

	@Test(dataProvider = "data")
	public void updatedGroupingAdd(Double[] raw, List<Double> ls) {
	}
}
