package hivis.common;

import java.util.Arrays;

import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

public class TestBMListSet extends TestListSet {
	public ListSet<Double> newListSet(Double... values) {
		return new BMListSet<Double>(Arrays.asList(values));
	}
}
