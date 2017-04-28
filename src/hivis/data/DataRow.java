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
public interface DataRow extends DataSequence {
	/**
	 * Returns the index of the table row this DataRow is referencing if applicable, otherwise -1.
	 */
	public int getRowIndex();
	
	/**
	 * Return the type of the element at the specified column index.
	 * @throws IllegalStateException If the row no longer exists in the underlying table.
	 */
	Class<?> getType(int index);

	/**
	 * Return the type of the element at the specified column.
	 * @throws IllegalStateException If the row no longer exists in the underlying table.
	 */
	Class<?> getType(String label);

	/**
	 * Returns true iff the element at the specified column is numeric.
	 */
	boolean isNumeric(String label);

	/**
	 * Get the element at the specified column.
	 * @throws IllegalStateException If the row no longer exists in the underlying table.
	 */
	Object get(String label);

	/**
	 * Get the element at the specified column as the primitive type 'boolean'.
	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a boolean.
	 * @throws IllegalStateException If the row no longer exists in the underlying table.
	 */
	boolean getBoolean(String label);

	/**
	 * Get the element at the specified column as the primitive type 'int'.
 	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to an int.
	 * @throws IllegalStateException If the row no longer exists in the underlying table.
	 */
	int getInt(String label);

	/**
	 * Get the element at the specified column as the primitive type 'long'.
 	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a long.
	 * @throws IllegalStateException If the row no longer exists in the underlying table.
	 */
	long getLong(String label);

	/**
	 * Get the element at the specified column as the primitive type 'float'.
	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a float.
	 * @throws IllegalStateException If the row no longer exists in the underlying table.
	 */
	float getFloat(String label);
	
	/**
	 * Get the element at the specified column as the primitive type 'double'.
 	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a double.
	 * @throws IllegalStateException If the row no longer exists in the underlying table.
	 */
	double getDouble(String label);
	
	/**
	 * Get the element at the specified column as a String.
	 * @throws IllegalStateException If the row no longer exists in the underlying table.
	 */
	public String getString(String label);
	
	// Overridden methods to change javadoc.
	

	/**
	 * Returns true iff the element at the specified column index is numeric.
	 */
	boolean isNumeric(int index);
	
	/**
	 * Get the element at the specified column index.
	 * @throws IllegalStateException If the row no longer exists in the underlying table.
	 */
	Object get(int index);

	/**
	 * Get the element at the specified column index as the primitive type 'boolean'.
	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a boolean.
	 * @throws IllegalStateException If the row no longer exists in the underlying table.
	 */
	boolean getBoolean(int index);

	/**
	 * Get the element at the specified column index as the primitive type 'int'.
	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to an int.
	 * @throws IllegalStateException If the row no longer exists in the underlying table.
	 */
	int getInt(int index);

	/**
	 * Get the element at the specified column index as the primitive type 'long'.
	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a long.
	 * @throws IllegalStateException If the row no longer exists in the underlying table.
	 */
	long getLong(int index);

	/**
	 * Get the element at the specified column index as the primitive type 'float'.
	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a float.
	 * @throws IllegalStateException If the row no longer exists in the underlying table.
	 */
	float getFloat(int index);

	/**
	 * Get the element at the specified column index as the primitive type 'double'.
	 * @throws UnsupportedOperationException If the specified element cannot be
	 * converted to a double.
	 * @throws IllegalStateException If the row no longer exists in the underlying table.
	 */
	double getDouble(int index);

	/**
	 * Get the element at the specified column index as a String.
	 * @throws IllegalStateException If the row no longer exists in the underlying table.
	 */
	public String getString(int index);
}
