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

import java.util.AbstractList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.locks.ReentrantLock;

import hivis.data.view.CalcSeries;
import hivis.data.view.CollectionView;
import hivis.data.view.SeriesView;
import hivis.common.ListMap;
import hivis.common.Util;
import hivis.data.view.AbstractSeriesViewMultiple;
import hivis.data.view.CalcMap;
import hivis.data.view.SeriesViewAppend;

/**
 * Default base implementation of {@link DataSeries} intended for user-modifiable series implementations.
 * 
 * @author O. J. Coleman
 */
public abstract class AbstractModifiableDataSeries<V> extends AbstractDataSeries<V> {
	@Override
	@SuppressWarnings("unchecked")
	public void set(int index, Object value) {
		try {
			if (isNumeric()) {
				setValue(index, castToNumericType(value));
			}
			else {
				setValue(index, (V) value);
			}
		}
		catch (ClassCastException ex) {
			throw new IllegalArgumentException("The given value of type " + value.getClass().getSimpleName() + " can not be cast to the type stored by " + this.getClass().getSimpleName());
		}
	}
	
	
	@Override
	@SuppressWarnings("unchecked")
	public void append(Object value) {
		try {
			if (isNumeric()) {
				appendValue(castToNumericType(value));
			}
			else {
				appendValue((V) value);
			}
		}
		catch (ClassCastException ex) {
			throw new IllegalArgumentException("The given value of type " + value.getClass().getSimpleName() + " can not be cast to the type stored by " + this.getClass().getSimpleName());
		}
	}
	
	private ReentrantLock lock = new ReentrantLock();
	@Override
	public void lock() {
		lock.lock();
	}
	@Override
	public void unlock() {
		lock.unlock();
	}
}

