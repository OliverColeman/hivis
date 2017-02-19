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


import hivis.common.HV;
import hivis.data.DataSeries;
import hivis.data.DataTable;
import hivis.data.DataValue;
import hivis.data.view.CalcValue;
import hivis.data.view.Function;
import hivis.data.view.TableViewTranspose;

/**
 * Examples of working with {@link DataSeries}.
 * 
 * @author O. J. Coleman
 */
public class Series {
	public static void main(String[] args) {
		// Create a data series containing the specified integer numbers.
		DataSeries<Integer> intNumbers = HV.newIntegerSeries(1, 1, 2, 3, 5, 8);
		
		// Create an empty real number data series and add some values to it.
		DataSeries<Double> realNumbers = HV.newRealSeries();
		for (Integer v : intNumbers) {
			realNumbers.append(Math.log(v));
		}
		
		System.out.println("\nintNumbers\n" + intNumbers);
		System.out.println("\nrealNumbers\n" + realNumbers);
		
		
		// We can set the values in a series, here we swap the first values in our series.
		// Some thing to note here is that we're assigning a real (double) number value to a series that stores integer (int) values. 
		// In general HiVis will do its best to accommodate the interchange of data of different types when it makes sense to do so. 
		// (If we tried to add a value of, say 0.5, to an integer series then an error would occur.)
		int int0 = intNumbers.get(0);
		double real0 = realNumbers.get(0);
		intNumbers.set(0, real0);
		realNumbers.set(0, int0);
		System.out.println("\nintNumbers (changed first value)\n" + intNumbers);
		System.out.println("\nrealNumbers (changed first value)\n" + realNumbers);
		
		
		// We can create new series by performing simple arithmetic operations on each element of an existing series:
		DataSeries<Double> plus1 = realNumbers.add(1);
		System.out.println("\nplus1 = realNumbers.add(1)\n" + plus1);
		// Similar functions exist for subtract, multiply and divide.
		
		// Or by performing simple arithmetic operations over all elements of two series:
		DataSeries<Double> realsMinusInts = realNumbers.subtract(intNumbers);
		System.out.println("\nrealsMinusInts = realNumbers.subtract(intNumbers)\n" + realsMinusInts);
		
		// A useful built-in series method is toUnitRange(), which creates a series containing the
		// values in the original series scaled to the unit range [0, 1]:	
		DataSeries<Double> realNumbersUnitRange = realNumbers.toUnitRange();
		System.out.println("\nrealNumbersUnitRange = realNumbers.toUnitRange()\n" + realNumbersUnitRange);
		
		
		// We can also create new series by performing custom operations on each element of an existing series:
		DataSeries<Double> customFunc = plus1.apply(new Function<Double, Double>() {
			public Double apply(Double input) {
				return Math.pow(input, 3);
			}
		});
		System.out.println("\ncustomFunc = plus1^3\n" + customFunc);
		
		
		// Series can be appended to each other:
		DataSeries<Double> realNumbersAppendPlus1 = realNumbers.append(plus1);
		System.out.println("\nrealNumbersAppendPlus1 = realNumbers.append(plus1)\n" + realNumbersAppendPlus1);
				
		
		// The series created above, starting with 'plus1', are "views" of the original series but with the operation 
		// performed on each element, so changes in the original series will be reflected in the new series.
		// Note that the customFunc series is a view of the "plus1" series, which in turn is a view of realNumbers; 
		// changes to the underlying data will "bubble up" through the chain of views.
		realNumbers.append(55.55);
		System.out.println("\nrealNumbersUnitRange reflecting appended value in realNumbers\n" + realNumbersUnitRange);
		System.out.println("\nplus1 reflecting appended value in realNumbers\n" + plus1);
		System.out.println("\ncustomFunc reflecting appended value in realNumbers (via plus1)\n" + customFunc);
		System.out.println("\nrealNumbersAppendPlus1 reflecting appended value in realNumbers\n" + realNumbersAppendPlus1);
		
		
		// We can create a series that provides a view of the elements in a series filtered and/or rearranged:
		DataSeries<Double> realNumbersRearranged = realNumbers.select(5, 3, 1);
		System.out.println("\nrealNumbersRearranged = realNumbers.select(5, 3, 1)\n" + realNumbersRearranged);
		
		
		// Some other built-in methods are min, max, sum (result of adding all values together), product (result of 
		// multiplying all values) and mean (arithmetic mean, or average, over all values). These all return the result
		// of the operation as a DataValue. Note that DataValues are typed but we're ignoring that here for convenience and readability.
		DataValue intNumbersMin = intNumbers.min(); // Returns a DataValue<Integer> because the series is Integer.
		DataValue intNumbersMax = intNumbers.max(); // Returns a DataValue<Integer> because the series is Integer.
		DataValue intNumbersSum = intNumbers.sum(); // Returns a DataValue<Integer> because the series is Integer.
		DataValue intNumbersProduct = intNumbers.product(); // Returns a DataValue<Integer> because the series is Integer.
		DataValue intNumbersMean = intNumbers.mean(); // Returns a DataValue<Double> because the mean may be fractional.
		DataValue intNumbersVariance = intNumbers.variance(); // Returns a DataValue<Double> because the mean may be fractional.
		DataValue intNumbersStdDev = intNumbers.stdDev(); // Returns a DataValue<Double> because the mean may be fractional.
		System.out.println("\nintNumbersMin = intNumbers.min() => " + intNumbersMin);
		System.out.println("intNumbersMax = intNumbers.mean() => " + intNumbersMax);
		System.out.println("intNumbersSum = intNumbers.mean() => " + intNumbersSum);
		System.out.println("intNumbersProduct = intNumbers.mean() => " + intNumbersProduct);
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
		DataSeries intNumbersUnitRange = intNumbers.asDouble().subtract(intNumbersMin).divide(intNumbersMax.subtract(intNumbersMin));
		System.out.println("\nintNumbersUnitRange = intNumbers.asDouble().subtract(intNumbersMin).divide(intNumbersMax.subtract(intNumbersMin))\n" + intNumbersUnitRange);
		// Note that we convert the integer series to represent real (double) numbers so 
		// that the divide operation doesn't convert the calculated values to integers.
		
		// Changes in the min and max DataValues (which reflect changes in the intNumbers series) will be reflected in the calculated series:
		intNumbers.set(0, 0);
		System.out.println("\nintNumbers (changed first value)\n" + intNumbers);
		System.out.println("\nintNumbersUnitRange reflecting changed value in intNumbers => " + intNumbersUnitRange);
		
		
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
		System.out.println("\nvector1\n" + vector1);
		System.out.println("\nvector2\n" + vector2);
		System.out.println("\ndotProduct => " + dotProduct);
		
		// Note that a nicer way to achieve the above is:
		DataValue dotProductNice = vector1.multiply(vector2).sum();
		System.out.println("\ndotProductNice = vector1.multiply(vector2).sum() => " + dotProductNice);
		
	}
}
