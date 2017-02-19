import hivis.common.*;
import hivis.data.*;
import hivis.data.view.*;

// Examples of working with HiVis DataValues.

void setup() {
	// Create some DataValues with specific initial values.
	// Integer values.
	DataValue int2 = HV.newIntegerValue(2);
	DataValue int5 = HV.newIntegerValue(5); 
	// Real values.
	DataValue pi = HV.newRealValue(3.141592653589793);
	
	println("int2 => " + int2);
	println("int5 => " + int5);
	println("pi => " + pi);
	
	
	// We can create new values by performing simple arithmetic operations on an existing value.
	DataValue int5PlusInt2 = int5.add(int2);
	float variable1 = 12.5f;
	DataValue int2SubtractVariable1 = int2.subtract(variable1);
	DataValue twoPi = pi.multiply(2);
	DataValue piAgain = twoPi.divide(int2);
	
	println();
	println("int5PlusInt2 = int5.add(int2) => " + int5PlusInt2);
	println("variable1 => " + variable1);
	println("int2SubtractVariable1 = int2.subtract(variable1) => " + int2SubtractVariable1);
	println("twoPi = pi.multiply(2) => " + twoPi);
	println("piAgain = twoPi.divide(int2) => " + piAgain);
	
	// Some things to note about the above operations:
	// * We can use a DataValue, a constant, or a variable as the argument for the various arithmetic functions.
	// * We subtracted a real, or decimal, number (variable1) from an integer DataValue.
	//   In general HiVis will do its best to accommodate the interchange of data of different types when it makes 
	//   sense to do so. In this case the returned DataValue will be of a real (decimal) type.
	//   However if we tried to set an integer DataValue to a non-integer value an error would occur, for example:
	//int2.set(0.5); 
	
	
	// Another thing to note is that the new DataValues are actually "Views" of the original
	// DataValue(s). Changes to the original DataValue(s) will be reflected in the new
	// DataValue.
	// For example if we change the value of DataValue int5 to 10:
	int5.set(10);
	// It will be reflected in DataValue int5PlusInt2:
	println("int5PlusInt2 (int5 = 10) => " + int5PlusInt2);
	// Or if we change the sign of DataValue pi:
	// (note that getDouble gets the value of the DataValue as a real (decimal) number. 
	// Double stands for double-precision, the most accurate available in Java).
	pi.set(-pi.getDouble());
	// It will be reflected in DataValue piAgain, via DataValue twoPi.
	println("piAgain (pi = -3.141...) => " + piAgain);
	
	
	// Note that you can't set the value of a view DataValue. The following would cause an error:
	//piAgain.set(2);
	
	
	// We can also create view DataValues by performing custom operations on existing DataValues:
	DataValue customFunc = int2.apply(new Function<Integer, Double>() {
		public Double apply(Integer input) {
			return Math.sqrt(input);
		}
	});
	println("customFunc = sqrt(int2) => " + customFunc);
}
