import hivis.common.*;
import hivis.data.*;
import hivis.data.view.*;

// Examples of working with HiVis DataMaps.

void setup() {
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


void draw() {
  
}