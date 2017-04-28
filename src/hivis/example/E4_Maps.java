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
import hivis.data.DataMap;
import hivis.data.DataSeries;

/**
 * Examples of working with {@link DataMap}s.
 * TODO
 * @author O. J. Coleman
 */
public class E4_Maps {
	public static void main(String[] args) {
		// DataMaps represent a mapping from keys to values. 
		// Keys are always unique with respect to each other, values may be duplicated. 
		// The keys and values may be any type of Object, however typically a key will be "simple", such as numbers, 
		// strings, dates, but values may be simple or more complex, for example other data structures such as 
		// DataSeries. (Note that a DataTable is a specialised kind of DataMap where the keys (labels) are Strings 
		// and the values are DataSeries.)
		// DataMaps are currently primarily used to support the grouping functions over DataSeries and DataTables.
		
		// (Advanced Java developers: DataMaps have a generic type parameter for the key and values they represent, 
		// however this can generally be safely ignored. This is done here for readability and simplicity (one of the 
		// core design principles of HiVis). The examples below indicate when type must be taken into consideration.)
		
		// Make a new DataMap.
		DataMap map = HV.newMap();
		
		// Put some mappings into the map.
		map.put("Charlotte", 50);
		map.put("Genevieve", 43);
		
		// We can view the mappings in a map:
		System.out.println("map => " + map);
		// And get some basic information about it:
		System.out.println("map.size() => " + map.size());
		System.out.println("map.containsKey(\"Genevieve\") => " + map.containsKey("Genevieve"));
		
		// Get a view of the keys and values.
		DataSeries keys = map.keys();
		DataSeries values = map.values();
		System.out.println("keys => " + keys);
		System.out.println("values => " + values);
		
		// These views are updated if the map changes:
		map.remove("Genevieve");
		map.put("Charlotte", 51);
		map.put("Roberto", 10);
		map.put("Stefan", 11);
		System.out.println("map (new values) => " + map);
		System.out.println("keys (reflecting new values) => " + keys);
		System.out.println("values (reflecting new values) => " + values);
	}
}
