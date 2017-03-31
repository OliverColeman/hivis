package hivis.common;

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

/**
 * Base class for testing implementations of {@link ListSet}.
 *
 * @author oliver
 */
public abstract class TestListSet {
	/**
	 * Should return an instance of the implementation to test containing the given values.
	 */
	public abstract ListSet<Double> newListSet(Double... values);
	
	
	@DataProvider(name = "listset")
	public Object[][] dataListSet() {
		Double[][] data = new Double[][] {{}, {1001d}, {null}, {1002d, 1003d, 1004d}, {null, 1003d, 1004d}, {1002d, null, 1004d}, {1002d, 1003d, null}};
		Object[][] out = new Object[data.length][2];
		for (int i = 0; i < data.length; i++) {
			out[i][0] = data[i];
			out[i][1] = newListSet(data[i]);
//			
//			int begin = 0;
//			int end = Math.max(0, data[i].length-1);
//			out[i*3+1][0] = Arrays.copyOfRange(data[i], begin, end);
//			out[i*3+1][1] = newListSet(data[i]).subList(begin, end);
//			
//			if (((Double[]) out[i*3+1][0]).length != ((List<?>) out[i*3+1][1]).size()) {
//				throw new RuntimeException();
//			}
//			
//			begin = Math.min(1, data[i].length);
//			end = data[i].length;
//			out[i*3+2][0] = Arrays.copyOfRange(data[i], begin, end);
//			out[i*3+2][1] = newListSet(data[i]).subList(begin, end);
//			
//			if (((Double[]) out[i*3+2][0]).length != ((List<?>) out[i*3+2][1]).size()) {
//				throw new RuntimeException();
//			}
		}
//		System.out.println(Arrays.deepToString(out));
//		System.exit(0);
		return out;
	}
	
	@DataProvider(name = "listset-and-not-contained")
	public Object[][] dataListSetObject() {
		Object[][] dataListSet = dataListSet();
		Object[][] out = new Object[dataListSet.length][3];
		for (int i = 0; i < dataListSet.length; i++) {
			out[i][0] = dataListSet[i][0];
			out[i][1] = dataListSet[i][1];
			out[i][2] = new Double[] {2002d, 3003d, 4004d};
		}
		return out;
	}
	
	
	@Test(dataProvider = "listset")
	public void isEmpty(Double[] raw, List<Double> ls) {
		System.out.println(ls.toString());
		boolean isEmpty = raw.length == 0;
		Assert.assertEquals(isEmpty, ls.isEmpty());
	}
	
	@Test(dataProvider = "listset")
	public void size(Double[] raw, List<Double> ls) {
		Assert.assertEquals(raw.length, ls.size());
	}
	

	@Test(dataProvider = "listset")
	public void clear(Double[] raw, List<Double> ls) {
		ls.clear();
		Assert.assertTrue(ls.isEmpty());
	}
	

	@Test(dataProvider = "listset")
	public void get(Double[] raw, List<Double> ls) {
		for (int i = 0; i < raw.length; i++) {
			Assert.assertSame(raw[i], ls.get(i));
		}
	}
	
	@Test(dataProvider = "listset", expectedExceptions = IndexOutOfBoundsException.class)
	public void getIndexOutOfBoundsNegative(Double[] raw, List<Double> ls) {
		ls.get(-1);
	}
	@Test(dataProvider = "listset", expectedExceptions = IndexOutOfBoundsException.class)
	public void getIndexOutOfBoundsSize(Double[] raw, List<Double> ls) {
		ls.get(raw.length);
	}
	@Test(dataProvider = "listset", expectedExceptions = IndexOutOfBoundsException.class)
	public void getIndexOutOfBoundsSizePlusOne(Double[] raw, List<Double> ls) {
		ls.get(raw.length+1);
	}
	
	
	@Test(dataProvider = "listset")
	public void contains(Double[] raw, List<Double> ls) {
		for (int i = 0; i < raw.length; i++) {
			Assert.assertTrue(ls.contains(raw[i]));
		}
	}
	@Test(dataProvider = "listset-and-not-contained")
	public void containsNot(Double[] raw, List<Double> ls, Double[] notContained) {
		for (int i = 0; i < notContained.length; i++) {
			Assert.assertFalse(ls.contains(notContained[i]));
		}
	}
	

	@Test(dataProvider = "listset")
	public void containsAll(Double[] raw, List<Double> ls) {
		List<Double> rawList = Arrays.asList(raw);
		for (int j = 0; j < raw.length; j++) {
			Assert.assertTrue(ls.containsAll(rawList.subList(0, j+1)));
			Assert.assertTrue(ls.containsAll(rawList.subList(j, raw.length)));
		}
	}

	@Test(dataProvider = "listset-and-not-contained")
	public void containsAllNot(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> ncList = Arrays.asList(notContained);
		for (int j = 0; j < notContained.length; j++) {
			Assert.assertFalse(ls.containsAll(ncList.subList(0, j+1)));
			Assert.assertFalse(ls.containsAll(ncList.subList(j, notContained.length)));
		}
	}

	
	@Test(dataProvider = "listset")
	public void equals(Double[] raw, List<Double> ls) {
		List<Double> rawList = Arrays.asList(raw);
		Assert.assertTrue(ls.equals(rawList));
	}
	
	@Test(dataProvider = "listset")
	public void equalsNotDiffOrder(Double[] raw, List<Double> ls) {
		if (raw.length >= 2) {
			Double raw0 = raw[0];
			raw[0] = raw[1];
			raw[1] = raw0;
			List<Double> rawList = Arrays.asList(raw);
			Assert.assertFalse(ls.equals(rawList));
		}
	}
	
	@Test(dataProvider = "listset-and-not-contained")
	public void equalsNotItemChanged(Double[] raw, List<Double> ls, Double[] notContained) {
		if (raw.length > 0) {
			raw[0] = notContained[0];
			List<Double> rawList = Arrays.asList(raw);
			Assert.assertFalse(ls.equals(rawList));
		}
	}
	
	@Test(dataProvider = "listset")
	public void equalsNotItemRemoved(Double[] raw, List<Double> ls) {
		if (raw.length > 0) {
			List<Double> rawList = Arrays.asList(raw);
			
			List<Double> rawListRemoveFirst = rawList.subList(1, raw.length);
			Assert.assertFalse(ls.equals(rawListRemoveFirst));
			
			List<Double> rawListRemoveLast = rawList.subList(0, raw.length-1);
			Assert.assertFalse(ls.equals(rawListRemoveLast));
		}
	}
	
	@Test(dataProvider = "listset-and-not-contained")
	public void equalsNotItemAdded(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> rawList = new ArrayList<Double>(Arrays.asList(raw));
		rawList.add(notContained[0]);
		Assert.assertFalse(ls.equals(rawList));
		
	}
	
	@Test(dataProvider = "listset")
	public void equalsNotDupItem(Double[] raw, List<Double> ls) {
		if (raw.length > 0) {
			List<Double> rawList = new ArrayList<Double>(Arrays.asList(raw));
			rawList.add(raw[0]);
			Assert.assertFalse(ls.equals(rawList));
		}
	}
	
	
	@Test(dataProvider = "listset")
	public void indexOf(Double[] raw, List<Double> ls) {
		List<Double> rawList = Arrays.asList(raw);
		for (int i = 0; i < raw.length; i++) {
			Assert.assertEquals(ls.indexOf(raw[i]), rawList.indexOf(raw[i]));
		}
	}
	
	@Test(dataProvider = "listset-and-not-contained")
	public void indexOfNotContained(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> rawList = Arrays.asList(raw);
		for (int i = 0; i < notContained.length; i++) {
			Assert.assertEquals(ls.indexOf(notContained[i]), rawList.indexOf(notContained[i]));
		}
	}
	
	
	@Test(dataProvider = "listset")
	public void lastIndexOf(Double[] raw, List<Double> ls) {
		List<Double> rawList = Arrays.asList(raw);
		for (int i = 0; i < raw.length; i++) {
			Assert.assertEquals(ls.lastIndexOf(raw[i]), rawList.lastIndexOf(raw[i]));
		}
	}
	
	@Test(dataProvider = "listset-and-not-contained")
	public void lastIndexOfNotContained(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> rawList = Arrays.asList(raw);
		for (int i = 0; i < notContained.length; i++) {
			Assert.assertEquals(ls.lastIndexOf(notContained[i]), rawList.lastIndexOf(notContained[i]));
		}
	}
	
	
	@Test(dataProvider = "listset-and-not-contained")
	public void add(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> rawList = new ArrayList<Double>(Arrays.asList(raw));
		for (int i = 0; i < notContained.length; i++) {
			ls.add(notContained[i]);
			rawList.add(notContained[i]);
		}
		Assert.assertEquals(ls, rawList);
	}
	
	@Test(dataProvider = "listset-and-not-contained")
	public void addExisting(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> rawList = new ArrayList<Double>(Arrays.asList(raw));
		// Try adding each existing item, none should be added (Set).
		for (int i = 0; i < raw.length; i++) {
			Assert.assertFalse(ls.add(raw[i]));
		}
		Assert.assertEquals(ls, rawList);
	}

	@Test(dataProvider = "listset-and-not-contained")
	public void addAtIndex(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> rawList = new ArrayList<Double>(Arrays.asList(raw));
		
		// Try new items at several positions, all should be added (Set).
		// beginning
		ls.add(0, notContained[0]);
		rawList.add(0, notContained[0]);
		Assert.assertEquals(ls, rawList);
		
		// middle
		ls.add((raw.length + 1) / 2, notContained[1]);
		rawList.add((raw.length + 1) / 2, notContained[1]);
		Assert.assertEquals(ls, rawList);
		
		// end
		ls.add(raw.length + 2, notContained[2]);
		rawList.add(raw.length + 2, notContained[2]);
		Assert.assertEquals(ls, rawList);
	}
	
	@Test(dataProvider = "listset-and-not-contained")
	public void addAtIndexExisting(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> rawList = new ArrayList<Double>(Arrays.asList(raw));
		// Try adding each existing item at every possible position, none should be added (Set).
		for (int i = 0; i < raw.length; i++) {
			for (int j = 0; j < ls.size(); j++){
				try {
					ls.add(j, raw[i]);
					Assert.fail("Added existing element but no Exception thrown.");
				}
				catch (IllegalArgumentException ex) {}
				catch (Exception ex){
					Assert.fail("Adding existing element did not throw an IllegalArgumentException.", ex);
				}
			}
		}
		Assert.assertEquals(ls, rawList);
	}
	
	@Test(dataProvider = "listset-and-not-contained")
	public void addAll(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> rawList = new ArrayList<Double>(Arrays.asList(raw));
		List<Double> ncList = Arrays.asList(notContained);
		
		ls.addAll(ncList);
		rawList.addAll(ncList);
	
		Assert.assertEquals(ls, rawList);
	}
	
	@Test(dataProvider = "listset-and-not-contained")
	public void addAllExisting(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> rawList = new ArrayList<Double>(Arrays.asList(raw));
		
		// Try adding existing items, none should be added (Set).
		Assert.assertFalse(ls.addAll(rawList));
		
		Assert.assertEquals(ls, rawList);
	}


	@Test(dataProvider = "listset")
	public void addAllAtIndexExisting(Double[] raw, List<Double> ls) {
		List<Double> rawList = new ArrayList<Double>(Arrays.asList(raw));
		
		// Adding existing should return false...
		Assert.assertFalse(ls.addAll(rawList));
		// ... and have no effect.
		Assert.assertEquals(ls, rawList);
	}
	
	@Test(dataProvider = "listset-and-not-contained")
	public void addAllAtIndexMixedBeginning(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> rawList = new ArrayList<Double>(Arrays.asList(raw));
		
		// Make a list containing existing and new items interleaved.
		List<Double> toAdd = new ArrayList<>();
		List<Double> toAddNC = new ArrayList<>();
		if (raw.length > 0) {
			for (int i = 0; i < raw.length; i++) {
				toAdd.add(raw[i]);
				toAdd.add(notContained[i]);
				toAddNC.add(notContained[i]);
			}
		}
		else {
			toAdd.add(notContained[0]);
			toAddNC.add(notContained[0]);
		}
		
		Assert.assertTrue(ls.addAll(0, toAdd));
		rawList.addAll(0, toAddNC);
		Assert.assertEquals(ls, rawList);
	}
	
	@Test(dataProvider = "listset-and-not-contained")
	public void addAllAtIndexMixedMiddle(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> rawList = new ArrayList<Double>(Arrays.asList(raw));
		
		// Make a list containing existing and new items interleaved.
		List<Double> toAdd = new ArrayList<>();
		List<Double> toAddNC = new ArrayList<>();
		if (raw.length > 0) {
			for (int i = 0; i < raw.length; i++) {
				toAdd.add(raw[i]);
				toAdd.add(notContained[i]);
				toAddNC.add(notContained[i]);
			}
		}
		else {
			toAdd.add(notContained[0]);
			toAddNC.add(notContained[0]);
		}
		
		Assert.assertTrue(ls.addAll(raw.length / 2, toAdd));
		rawList.addAll(raw.length / 2, toAddNC);
		Assert.assertEquals(ls, rawList);
	}
	
	@Test(dataProvider = "listset-and-not-contained")
	public void addAllAtIndexMixedEnd(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> rawList = new ArrayList<Double>(Arrays.asList(raw));
		
		// Make a list containing existing and new items interleaved.
		List<Double> toAdd = new ArrayList<>();
		List<Double> toAddNC = new ArrayList<>();
		if (raw.length > 0) {
			for (int i = 0; i < raw.length; i++) {
				toAdd.add(raw[i]);
				toAdd.add(notContained[i]);
				toAddNC.add(notContained[i]);
			}
		}
		else {
			toAdd.add(notContained[0]);
			toAddNC.add(notContained[0]);
		}
		
		Assert.assertTrue(ls.addAll(raw.length, toAdd));
		rawList.addAll(raw.length, toAddNC);
		Assert.assertEquals(ls, rawList);
	}
	
	
	@Test(dataProvider = "listset")
	public void iterator(Double[] raw, List<Double> ls) {
		List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
		Iterator<Double> rawItr = rawList.iterator();
		Iterator<Double> lsItr = ls.iterator();
		boolean remove = true;
		
		while (true) {
			boolean hasNext = rawItr.hasNext();
			Assert.assertEquals(hasNext, lsItr.hasNext());
			
			if (hasNext) {
				Assert.assertEquals(rawItr.next(), lsItr.next());
				
				if (remove) {
					rawItr.remove();
					lsItr.remove();
					Assert.assertEquals(rawList, ls);
				}
			}
			else {
				try {
					lsItr.next();
					Assert.fail("Iterator did not throw an Exception when iterator exhausted.");
				}
				catch (NoSuchElementException ex){}
				catch (Exception ex){
					Assert.fail("Iterator did not throw a NoSuchElementException when iterator exhausted.");
				}
				break;
			}
			
			remove = !remove;
		}
	}

	@Test(dataProvider = "listset")
	public void listIterator(Double[] raw, List<Double> ls) {
		testListIterator(ls.listIterator(), Arrays.asList(raw).listIterator());
	}

	@Test(dataProvider = "listset")
	public void listIteratorFromIndex(Double[] raw, List<Double> ls) {
		for (int i = 0; i < raw.length; i++) {
			testListIterator(ls.listIterator(i), Arrays.asList(raw).listIterator(i));
		}
	}
	
	/**
	 * Basic test only, functional test tests reverse traversal and modification via iterator.
	 */
	public void testListIterator(ListIterator<?> lsItr, ListIterator<?> rawItr) {
		while (true) {
			boolean hasNext = rawItr.hasNext();
			Assert.assertEquals(hasNext, lsItr.hasNext());
			
			if (hasNext) {
				Assert.assertEquals(rawItr.next(), lsItr.next());
			}
			else {
				try {
					lsItr.next();
					Assert.fail("Iterator did not throw an Exception when iterator exhausted.");
				}
				catch (NoSuchElementException ex){}
				catch (Exception ex){
					Assert.fail("Iterator did not throw a NoSuchElementException when iterator exhausted.");
				}
				break;
			}
		}
	}
	
	@Test(dataProvider = "listset")
	public void removeIndexZero(Double[] raw, List<Double> ls) {
		List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
		while (!rawList.isEmpty()) {
			rawList.remove(0);
			ls.remove(0);
			Assert.assertEquals(ls, rawList);
		}
	}
	
	@Test(dataProvider = "listset")
	public void removeIndexOne(Double[] raw, List<Double> ls) {
		List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
		while (rawList.size() > 1) {
			rawList.remove(1);
			ls.remove(1);
			Assert.assertEquals(ls, rawList);
		}
	}
	
	@Test(dataProvider = "listset")
	public void removeIndexLast(Double[] raw, List<Double> ls) {
		List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
		while (!rawList.isEmpty()) {
			ls.remove(rawList.size()-1);
			rawList.remove(rawList.size()-1);
			Assert.assertEquals(ls, rawList);
		}
	}
	
	@Test(dataProvider = "listset", expectedExceptions = IndexOutOfBoundsException.class)
	public void removeIndexOutOfBoundsNegative(Double[] raw, List<Double> ls) {
		ls.remove(-1);
	}
	
	@Test(dataProvider = "listset", expectedExceptions = IndexOutOfBoundsException.class)
	public void removeIndexOutOfBoundsPositive(Double[] raw, List<Double> ls) {
		ls.remove(raw.length);
	}
	
	@Test(dataProvider = "listset")
	public void removeObjectExists(Double[] raw, List<Double> ls) {
		List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
		
		for (int i = 0; i < raw.length; i++) {
			ls.remove(raw[i]);
			rawList.remove(raw[i]);
			Assert.assertEquals(ls, rawList);
		}
	}

	@Test(dataProvider = "listset")
	public void removeObjectExistsReverse(Double[] raw, List<Double> ls) {
		List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
		
		for (int i = raw.length-1; i >= 0; i--) {
			ls.remove(raw[i]);
			rawList.remove(raw[i]);
			Assert.assertEquals(ls, rawList);
		}
	}

	@Test(dataProvider = "listset-and-not-contained")
	public void removeObjectNotExists(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
		
		for (int i = 0; i < notContained.length; i++) {
			ls.remove(notContained[i]);
			rawList.remove(notContained[i]);
			Assert.assertEquals(ls, rawList);
		}
	}

	@Test(dataProvider = "listset")
	public void removeAllExistsAll(Double[] raw, List<Double> ls) {
		if (raw.length > 0) {
			List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
			Assert.assertTrue(ls.removeAll(Arrays.asList(raw)));
			rawList.removeAll(Arrays.asList(raw));
			Assert.assertEquals(ls, rawList);
		}
	}
	
	@Test(dataProvider = "listset")
	public void removeAllExistsFirstFew(Double[] raw, List<Double> ls) {
		if (raw.length > 0) {
			List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
			Assert.assertTrue(ls.removeAll(Arrays.asList(raw).subList(0, raw.length/2+1)));
			rawList.removeAll(Arrays.asList(raw).subList(0, raw.length/2+1));
			Assert.assertEquals(ls, rawList);
		}
	}
	
	@Test(dataProvider = "listset")
	public void removeAllExistsLastFew(Double[] raw, List<Double> ls) {
		if (raw.length > 0) {
			List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
			Assert.assertTrue(ls.removeAll(Arrays.asList(raw).subList(raw.length/2, raw.length)));
			rawList.removeAll(Arrays.asList(raw).subList(raw.length/2, raw.length));
			Assert.assertEquals(ls, rawList);
		}
	}
	
	
	@Test(dataProvider = "listset-and-not-contained")
	public void removeAllNotExists(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
		List<Double> ncList = new ArrayList<>(Arrays.asList(notContained));
		
		for (int j = 0; j < notContained.length; j++) {
			Assert.assertFalse(ls.removeAll(ncList.subList(0, j+1)));
			Assert.assertEquals(ls, rawList);
			
			Assert.assertFalse(ls.removeAll(ncList.subList(j, notContained.length)));
			Assert.assertEquals(ls, rawList);
		}
	}
	
	@Test(dataProvider = "listset")
	public void replaceAllExists(Double[] raw, List<Double> ls) {
		if (raw.length > 1) {
			for (int i = 0; i < raw.length; i++) {
				try {
					final int index = i;
					ls.replaceAll((Double e) -> { return raw[index]; });
					Assert.fail("Replace element with existing at different index but no Exception thrown.");
				}
				catch (IllegalArgumentException ex) {}
				catch (Exception ex) {
					Assert.fail("Replace element with existing at different index did not throw an IllegalArgumentException.", ex);
				}
			}
		}
	}
	
	@Test(dataProvider = "listset")
	public void replaceAllExistsEqual(Double[] raw, List<Double> ls) {
		ls.replaceAll((Double e) -> { return e == null ? null : e + 0; });
	}
	
	@Test(dataProvider = "listset-and-not-contained")
	public void replaceAllExistsFillDiff(Double[] raw, List<Double> ls, Double[] notContained) {
		if (raw.length > 1) {
				for (int i = 0; i < notContained.length; i++) {
				try {
					final int index = i;
					ls.replaceAll((Double e) -> { return notContained[index]; });
					Assert.fail("Replace element with existing at different index (fill operation) but no Exception thrown.");
				}
				catch (IllegalArgumentException ex) {}
				catch (Exception ex){
					Assert.fail("Replace element with existing at different index (fill operation) did not throw an IllegalArgumentException.", ex);
				}
			}
		}
	}
	
	@Test(dataProvider = "listset-and-not-contained")
	public void replaceAllNotExists(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> rawList = new ArrayList<Double>(Arrays.asList(raw));
		ls.replaceAll((Double e) -> { return (e == null ? 0 : e)  + 1000d; });
		rawList.replaceAll((Double e) -> { return (e == null ? 0 : e) + 1000d; });
		Assert.assertEquals(ls, rawList);
	}

	
	@Test(dataProvider = "listset")
	public void retainAllSame(Double[] raw, List<Double> ls) {
		Assert.assertFalse(ls.retainAll(Arrays.asList(raw)));
		List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
		rawList.retainAll(Arrays.asList(raw));
		Assert.assertEquals(ls, rawList);
	}

	@Test(dataProvider = "listset")
	public void retainAllReverse(Double[] raw, List<Double> ls) {
		List<Double> rawListReverse = new ArrayList<>(Arrays.asList(raw));
		Collections.reverse(rawListReverse);
		Assert.assertFalse(ls.retainAll(rawListReverse));
		List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
		rawList.retainAll(rawListReverse);
		Assert.assertEquals(ls, rawList);
	}

	@Test(dataProvider = "listset")
	public void retainAllExistsAll(Double[] raw, List<Double> ls) {
		List<Double> rawList = Arrays.asList(raw);
		List<Double> aList = new ArrayList<>(Arrays.asList(raw));
		boolean ret = aList.retainAll(rawList);
		Assert.assertEquals(ls.retainAll(rawList), ret);
		Assert.assertEquals(ls, aList);
	}
	
	@Test(dataProvider = "listset")
	public void retainAllExistsFirstFew(Double[] raw, List<Double> ls) {
		if (raw.length > 0) {
			List<Double> toRetain = Arrays.asList(raw).subList(0, raw.length/2+1);
			List<Double> aList = new ArrayList<>(Arrays.asList(raw));
			boolean ret = aList.retainAll(toRetain);
			Assert.assertEquals(ls.retainAll(toRetain), ret);
			Assert.assertEquals(ls, aList);
		}
	}
	
	@Test(dataProvider = "listset")
	public void retainAllExistsLastFew(Double[] raw, List<Double> ls) {
		List<Double> toRetain = Arrays.asList(raw).subList(raw.length/2, raw.length);
		List<Double> aList = new ArrayList<>(Arrays.asList(raw));
		boolean ret = aList.retainAll(toRetain);
		Assert.assertEquals(ls.retainAll(toRetain), ret);
		Assert.assertEquals(ls, aList);
	}
	
	@Test(dataProvider = "listset-and-not-contained")
	public void retainAllNotExists(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
		List<Double> ncList = Arrays.asList(notContained);
		boolean ret = rawList.retainAll(ncList);
		Assert.assertEquals(ls.retainAll(ncList), ret);
		Assert.assertEquals(ls, rawList);
	}
	
	@Test(dataProvider = "listset-and-not-contained")
	public void retainAllMixed(Double[] raw, List<Double> ls, Double[] notContained) {
		// Make a list containing existing and new items interleaved.
		List<Double> toRetain = new ArrayList<>();
		for (int i = 0; i < raw.length; i++) {
			toRetain.add(raw[i]);
			toRetain.add(notContained[i]);
		}
		List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
		ls.retainAll(toRetain);
		rawList.retainAll(toRetain);
		Assert.assertEquals(ls, rawList);
	}
	

	@Test(dataProvider = "listset")
	public void setExistsSameIndex(Double[] raw, List<Double> ls) {
		for (int i = 0; i < raw.length; i++) {
			Assert.assertEquals(ls.set(i, raw[i]), raw[i]);
			Assert.assertEquals(ls, Arrays.asList(raw));
		}
	}
	
	@Test(dataProvider = "listset")
	public void setExistsDiffIndex(Double[] raw, List<Double> ls) {
		for (int i = 0; i < raw.length; i++) {
			for (int j = 0; j < raw.length; j++) {
				if (i != j) {
					try {
						ls.set(i, raw[j]);
						Assert.fail("Set element with existing at different index but no Exception thrown.");
					}
					catch (IllegalArgumentException ex) {}
					catch (Exception ex) {
						Assert.fail("Set element with existing at different index did not throw an IllegalArgumentException.", ex);
					}
				}
			}
		}
	}
	
	@Test(dataProvider = "listset-and-not-contained")
	public void setExistsDiffFill(Double[] raw, List<Double> ls, Double[] notContained) {
		if (raw.length > 1) {
			for (int j = 0; j < notContained.length; j++) {
				try {
					for (int i = 0; i < raw.length; i++) { 
						ls.set(i, notContained[j]);
					}
					Assert.fail("Set element with existing at different index (fill operation) but no Exception thrown.");
				}
				catch (IllegalArgumentException ex) {}
				catch (Exception ex) {
					Assert.fail("Set element with existing at different index (fill operation) did not throw an IllegalArgumentException.", ex);
				}
			}
		}
	}
	
	@Test(dataProvider = "listset-and-not-contained")
	public void setNotExists(Double[] raw, List<Double> ls, Double[] notContained) {
		List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
		for (int j = 0; j < raw.length; j++) {
			ls.set(j, notContained[j]);
			rawList.set(j, notContained[j]);
			Assert.assertEquals(ls, rawList);
		}
	}
	
	
	@Test(dataProvider = "listset")
	public void sort(Double[] raw, List<Double> ls) {
		ls.sort(new Comp());
		List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
		rawList.sort(new Comp());
		Assert.assertEquals(ls, rawList);
	}
	
	@Test(dataProvider = "listset")
	public void sortReverse(Double[] raw, List<Double> ls) {
		ls.sort(Collections.reverseOrder(new Comp()));
		List<Double> rawList = new ArrayList<>(Arrays.asList(raw));
		rawList.sort(Collections.reverseOrder(new Comp()));
		Assert.assertEquals(ls, rawList);
	}
	
	private class Comp implements Comparator<Double> {
		@Override
		public int compare(Double e1, Double e2) {
			if (e1 == null) e1 = 0d;
			if (e2 == null) e2 = 0d;
			return Double.compare(e1, e2);
		}
	}
	
	
	@Test(dataProvider = "listset")
	public void toArray(Double[] raw, List<Double> ls) {
		Assert.assertEquals(ls.toArray(), raw);
	}
	
	@Test(dataProvider = "listset")
	public void toArrayProvided(Double[] raw, List<Double> ls) {
		Assert.assertEquals(ls.toArray(new Double[raw.length]), raw);
	}
}
