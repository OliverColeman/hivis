/**
 * 
 */
package hivis.data.reader;

import hivis.data.DataSet;
import hivis.data.DataSetDefault;

/**
 * Interface for classes that provide a {@link DataSetDefault} from an external source.
 * 
 * @author O. J. Coleman
 */
public interface DataSetSource<D extends DataSet> {
	public D getData();
}
