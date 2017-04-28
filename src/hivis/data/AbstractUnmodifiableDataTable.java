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
 * Base class for DataTables that cannot be directly modified.
 * 
 * @author O. J. Coleman
 */
public abstract class AbstractUnmodifiableDataTable<S extends DataSeries<?>> extends AbstractDataTable {
	@Override
	public DataTable addSeries(String label, DataSeries<?> newSeries) {
		throw new UnsupportedOperationException("Can not add a series to unmodifiable table.");
	}

	@Override
	public DataTable addSeries(DataTable table) {
		throw new UnsupportedOperationException("Can not add series to unmodifiable table.");
	}

	@Override
	public DataTable removeSeries(String label) {
		throw new UnsupportedOperationException("Can not remove a series from unmodifiable table.");
	}

	@Override
	public DataTable removeSeries(int index) {
		throw new UnsupportedOperationException("Can not remove a series from unmodifiable table.");
	}
}
