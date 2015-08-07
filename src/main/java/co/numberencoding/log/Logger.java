/**
 * This is used for logging everything in the system..
 * 
 * Outputstream and error stream could be set using setters.. 
 * closeprev is used to control the closing of previous assigned stream..
 * probably a good candidate of finalize??
 * @author karan dewan
 */
package co.numberencoding.log;

import java.io.IOException;
import java.io.OutputStream;

public class Logger {
	
	//default streams..
	private static OutputStream out;
	private static OutputStream err;
	
	///this should be set through some configuration file..
	//default mode error
	private static Severity mode = Severity.Debug;
	
	
	
	public synchronized static void setOutputStream(OutputStream out, boolean closeprev) {
		
		if(Logger.out != null && closeprev) {
			Logger.closeOut();
		}
		
		Logger.out = out;
	}
	
	public synchronized static void setErrorOutputStream(OutputStream err, boolean closeprev) {
		if(Logger.err != null && closeprev) {
			Logger.closeErr();
		}
		
		Logger.err = err;
	}
	
	
	private static void logWarning(String warn) {
		if(out != null) {
			try {
				Logger.out.write( ("Warning: "+warn).getBytes());
				Logger.out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	private static boolean amILoggable(Severity sev) {
		boolean ret = true;
		int mid = sev.getSeverity();
		int sevid = mode.getSeverity();
		if(mid <= sevid) {
			ret = true;
		}else{
			ret = false;
		}
		
		return ret;
	}
	
	private static void logError(String error) {
		
		if(err != null) {
		
			try {
				Logger.err.write( ("ERROR: "+error).getBytes());
				Logger.err.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static void logDebug(String debug) {
		if(out != null) {
			try {
				Logger.out.write( ("Debug: "+debug).getBytes());
				Logger.out.flush();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void log(String message, Severity sev) {
		
		if(amILoggable(sev)){
			message = message +"\n";
			switch(sev) {
			
				case Error:
					logError(message);
					break;
				case Warning:
					logWarning(message);
					break;
				case Debug:
					logDebug(message);
					break;
			}
			
		}
		
	}

	
	private static void closeOut() {
		
		if(out != null) {
		
			try {
				Logger.out.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	private static void closeErr() {
		if(err != null) {
			try {
				Logger.err.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	//we need to make sure everytime this get called.. when system goes down..
	public static void close() {
		
		Logger.closeOut();
		Logger.closeErr();
		
	}

}
