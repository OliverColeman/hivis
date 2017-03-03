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
 * A view of a row of a table. Typically a {@link DataTable}.
 * 
 * @author O. J. Coleman
 */
public interface DataRow {
	/**
	 * Returns the number of elements in this row.
	 */
	public int size();
	
	/**
	 * Returns the index of the table row this DataRow is referencing if applicable, otherwise -1.
	 */
	public int getRowIndex();
	
	/**
	 * Return the type of the element at the specified column index.
	 */
	Class<?> getType(int index);

	/**
	 * Return the type of the element at the specified column.
	 */
	Class<?> getType(String label);


	/**
	 * Get the element at the specified column.
	 */
	Object get(String label);

	/**
	 * Get the element at the specified column as the primitive type 'boolean'.
	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a boolean.
	 */
	boolean getBoolean(String label);

	/**
	 * Get the element at the specified column as the primitive type 'int'.
 	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to an int.
	 */
	int getInt(String label);

	/**
	 * Get the element at the specified column as the primitive type 'long'.
 	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a long.
	 */
	long getLong(String label);

	/**
	 * Get the element at the specified column as the primitive type 'float'.
	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a float.
	 */
	float getFloat(String label);
	
	/**
	 * Get the element at the specified column as the primitive type 'double'.
 	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a double.
	 */
	double getDouble(String label);
	
	/**
	 * Get the element at the specified column index.
	 */
	Object get(int index);
	
	/**
	 * Get the element at the specified column index as the primitive type 'boolean'.
	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a boolean.
	 */
	boolean getBoolean(int index);

	/**
	 * Get the element at the specified column index as the primitive type 'int'.
 	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to an int.
	 */
	int getInt(int index);

	/**
	 * Get the element at the specified column index as the primitive type 'long'.
 	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a long.
	 */
	long getLong(int index);

	/**
	 * Get the element at the specified column index as the primitive type 'float'.
	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a float.
	 */
	float getFloat(int index);
	
	/**
	 * Get the element at the specified column index as the primitive type 'double'.
 	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a double.
	 */
	double getDouble(int index);
}
