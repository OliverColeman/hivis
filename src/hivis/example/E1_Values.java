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
import hivis.data.view.Function;
import hivis.data.view.TableViewTranspose;

/**
 * Examples of working with {@link DataValue}s.
 * 
 * @author O. J. Coleman
 */
public class E1_Values {
	public static void main(String[] args) {
		// A DataValue represents a single data item. The data represented  
		// may be numeric, strings, dates/times or any other type of object.
		
		// Create some DataValues with specific initial values.
		// Integer values.
		DataValue int2 = HV.newIntegerValue(2);
		DataValue int5 = HV.newIntegerValue(5); 
		// Real values.
		DataValue pi = HV.newRealValue(3.141592653589793);
		
		System.out.println("int2 => " + int2);
		System.out.println("int5 => " + int5);
		System.out.println("pi => " + pi);
		
		
		// We can create new values by performing simple arithmetic operations on an existing value.
		DataValue int5PlusInt2 = int5.add(int2);
		float variable1 = 12.5f;
		DataValue int2SubtractVariable1 = int2.subtract(variable1);
		DataValue twoPi = pi.multiply(2);
		DataValue piAgain = twoPi.divide(int2);
		
		System.out.println();
		System.out.println("int5PlusInt2 = int5.add(int2) => " + int5PlusInt2);
		System.out.println("variable1 => " + variable1);
		System.out.println("int2SubtractVariable1 = int2.subtract(variable1) => " + int2SubtractVariable1);
		System.out.println("twoPi = pi.multiply(2) => " + twoPi);
		System.out.println("piAgain = twoPi.divide(int2) => " + piAgain);
		
		// Some things to note about the above operations:
		// * We can use a DataValue, a constant, or a variable as the argument for the various arithmetic functions.
		// * We subtracted a real, or decimal, number (variable1) from an integer DataValue.
		//   In general HiVis will do its best to accommodate the interchange of data of different types when it makes 
		//   sense to do so. In this case the returned DataValue will be of a real (decimal) type.
		//   However if we tried to set an integer DataValue to a non-integer value an error would occur, for example:
		//int2.set(0.5); 
		
		
		// Another thing to note is that the new DataValues are actually "Views" of the original
		// DataValue(s). Changes to the original DataValue(s) will be reflected in the new DataValue.
		// For example if we change the value of DataValue int5 to 10:
		int5.set(10);
		// It will be reflected in DataValue int5PlusInt2:
		System.out.println("int5PlusInt2 (int5 = 10) => " + int5PlusInt2);
		// Or if we change the sign of DataValue pi:
		// (note that getDouble gets the value of the DataValue as a real (decimal) number. 
		// Double stands for double-precision, the most accurate available in Java).
		pi.set(-pi.getDouble());
		// It will be reflected in DataValue piAgain, via DataValue twoPi.
		System.out.println("piAgain (pi = -3.141...) => " + piAgain);
		
		
		// Note that you can't set the value of a view DataValue. The following would cause an error:
		//piAgain.set(2);
		
		
		
		// We can derive new series by applying a custom function to each element of a series.
		// You define the function via an implementation of the Function class:
		
		// Note: The type of the argument for the apply method should match the type of the data represented by the series.
		// (Advanced Java developers: the return type of the apply method should be Object unless you've specified, via 
		//    generics, the input and output type of the function: for example new Function<MyInputType, MyOutputType>()...)
		
		
		
		// We can also derive a View DataValue by applying a custom function to it.
		// You define the function via an implementation of the Function class:
		DataValue customFunc = int2.apply(new Function() {
			public Object apply(int input) {
				return Math.sqrt(input);
			}
		});
		System.out.println("customFunc = sqrt(int2) => " + customFunc);
		// Note: The type of the argument for the apply method should match the type of the data represented by the DataValue.
		// 		In the above example the source DataValue stores an int, so the input value for the function has type int. 
		// (Advanced Java developers: the return type of the apply method should be Object unless you've specified, via 
		//		generics, the input and output type of the function: for example new Function<MyInputType, MyOutputType>()...)
	}
}
