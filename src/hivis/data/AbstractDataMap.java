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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.StringJoiner;

import com.google.common.base.Strings;

import hivis.common.LSListMap;
import hivis.common.ListMap;

/**
 * Base class for {@link DataMap} implementations.
 * 
 * @author O. J. Coleman
 */
public abstract class AbstractDataMap<K, V> extends DataDefault implements DataMap<K, V> {
	@Override
	public String toString() {
		ListMap<String, String[]> values = new LSListMap<>();
		
		boolean multiLineValues = false;
		for (K key : keys()) {
			String[] valLines = get(key).toString().split("\n");
			values.put(key.toString(), valLines);
			multiLineValues |= valLines.length > 1;
		}
		
		String sep = multiLineValues ? ",\n\n\t" : ",\n\t";
		
		StringJoiner sj = new StringJoiner(sep, "DataMap (" + size() + ") [ " + (size() > 1 ? "\n\t" : ""), (size() > 1 ? "\n" : "") + " ]");
		
		for (String key : values.keySet()) {
			String[] valLines = values.get(key);
			String keyArrow = key + " => ";
			StringJoiner valSJ = new StringJoiner("\n\t");
			valSJ.add(keyArrow + valLines[0]);
			String keyArrowPad = Strings.repeat(" ", keyArrow.length());
			for (int i = 1; i < valLines.length; i++) {
				valSJ.add(keyArrowPad + valLines[i]);
			}
			sj.add(valSJ.toString());
		}
		
		return sj.toString();
	}
}
