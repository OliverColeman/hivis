/**
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public License
 * as published by the Free Software Foundation; either version 2.1
 * of the License, or (at your option) any later version.
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General
 * Public License along with this library; if not, write to the
 * Free Software Foundation, Inc., 59 Temple Place, Suite 330,
 * Boston, MA 02111-1307 USA
 */

package hivis.example;


import java.awt.Point;
import java.time.LocalDate;

import hivis.common.HV;
import hivis.data.DataSeries;
import hivis.data.DataValue;
import hivis.data.view.CalcValue;
import hivis.data.view.Function;

/**
 * Examples of working with {@link DataSeries}.
 * 
 * @author O. J. Coleman
 */
public class Series {
	public static void main(String[] args) {
		// DataSeries represent a sequence (or vector or list) of data items. The data represented by a DataSeries
		// may be numeric, strings, dates/times or any other type of object.
		// Numeric DataSeries have numerous built-in methods for performing arithmetic operations over the data.
		
		// (Advanced Java developers: DataSeries have a generic type parameter for the data they represent, however 
		// this can generally be safely ignored. This is done here for readability and simplicity (one of the core 
		// design principles of HiVis). The examples below indicate when type must be taken into consideration.)
		
		// We can create a data series containing a specified sequence of integer numbers:
		DataSeries intNumbers = HV.newIntegerSeries(1, 1, 2, 3, 5, 8);
		
		// And an empty real number data series. If we provide numeric arguments as we did above they would be 
		// inserted into the series.
		DataSeries realNumbers = HV.newRealSeries();
		
		
		// We can iterate over a DataSeries using a 'for' loop. 
		// Here we use this to add numbers to our realNumbers series:
		for (int v : intNumbers.asInt()) {
			realNumbers.append(Math.log(v));
		}
		// Note: because we haven't specified the data type represented by the series (eg DataSeries<Integer>) we use 
		// the asInt() method to get a view of the intNumbers series as explicitly representing integers, so the type 
		// of the variable 'v' above can be int.
		
		System.out.println("\nintNumbers =>\n\t" + intNumbers);
		System.out.println("\nrealNumbers =>\n\t" + realNumbers);
		
		
		// We can set the values in a series - providing it is not calculated from another data set, more on this below. 
		// Here we swap the first values in our series:
		int int0 = intNumbers.getInt(0); // getInt returns the value as an integer.
		double real0 = realNumbers.getDouble(0); // getDouble returns the vale as a double-precision floating-point (real) number.
		intNumbers.set(0, real0);
		realNumbers.set(0, int0);
		System.out.println("\nintNumbers (changed first value) =>\n\t" + intNumbers);
		System.out.println("\nrealNumbers (changed first value) =>\n\t" + realNumbers);
		// Some thing to note here is that we assigned a real (double) number value to a series that stores integer (int) values. 
		// In general HiVis will do its best to accommodate the interchange of data of different types when it makes sense to do so. 
		// (If we tried to set a value in a series representing integers to, say 1.5, then an error would occur.)
		
		
		// We can derive new series by performing simple arithmetic operations on each element of an existing series:
		DataSeries plus1 = realNumbers.add(1);
		System.out.println("\nplus1 = realNumbers.add(1) =>\n\t" + plus1);
		// Similar functions exist for subtract, multiply and divide.
		// (Advanced Java developers: the number type of the new series will be set to accommodate the calculated values. 
		//   For example if one series stores ints and the other floats then the new series will be of type double.
		//   The divide operation always returns a series representing doubles.)
		
		// Or by performing simple arithmetic operations over all elements of two series:
		DataSeries realsMinusInts = realNumbers.subtract(intNumbers);
		System.out.println("\nrealsMinusInts = realNumbers.subtract(intNumbers) =>\n\t" + realsMinusInts);
		
		// We can also derive a series from another by performing common numeric operations such as exponential, 
		// logarithm, square root, trigonometric and many others on each element of the series.
		// Any method in Java's Math class (see https://docs.oracle.com/javase/8/docs/api/java/lang/Math.html)
		// that accepts a single argument/parameter may be used. For example:
		DataSeries cosineIntNumbers = intNumbers.applyMathMethod("cos");
		System.out.println("\ncosineIntNumbers = intNumbers.applyMathMethod(\"cos\") =>\n\t" + cosineIntNumbers);
		
		
		// A useful built-in series method is toUnitRange(), which creates a series containing the
		// values in the original series scaled to the unit range [0, 1]:	
		DataSeries realNumbersUnitRange = realNumbers.toUnitRange();
		System.out.println("\nrealNumbersUnitRange = realNumbers.toUnitRange() =>\n\t" + realNumbersUnitRange);
		
		
		// We can derive new series by applying a custom function to each element of a series.
		// You define the function via an implementation of the Function class.
		// The type of the argument for the apply method should match the type of the data represented by the series.
		DataSeries customFunc = plus1.apply(new Function() {
			public Object apply(double input) {
				return Math.pow(input, 3);
			}
		});
		System.out.println("\ncustomFunc = plus1^3 =>\n\t" + customFunc);
		// (Advanced Java developers: the return type of the apply method should be Object unless you've specified, via 
		//    generics, the input and output type of the function: for example new Function<MyInputType, MyOutputType>()...)

		// A function doesn't have to be numeric, for the input or the return value, 
		// nor does the input and output type have to be the same.
		// Here we create a function that accepts Strings and returns Points.
		// Some pivotal peoples' birth dates.
		DataSeries myStrings = HV.newSeries("1791-12-26", "1815-11-02", "1815-12-10", "1910-06-22", "1912-06-23");
		DataSeries customFunc2 = myStrings.apply(new Function() {
			public Object apply(String input) {
				// Parse the date using a class from Java's date/time API. 
				LocalDate date = LocalDate.parse(input);
				// Make a Point with the X coordinate set to the year and the Y set to the month.
				return new Point(date.getYear(), date.getMonthValue());
			}
		});
		System.out.println("\ncustomFunc2 (type is " + customFunc2.getType() + ") => \n\t" + customFunc2);
		
		// And a function that accepts Points and returns Strings:
		DataSeries customFunc3 = customFunc2.apply(new Function() {
			public Object apply(Point input) {
				return "(" + input.x + ", " + input.y + ")"; 
			}
		});
		System.out.println("\ncustomFunc3 (type is " + customFunc3.getType() + ") => \n\t" + customFunc3);
		
		
		// Finally, series can be appended to each other:
		DataSeries realNumbersAppendPlus1 = realNumbers.append(plus1);
		System.out.println("\nrealNumbersAppendPlus1 = realNumbers.append(plus1) =>\n" + realNumbersAppendPlus1);
		
		
		// The series created above, starting with 'plus1', are "views" of the original series but with the operation 
		// performed on each element, so changes in the original series will be reflected in the new series.
		
		// NOTE: These DataSeries that are calculated from other data sets (DataSeries or otherwise) cannot have their 
		// values directly manipulated via the set() method.  
		
		// Also note that the customFunc series is a view of the plus1 series, which in turn is a view of realNumbers; 
		// changes to the underlying data will "bubble up" through the chain of views.
		realNumbers.append(55.55);
		System.out.println("\nrealNumbersUnitRange reflecting appended value in realNumbers =>\n" + realNumbersUnitRange);
		System.out.println("\nplus1 reflecting appended value in realNumbers =>\n" + plus1);
		System.out.println("\ncustomFunc reflecting appended value in realNumbers (via plus1) =>\n" + customFunc);
		System.out.println("\nrealNumbersAppendPlus1 reflecting appended value in realNumbers =>\n" + realNumbersAppendPlus1);
		
		
		// We can create a series that provides a view of the elements in a series filtered and/or rearranged:
		DataSeries realNumbersRearranged = realNumbers.select(5, 3, 1);
		System.out.println("\nrealNumbersRearranged = realNumbers.select(5, 3, 1) =>\n" + realNumbersRearranged);
		
		
		// Some other built-in methods are min, max, sum, product, 
		// mean (average), variance, and stdDev (standard deviation). 
		// These all return the result of the operation as a DataValue. 
		// (Advanced Java developers: DataValues also have a generic type, but again this can usually be safely ignored.)
		DataValue intNumbersMin = intNumbers.min(); // Returns a DataValue<Integer> because the series is Integer.
		DataValue intNumbersMax = intNumbers.max(); // Returns a DataValue<Integer> because the series is Integer.
		DataValue intNumbersSum = intNumbers.sum(); // Returns a DataValue<Integer> because the series is Integer.
		DataValue intNumbersProduct = intNumbers.product(); // Returns a DataValue<Integer> because the series is Integer.
		DataValue intNumbersMean = intNumbers.mean(); // Returns a DataValue<Double> because the mean may be fractional.
		DataValue intNumbersVariance = intNumbers.variance(); // Returns a DataValue<Double> because the variance may be fractional.
		DataValue intNumbersStdDev = intNumbers.stdDev(); // Returns a DataValue<Double> because the standard deviation may be fractional.
		System.out.println("\nintNumbersMin = intNumbers.min() => " + intNumbersMin);
		System.out.println("intNumbersMax = intNumbers.max() => " + intNumbersMax);
		System.out.println("intNumbersSum = intNumbers.sum() => " + intNumbersSum);
		System.out.println("intNumbersProduct = intNumbers.product() => " + intNumbersProduct);
		System.out.println("intNumbersMean = intNumbers.mean() => " + intNumbersMean);
		System.out.println("intNumbersVariance = intNumbers.variance() => " + intNumbersVariance);
		System.out.println("intNumbersStdDev = intNumbers.stdDev() => " + intNumbersStdDev);
		
		// If the series changes then the value represented by the DataValue will be updated automatically:
		intNumbers.set(0, -100);
		System.out.println("\nintNumbers (changed first value)\n" + intNumbers);
		System.out.println("\nintNumbersMin reflecting changed value in intNumbers => " + intNumbersMin);
		System.out.println("intNumbersMax reflecting changed value in intNumbers => " + intNumbersMax);
		System.out.println("intNumbersSum reflecting changed value in intNumbers => " + intNumbersSum);
		System.out.println("intNumbersProduct reflecting changed value in intNumbers => " + intNumbersProduct);
		System.out.println("intNumbersMean reflecting changed value in intNumbers => " + intNumbersMean);
		System.out.println("intNumbersVariance reflecting changed value in intNumbers => " + intNumbersVariance);
		System.out.println("intNumbersStdDev reflecting changed value in intNumbers => " + intNumbersStdDev);
		
		// Operations over all elements in a series also accept DataValues. 
		// Thus we could recreate the toUnitRange method:
		DataSeries intNumbersUnitRange = intNumbers.subtract(intNumbers.min()).divide(intNumbers.max().subtract(intNumbers.min()));
		System.out.println("\nintNumbersUnitRange = intNumbers.subtract(intNumbersMin).divide(intNumbersMax.subtract(intNumbersMin))\n" + intNumbersUnitRange);
		// (Advanced Java developers: you may have noticed that we are calling intNumbers.min() twice, however the 
		//   minimum is not actually calculated twice as the same DataValue "view" is cached and reused by subsequent 
		//   calls to the min() method. The same is true for the other statistical methods - and even within the 
		//   methods where possible, eg mean() will create a cache of the sum DataValue as well as the mean DataValue).
		
		// Changes in the min and max DataValues (which reflect changes in the intNumbers series) will be reflected in the calculated series:
		intNumbers.set(0, 0);
		System.out.println("\nintNumbers (changed first value) =>\n" + intNumbers);
		System.out.println("\nintNumbersUnitRange reflecting changed value in intNumbers =>\n" + intNumbersUnitRange);
		
		
		// We can also create a DataValue from one or more Series with a custom function:
		DataSeries vector1 = HV.randomUniformSeries(3, 0, 1);
		DataSeries vector2 = HV.randomUniformSeries(3, 0, 1);
		DataValue dotProduct = new CalcValue<Double, Double>(vector1, vector2) {
			@Override
			public Double calc() {
				double dp = 0;
				DataSeries v1 = getInputSeries(0);
				DataSeries v2 = getInputSeries(1);
				for (int i = 0; i < v1.length(); i++) {
					dp += v1.getDouble(i) * v2.getDouble(i);
				}
				return dp;
			}
			
		};
		System.out.println("\nvector1 => " + vector1);
		System.out.println("\nvector2 => " + vector2);
		System.out.println("\ndotProduct => " + dotProduct);
		// Again, the dotProduct DataValue is a view, of the two input DataSeries, 
		// so changes to either vector1 or vector2 will be reflected in it.
		
		// Note that a nicer way to achieve the above is:
		DataValue dotProductNice = vector1.multiply(vector2).sum();
		System.out.println("\ndotProductNice = vector1.multiply(vector2).sum() => " + dotProductNice);
	}
}
