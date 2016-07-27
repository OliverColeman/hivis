package hivis.data.reader;

/**
 * Indicates an exception occurred while trying to read data from an input source.
 * 
 * @author O. J. Coleman
 */
public class DataReadException extends RuntimeException {
	private static final long serialVersionUID = 1L;

	public DataReadException(String msg) {
		super(msg);
	}
	
	public DataReadException(String msg, Exception source) {
		super(msg, source);
	}
}
