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


