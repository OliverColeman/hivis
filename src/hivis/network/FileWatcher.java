package hivis.network;

import java.io.File;
import java.util.TimerTask;

public class FileWatcher extends TimerTask {
	  long timeStamp;
	  File file;
	  String path;	  

	  FileWatcher( File _file, String _path) {
	    this.file = _file;
	    this.timeStamp = file.lastModified();
	    this.path = _path;	    
	  }
	@Override
	public void run() {
	    long timeStamp = file.lastModified();

	    if ( this.timeStamp != timeStamp ) {	    	
	      //onChange(file);
	    	this.timeStamp = timeStamp;
	    	NetworkVis.getInstance().readExcel(path);
	  	    NetworkVis.getInstance().drawNetwork();
	  	        	
	    }
	  }
	
	  // load data again
	  void onChange( File file ) {		  
		//NetworkVis instanceOfNetworkVis = NetworkVis.getInstance();  
	    NetworkVis.getInstance().readExcel(path);
	    NetworkVis.getInstance().drawNetwork();
	  }

	}


