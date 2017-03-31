package hivis.common;

import java.util.Arrays;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestAListSet extends TestListSet {
	public ListSet<Double> newListSet(Double... values) {
		return new AListSet<Double>(Arrays.asList(values));
	}
}
