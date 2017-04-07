package hivis.data.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Set;

import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import hivis.common.HV;
import hivis.common.ListSet;
import hivis.data.DataEvent;
import hivis.data.DataListener;
import hivis.data.DataMap;
import hivis.data.DataSeries;
import hivis.data.DataSeriesDouble;
import hivis.data.DataSeriesFloat;
import hivis.data.DataSeriesGeneric;
import hivis.data.DataSeriesInteger;
import hivis.data.DataSeriesLong;

/**
 * Tests for {@link GroupedSeries}.
 * 
 * TODO Test events produced correctly.
 *
 * @author O. J. Coleman
 */
public class TestGroupedSeries {
	@DataProvider(name = "data")
	public Object[][] dataListSet() {
		int[][] dataNumeric = new int[][] {
			{},
			{3},
			{3, 44, 21, 10, 3},
			{44, 21, 44, 3, 9, 3, 3}
		};
		String[][] dataStrings = new String[][] {
			{},
			{"d"},
			{"ea", "e", "d", "ea", "eb", "d"},
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
		
		Function<Number, String> numberFunc = new Function<Number, String>() {
			public String apply(Number n) {
				return n.intValue() % 2 == 0 ? "even" : "odd";
			}
		};
		Function<String, Integer> stringFunc = new Function<String, Integer>() {
			public Integer apply(String s) {
				return s.length();
			}
		};
		
		for (int i = 0; i < len; i++) {
			DataSeries<?> ds = outList.get(i);
			
			// Test grouped according to natural ordering.
			DataSeries<?> dsCopy = ds.getNewSeries(ds);
			out[i][0] = dsCopy;
			out[i][1] = dsCopy.group();
			if (!(out[i][1] instanceof GroupedSeries)) {
				throw new RuntimeException("Trying to test GroupedSeries but generated DataSeries group() method returned unexpected implementation: " + out[i][1].getClass());
			}
			
			// Test grouped according to key function.
			dsCopy = ds.getNewSeries(ds);
			out[i+len][0] = dsCopy;
			if (ds.isNumeric()) {
				// even or odd for numeric.
				out[i+len][1] = ((DataSeries<Number>) dsCopy).group(numberFunc);
				out[i+len][2] = numberFunc;
			}
			else {
				// by length if string.
				out[i+len][1] = ((DataSeries<String>) dsCopy).group(stringFunc);
				out[i+len][2] = stringFunc;
			}
		}
		
		return out;
	}
	
	
	private void checkGroups(DataSeries original, DataMap groups, Function customFunc) {
		Map<Object, List<Object>> map = new HashMap<>();
		
		for (Object value : original) {
			Object key = customFunc != null ? customFunc.apply(value) : value;
			List<Object> g = map.containsKey(key) ? map.get(key) : new ArrayList<>();
			if (!map.containsKey(key)) map.put(key, g);
			g.add(value);
		}
		
		// Correct keys.
		Assert.assertEqualsNoOrder(groups.keys().asList().toArray(), map.keySet().toArray());
		
		// Correct group contents.
		for (Object key : map.keySet()) {
			Assert.assertEquals(((DataSeries) groups.get(key)).asList(), map.get(key));
		}
	}
	
	@Test(dataProvider = "data")
	public void initialGrouping(DataSeries<?> original, DataMap<?, DataSeries<?>> groups, Function customFunc) {
		checkGroups(original, groups, customFunc);
	}
	
	@Test(dataProvider = "data")
	public void updatedGroupingResize(DataSeries<?> original, DataMap<?, DataSeries<?>> groups, Function customFunc) {
		int origLen = original.length();
		
		for (int s = origLen - 1; s >= 0; s--) {
			original.resize(s);
			checkGroups(original, groups, customFunc);
		}
	}
	
	@Test(dataProvider = "data")
	public void updatedGroupingRemove(DataSeries<?> original, DataMap<?, DataSeries<?>> groups, Function customFunc) {
		while (original.length() > 1) {
			original.remove(1);
			checkGroups(original, groups, customFunc);
		}
		if (original.length() > 0) {
			original.remove(0);
			checkGroups(original, groups, customFunc);
		}
	}
	
	@Test(dataProvider = "data")
	public void updatedGroupingSet(DataSeries<?> original, DataMap<?, DataSeries<?>> groups, Function customFunc) {
		if (original.length() > 0) {
			original.set(0, original.isNumeric() ? 200 : "o");
			original.set(original.length()/2, original.isNumeric() ? 100 : "a");
			original.set(original.length()-1, original.isNumeric() ? 300 : "z");
			
			checkGroups(original, groups, customFunc);
		}
	}

	@Test(dataProvider = "data")
	public void updatedGroupingAppend(DataSeries<?> original, DataMap<?, DataSeries<?>> groups, Function customFunc) {
		original.append(original.isNumeric() ? 200 : "o");
		original.append(original.isNumeric() ? 100 : "a");
		original.append(original.isNumeric() ? 300 : "z");
		
		checkGroups(original, groups, customFunc);
	}
}
