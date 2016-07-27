package hivis.data;

/**
 * The interface that must be supported by classes that wish to receive notification of changes to a DataSet.
 * 
 * @author O. J. Coleman
 */
public interface DataListener {
	/**
	 * Receives notification of a DataSet change event.
	 */
	public void dataChanged(DataEvent event);
}
