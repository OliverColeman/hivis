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

package hivis.data.view;

import hivis.common.Util;
import hivis.data.DataEvent;
import hivis.data.DataSeries;

/**
 * A view over two or more series appended one after the other.
 * 
 * @author O. J. Coleman
 */
public class SeriesViewAppend<V> extends CalcSeries<V, V> {
	private boolean determinedType = false;
	private boolean isNumeric;
	
	/**
	 * Create a new ViewSeriesAppend that appends the given input series in the order given.
	 */
	public SeriesViewAppend(DataSeries<V>... input) {
		super(input);
	}
	
	@Override
	public int length() {
		int length = 0;
		for (DataSeries<V> s : inputSeries) {
			length += s.length();
		}
		return length;
	}
	@Override
	public V getEmptyValue() {
		return getNewSeries().getEmptyValue();
	}
	@Override
	public Class<?> getType() {
		if (!determinedType) {
			type = inputSeries.get(0).getType();
			isNumeric = inputSeries.get(0).isNumeric();
			for (int s = 1; s < inputSeries.size(); s++) {
				Class<?> sType = inputSeries.get(s).getType();
				if (!type.equals(sType)) {
					if (isNumeric && inputSeries.get(s).isNumeric()) {
						type = Util.getEnvelopeNumberType((Class<Number>) type, (Class<Number>) sType, true); 
					}
					else {
						throw new IllegalArgumentException("Cannot append series with incompatible types.");
					}
				}
				
			}
			determinedType = true;
		}
		return type;
	}
	
	@Override
	public boolean isNumeric() {
		if (!determinedType) {
			getType();
		}
		return isNumeric;
	}
	
	@Override
	public void update() {
		int idx = 0;
		for (DataSeries<V> series : inputSeries) {
			for (V val : series) {
				cache.set(idx++, val);
			}
		}
	}
	
//	@Override
//	public V get(int index) {
//		for (int seriesIndex = 0; seriesIndex < inputSeries.size(); seriesIndex++) {
//			int len = inputSeries.get(seriesIndex).length();
//			if (index < len) {
//				V val = inputSeries.get(seriesIndex).get(index);
//				
//				// We cast to the determined numeric type to ensure consistency with getType() 
//				// in the case of the input series being of different numeric types.
//				if (isNumeric) {
//					return (V) type.cast(val);
//				}
//				return val;
//			}
//			index -= len;
//		}
//		return (V) getEmptyValue();
//	}
//	
//	@Override
//	public boolean getBoolean(int index) {
//		for (int seriesIndex = 0; seriesIndex < inputSeries.size(); seriesIndex++) {
//			int len = inputSeries.get(seriesIndex).length();
//			if (index < len) return inputSeries.get(seriesIndex).getBoolean(index);
//			index -= len;
//		}
//		return (boolean) (Boolean) getEmptyValue();
//	}
//
//	@Override
//	public int getInt(int index) {
//		for (int seriesIndex = 0; seriesIndex < inputSeries.size(); seriesIndex++) {
//			int len = inputSeries.get(seriesIndex).length();
//			if (index < len) return inputSeries.get(seriesIndex).getInt(index);
//			index -= len;
//		}
//		return (int) (Integer) getEmptyValue();
//	}
//
//	@Override
//	public long getLong(int index) {
//		for (int seriesIndex = 0; seriesIndex < inputSeries.size(); seriesIndex++) {
//			int len = inputSeries.get(seriesIndex).length();
//			if (index < len) return inputSeries.get(seriesIndex).getLong(index);
//			index -= len;
//		}
//		return (long) (Long) getEmptyValue();
//	}
//
//	@Override
//	public double getDouble(int index) {
//		for (int seriesIndex = 0; seriesIndex < inputSeries.size(); seriesIndex++) {
//			int len = inputSeries.get(seriesIndex).length();
//			if (index < len) return inputSeries.get(seriesIndex).getDouble(index);
//			index -= len;
//		}
//		return (double) (Double) getEmptyValue();
//	}
//
//	@Override
//	public void update(DataEvent cause) {
//		// Nothing to do, view is not cached.
//	}
}
