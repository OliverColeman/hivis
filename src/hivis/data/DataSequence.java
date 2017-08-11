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
package hivis.data;

/**
 * Represents an ordered sequence of items, possibly of different types.
 * Provides methods to access the items as numeric values (if applicable).
 * This is a common interface for {@link DataSeries} and {@link DataRow}s.
 * 
 * @author O. J. Coleman
 */
public interface DataSequence extends Data {

	/**
	 * Returns the number of elements in this sequence.
	 */
	int length();
	
	/**
	 * Returns true iff the element at the specified index is numeric.
	 * @throws IndexOutOfBoundsException if the index is invalid.
	 */
	boolean isNumeric(int index);

	/**
	 * Get the element at the specified index.
	 * @throws IndexOutOfBoundsException if the index is invalid.
	 */
	Object get(int index);

	/**
	 * Get the element at the specified index as the primitive type 'boolean'.
	 * @throws IndexOutOfBoundsException if the index is invalid.
	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a boolean.
	 */
	boolean getBoolean(int index);

	/**
	 * Get the element at the specified index as the primitive type 'int'.
	 * @throws IndexOutOfBoundsException if the index is invalid.
	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to an int.
	 */
	int getInt(int index);

	/**
	 * Get the element at the specified index as the primitive type 'long'.
	 * @throws IndexOutOfBoundsException if the index is invalid.
	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a long.
	 */
	long getLong(int index);

	/**
	 * Get the element at the specified index as the primitive type 'float'.
	 * @throws IndexOutOfBoundsException if the index is invalid.
	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a float.
	 */
	float getFloat(int index);

	/**
	 * Get the element at the specified index as the primitive type 'double'.
	 * @throws IndexOutOfBoundsException if the index is invalid.
	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a double.
	 */
	double getDouble(int index);

}