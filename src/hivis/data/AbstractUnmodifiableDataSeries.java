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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.common.reflect.TypeToken;

import hivis.data.view.SeriesView;

/**
 *
 * @author O. J. Coleman
 */
public abstract class AbstractUnmodifiableDataSeries<V> extends AbstractDataSeries<V> implements SeriesView<V> {
	@Override
	public void setValue(int index, V value) {
		throw new UnsupportedOperationException("Can not set values in unmodifiable series.");
	}

	@Override
	public void set(int index, Object value) {
		throw new UnsupportedOperationException("Can not set values in unmodifiable series.");
	}

	@Override
	public void appendValue(V value) {
		throw new UnsupportedOperationException("Can not append values to unmodifiable series.");
	}

	@Override
	public void append(Object value) {
		throw new UnsupportedOperationException("Can not append values to unmodifiable series.");
	}

	@Override
	public void remove(int index) {
		throw new UnsupportedOperationException("Can not remove values from unmodifiable series.");
	}

	@Override
	public void resize(int newLength) {
		throw new UnsupportedOperationException("Can not resize unmodifiable series.");
	}
	
	@Override
	public void resize(int newLength, V padValue) {
		throw new UnsupportedOperationException("Can not resize unmodifiable series.");
	}
}
